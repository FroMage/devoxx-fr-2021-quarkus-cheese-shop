package fr.fromage.cheeseshop;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotNull;

public class CreateOrderRequest {

    @NotNull
    private final Long customerId;
    @NotNull
    private final Cheese type;
    private final int count;

    @JsonCreator
    public CreateOrderRequest(Long customerId, Cheese type, int count) {
        this.customerId = customerId;
        this.type = type;
        this.count = count;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Cheese getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
