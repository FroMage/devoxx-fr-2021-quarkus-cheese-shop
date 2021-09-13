package fr.fromage.cheeseshop.rest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.fromage.cheeseshop.model.Cheese;

public class CreateOrderRequest {

    @NotNull
    public Long customerId;
    @NotNull
    public Cheese type;
    @Min(1)
    public int count;
}
