package fr.fromage.cheeseshop;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class OrderSerializer extends ObjectMapperSerializer<Order> {
    public OrderSerializer() {
        super();
    }
}