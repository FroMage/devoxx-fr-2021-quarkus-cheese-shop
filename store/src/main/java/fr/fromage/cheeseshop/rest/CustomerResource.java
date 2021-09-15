package fr.fromage.cheeseshop.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.hibernate.reactive.mutiny.Mutiny;

import fr.fromage.cheeseshop.model.Customer;
import io.smallrye.mutiny.Uni;

@Path("customer")
public class CustomerResource {
    @GET
    @Path("{id}")
    public Uni<Customer> get(Long id) {
        return Customer.<Customer>findById(id)
                .call(customer -> Mutiny.fetch(customer.orders));
    }
}
