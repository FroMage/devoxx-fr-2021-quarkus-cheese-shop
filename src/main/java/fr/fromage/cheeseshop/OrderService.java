package fr.fromage.cheeseshop;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Singleton
public class OrderService {

    private final KafkaProducer<Long, Order> kafkaProducer;
    private final PriceService priceService;

    public OrderService(KafkaProducer<Long, Order> kafkaProducer, PriceService priceService) {
        this.kafkaProducer = kafkaProducer;
        this.priceService = priceService;
    }

    @Transactional
    public Order order(CreateOrderRequest createOrderRequest) {
        Long customerId = createOrderRequest.getCustomerId();
        Customer customer = Customer.findById(customerId);
        if (customer == null) {
            throw new Exceptions.NoCustomerFound(customerId);
        }
        Order order = toOrder(createOrderRequest, customer);
        Order.persist(order);

        sendToKafka(order);

        return order;
    }

    private void sendToKafka(Order order) {
        try {
            kafkaProducer.send(new ProducerRecord<>("cheese-orders", order.id, order)).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new Exceptions.KafkaException(e);
        }
    }

    private Order toOrder(CreateOrderRequest createOrderRequest, Customer customer) {
        Order order = new Order();
        order.customer = customer;
        order.type = createOrderRequest.getType();
        order.count = createOrderRequest.getCount();
        order.timestamp = LocalDateTime.now();
        order.status = Order.Status.Submitted;
        order.princeInBitcoins = priceService.priceInBitcoin(createOrderRequest.getType()) * createOrderRequest.getCount();
        return order;
    }

    @Transactional
    public Order cancel(Long orderId) {
        Order order = Order.findById(orderId);
        if (order == null) {
            throw new Exceptions.NoOrderFound(orderId);
        }
        order.status = Order.Status.Canceled;
        return order;
    }
}
