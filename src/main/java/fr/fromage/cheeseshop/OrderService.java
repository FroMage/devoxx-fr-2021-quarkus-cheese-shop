package fr.fromage.cheeseshop;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Singleton
public class OrderService {

    @Transactional
    public Order order(CreateOrderRequest createOrderRequest) {
        Long customerId = createOrderRequest.getCustomerId();
        Customer customer = Customer.findById(customerId);
        if (customer == null) {
            throw new Exceptions.NoCustomerFound(customerId);
        }
        Order order = toOrder(createOrderRequest, customer);
        Order.persist(order);
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

    private Order toOrder(CreateOrderRequest createOrderRequest, Customer customer) {
        Order order = new Order();
        order.customer = customer;
        order.type = createOrderRequest.getType();
        order.count = createOrderRequest.getCount();
        order.timestamp = LocalDateTime.now();
        order.status = Order.Status.Submitted;
        return order;
    }
}
