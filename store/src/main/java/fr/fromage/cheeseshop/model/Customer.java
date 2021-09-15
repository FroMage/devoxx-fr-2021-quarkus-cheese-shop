package fr.fromage.cheeseshop.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntity {

    @Column
    public String firstName;

    @Column
    public String lastName;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<Order> orders;

}
