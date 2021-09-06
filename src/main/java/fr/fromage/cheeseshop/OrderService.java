package fr.fromage.cheeseshop;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class OrderService {

//    private final KafkaProducer<UUID, Order> kafkaProducer;
    private final BitcoinPrice bitcoinPrice;
    @Inject
    @Channel("warehouse")
    Emitter<Order> kafkaEmitter;


    public OrderService(/*KafkaProducer<UUID, Order> kafkaProducer, */@RestClient BitcoinPrice bitcoinPrice) {
//        this.kafkaProducer = kafkaProducer;
        this.bitcoinPrice = bitcoinPrice;
    }

    @Transactional
    public Order order(CreateOrderRequest createOrderRequest) {
        Long customerId = createOrderRequest.getCustomerId();
        Customer customer = Customer.findById(customerId);
        if (customer == null) {
            throw new Exceptions.NoCustomerFound(customerId);
        }
        Order order = toOrder(createOrderRequest, customer);
        order.persist();

        sendToKafka(order);

        return order;
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
        order.type = createOrderRequest.getType();
        order.count = createOrderRequest.getCount();
        order.timestamp = LocalDateTime.now();
        order.status = Order.Status.Submitted;
        order.princeInBitcoins = bitcoinPrice.get("USD", createOrderRequest.getType().getDollarPrice()) * createOrderRequest.getCount();
        return order;
    }

}
