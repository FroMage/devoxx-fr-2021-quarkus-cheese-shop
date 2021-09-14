package fr.fromage.cheeseshop.kafka;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ShippedOrderDeserializer extends ObjectMapperDeserializer<ShippedOrder> {
    public ShippedOrderDeserializer() {
        super(ShippedOrder.class);
    }
}
