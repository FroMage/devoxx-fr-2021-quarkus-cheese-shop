package fr.fromage.cheeseshop.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntityBase {

    @Id
    public UUID id;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("customerId")
    @ManyToOne
    public Customer customer;

    public int count;

    public double priceInBitcoins;

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
