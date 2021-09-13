package fr.fromage.cheeseshop.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Stock extends PanacheEntity {
    public int count;
    @Enumerated(EnumType.STRING)
    public Cheese cheese;
    
    public static Stock findByCheese(Cheese cheese) {
        return find("cheese", cheese).singleResult();
    }
}
