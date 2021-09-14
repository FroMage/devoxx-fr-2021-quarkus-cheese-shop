package fr.fromage.cheeseshop.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import io.quarkus.hibernate.reactive.panache.Panache;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import fr.fromage.cheeseshop.Exceptions;
import fr.fromage.cheeseshop.model.Customer;
import fr.fromage.cheeseshop.model.Order;
import fr.fromage.cheeseshop.model.Stock;
import fr.fromage.cheeseshop.rest.UpstreamStock.OrderLine;
import fr.fromage.cheeseshop.rest.client.FarmDansLaCave;
import fr.fromage.cheeseshop.rest.client.FarmLaBelleVache;
import fr.fromage.cheeseshop.rest.client.FarmLaGrangeDuFermier;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;

@Path("order")
public class OrderResource {

    @RestClient
    FarmLaBelleVache farmLaBelleVache;

    @RestClient
    FarmDansLaCave farmDansLaCave;

    @RestClient
    FarmLaGrangeDuFermier farmLaGrangeDuFermier;

    @Channel("warehouse")
    MutinyEmitter<Order> kafkaEmitter;

    @GET
    public Uni<List<Order>> allOrders() {
        return Order.listAll();
    }

    @POST
    public Uni<Order> create(@Valid CreateOrderRequest createOrderRequest) {
        System.err.println("Initial request on "+Thread.currentThread());
        Long customerId = createOrderRequest.customerId;
        Uni<Customer> customerUni = Customer.findById(customerId);
        return customerUni
                .onItem().ifNull().failWith(() -> new Exceptions.NoCustomerFound(customerId))
                .onItem().transform(customer -> toOrder(createOrderRequest, customer))
                .call(order -> Panache.withTransaction(() -> {
                    return order.persist()
                            .replaceWith(order);
                }))
                .call(o -> sourceOrder(o))
                .call(o -> sendToKafka(o));
    }

    private Uni<?> sourceOrder(Order order) {
        Uni<Stock> ourStockUni = Stock.findByCheese(order.type);
        return ourStockUni.chain(ourStock -> {
            System.err.println("Our stock for "+order.type+" is "+ourStock.count + " and we need "+order.count);
            if(ourStock.count < order.count) {
                System.err.println(" Asking producers from thread "+Thread.currentThread());
                long now = System.nanoTime();
                Uni<Stream<UpstreamStock>> producerStockUni = queryProducers(order);
                return producerStockUni.chain(producerStock -> {
                    System.err.println(" Asking producers took " + ((System.nanoTime() - now) / 1_000_000) + "ms on "+Thread.currentThread());
                    List<UpstreamStock> byPrice = producerStock.sorted((a, b) -> Double.compare(a.price, b.price))
                            .collect(Collectors.toList());
                    LocalDate availability = null;
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
                        List<UpstreamStock> byAvailability = producerStock
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
                    return Uni.createFrom().item(availability);
                });
            } else {
                return Uni.createFrom().nullItem();
            }
            // 2 day delivery?
        })
                .onItem().ifNull().continueWith(() -> LocalDate.now())
                .invoke(availability -> order.estimatedDelivery = availability.plusDays(2));
    }

    private Uni<Stream<UpstreamStock>> queryProducers(Order order) {
        Uni<UpstreamStock> laBelleVacheStock = time(farmLaBelleVache.checkStock(order.type, order.count));
        Uni<UpstreamStock> laGrangeDuFermierStock = time(farmLaGrangeDuFermier.checkStock(order.type, order.count));
        Uni<UpstreamStock> dansLaCaveStock = time(farmDansLaCave.checkStock(order.type, order.count));
        return Uni.join().all(laBelleVacheStock, laGrangeDuFermierStock, dansLaCaveStock)
                .andFailFast()
                .onItem().transform(list -> list.stream());
    }

    private Uni<UpstreamStock> time(Uni<UpstreamStock> uni) {
        long now = System.nanoTime();
        return uni.invoke(res -> System.err.println("  Client result from "+res.origin+" took "+((System.nanoTime()-now) / 1_000_000)+"ms on thread "+Thread.currentThread()));
    }

    private Uni<?> sendToKafka(Order order) {
        return kafkaEmitter.send(order)
            .onFailure(Exception.class).transform(e -> new Exceptions.KafkaException(e));
    }

    private Order toOrder(CreateOrderRequest createOrderRequest, Customer customer) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.customer = customer;
        order.type = createOrderRequest.type;
        order.count = createOrderRequest.count;
        order.timestamp = LocalDateTime.now();
        order.status = Order.Status.Submitted;
        order.price = createOrderRequest.type.price;
        return order;
    }

}
