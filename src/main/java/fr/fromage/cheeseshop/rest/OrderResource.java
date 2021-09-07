package fr.fromage.cheeseshop.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import fr.fromage.cheeseshop.Exceptions;
import fr.fromage.cheeseshop.model.Customer;
import fr.fromage.cheeseshop.model.Order;
import fr.fromage.cheeseshop.model.Stock;
import fr.fromage.cheeseshop.rest.UpstreamStock.OrderLine;
import fr.fromage.cheeseshop.rest.client.BitcoinPrice;
import fr.fromage.cheeseshop.rest.client.FarmDansLaCave;
import fr.fromage.cheeseshop.rest.client.FarmLaBelleVache;
import fr.fromage.cheeseshop.rest.client.FarmLaGrangeDuFermier;

@Path("order")
public class OrderResource {

    @RestClient
    BitcoinPrice bitcoinPrice;

    @RestClient
    FarmLaBelleVache farmLaBelleVache;

    @RestClient
    FarmDansLaCave farmDansLaCave;

    @RestClient
    FarmLaGrangeDuFermier farmLaGrangeDuFermier;

    @Channel("warehouse")
    Emitter<Order> kafkaEmitter;

    @GET
    public List<Order> allOrders() {
        return Order.listAll();
    }

    @POST
    @Transactional
    public Order create(@Valid CreateOrderRequest createOrderRequest) {
        Long customerId = createOrderRequest.customerId;
        Customer customer = Customer.findById(customerId);
        if (customer == null) {
            throw new Exceptions.NoCustomerFound(customerId);
        }
        Order order = toOrder(createOrderRequest, customer);
        order.persist();

        sourceOrder(order);
        
        sendToKafka(order);

        return order;
    }

    private void sourceOrder(Order order) {
        Stock ourStock = Stock.findByCheese(order.type);
        LocalDate availability = null;
        System.err.println("Our stock is "+ourStock.count + " and we need "+order.count);
        if(ourStock.count < order.count) {
            System.err.println(" Asking producers");
            long now = System.nanoTime();
            UpstreamStock laBelleVacheStock = farmLaBelleVache.checkStock(order.type, order.count);
            UpstreamStock laGrangeDuFermierStock = farmLaGrangeDuFermier.checkStock(order.type, order.count);
            UpstreamStock dansLaCaveStock = farmDansLaCave.checkStock(order.type, order.count);
            System.err.println(" Asking producers took " + ((System.nanoTime() - now) / 1_000_000) + "ms");
            List<UpstreamStock> byPrice = new ArrayList<>();
            byPrice.add(laBelleVacheStock);
            byPrice.add(laGrangeDuFermierStock);
            byPrice.add(dansLaCaveStock);
            Collections.sort(byPrice, (a, b) -> Double.compare(a.price, b.price));
            int needed = order.count - ourStock.count;
            // favour cheapest, but prioritise availability
            for(UpstreamStock upstreamStock : byPrice) {
                OrderLine availableNow = upstreamStock.now();
                if(availableNow != null) {
                    int quantity = Math.min(availableNow.count, needed);
                    System.err.println(" Getting "+quantity+" from "+upstreamStock.origin+" NOW");
                    needed -= quantity;
                    if(needed == 0) {
                        break;
                    }
                }
            }
            if(needed > 0) {
                // now find the nearest availability
                List<UpstreamStock> byAvailability = Stream.of(laBelleVacheStock, laGrangeDuFermierStock, dansLaCaveStock)
                        .filter(stock -> stock.future() != null)
                        .sorted((a, b) -> a.future().availableDate.compareTo(b.future().availableDate))
                        .collect(Collectors.toList());
                for(UpstreamStock upstreamStock : byAvailability) {
                    OrderLine future = upstreamStock.future();
                    int quantity = Math.min(future.count, needed);
                    needed -= quantity;
                    System.err.println(" Getting "+quantity+" from "+upstreamStock.origin+" on "+future.availableDate);
                    // remember the later date
                    availability = future.availableDate;
                    if(needed == 0) {
                        break;
                    }
                }
            }
        }
        
        LocalDate d = availability != null ? availability : LocalDate.now();
        // 2 day delivery?
        order.estimatedDelivery = d.plusDays(2);
    }

    private void sendToKafka(Order order) {
        try {
            kafkaEmitter.send(order).toCompletableFuture().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new Exceptions.KafkaException(e);
        }
    }

    private Order toOrder(CreateOrderRequest createOrderRequest, Customer customer) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.customer = customer;
        order.type = createOrderRequest.type;
        order.count = createOrderRequest.count;
        order.timestamp = LocalDateTime.now();
        order.status = Order.Status.Submitted;
        order.priceInBitcoins = bitcoinPrice.get("USD", createOrderRequest.type.getDollarPrice()) * createOrderRequest.count;
        return order;
    }

}
