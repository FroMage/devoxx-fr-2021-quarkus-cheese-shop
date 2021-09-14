package fr.fromage.warehouse.kafka;

import fr.fromage.warehouse.model.Order;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Random;

@ApplicationScoped
public class Logistic {

    private final Random random = new Random();

    @Incoming("new-orders")
    @Outgoing("shipped-orders")
    @Broadcast
    public Uni<Order> shipping(Order order) {
        System.out.println("Shipping order " + order.id);
        return Uni.createFrom().item(order)
                .onItem().delayIt().by(Duration.ofSeconds(random.nextInt(3) + 1))
                .invoke(o -> o.status = Order.Status.Shipped);
    }


    @Incoming("shipped-orders")
    @Blocking
    public void delivered(Order order) throws InterruptedException {
        Thread.sleep(random.nextInt(5000) + 1);
        System.out.println("Order " + order.id + " containing " + order.count + " of " + order.type + " has been delivered to customer " + order.customerId);
    }

}
