package fr.fromage.cheeseshop.kafka;

import fr.fromage.cheeseshop.model.Order;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Tracking {

    @Incoming("shipped-orders")
    public Uni<Void> shipped(ShippedOrder shipped) {
        System.out.println("Got an order shipped: " + shipped.id);
        return Panache.withTransaction(() ->
                Order.<Order>findById(shipped.id)
                        .log()
                        .onItem().ifNotNull().transformToUni(o -> {
                            o.status = Order.Status.Shipped;
                            return o.persist();
                        })
                        .replaceWithVoid());
    }


}
