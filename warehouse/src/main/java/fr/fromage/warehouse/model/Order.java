package fr.fromage.warehouse.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

    public UUID id;

    public long customerId;

    public int count;

    public double price;

    public Cheese type;

    public LocalDateTime timestamp;

    public Status status;

    public LocalDate estimatedDelivery;

    public enum Status {
        Submitted,
        Shipped,
        Canceled
    }
}
