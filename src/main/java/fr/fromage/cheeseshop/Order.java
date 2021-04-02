package fr.fromage.cheeseshop;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("customerId")
    @ManyToOne
    public Customer customer;

    public int count;

    public double princeInBitcoins;

    public Cheese type;

    public LocalDateTime timestamp;

    public Status status;

    enum Status {
        Submitted,
        Shipped,
        Canceled
    }
}
