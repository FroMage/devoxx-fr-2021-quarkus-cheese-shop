package fr.fromage.cheeseshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class KafkaProviders {

    @Inject
    @Named("default-kafka-broker")
    Map<String, Object> config;

    @Produces
    KafkaConsumer<String, String> getConsumer() {
        return new KafkaConsumer<>(config,
                new StringDeserializer(),
                new StringDeserializer());
    }

    @Produces
    KafkaProducer<UUID, Order> getProducer(ObjectMapper objectMapper) {
        return new KafkaProducer<>(config,
                new UUIDSerializer(),
                new OrderSerializer(objectMapper));
    }

    private static class OrderSerializer implements Serializer<Order> {

        private final ObjectMapper objectMapper;

        OrderSerializer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public byte[] serialize(String topic, Order order) {
            try {
                return objectMapper.writeValueAsBytes(order);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
