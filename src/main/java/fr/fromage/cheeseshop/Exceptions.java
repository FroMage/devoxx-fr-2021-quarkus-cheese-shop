package fr.fromage.cheeseshop;

import java.util.UUID;

public final class Exceptions {

    private Exceptions() {
    }

    public static class NoCustomerFound extends RuntimeException {
        private final Long id;

        public NoCustomerFound(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    public static class NoOrderFound extends RuntimeException {

        private final UUID id;

        public NoOrderFound(UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }

    }

    public static class KafkaException extends RuntimeException {

        public KafkaException(Exception cause) {
            super(cause);
        }
    }
}
