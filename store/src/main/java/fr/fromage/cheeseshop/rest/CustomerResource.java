package fr.fromage.cheeseshop.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import fr.fromage.cheeseshop.model.Customer;

@Path("customer")
public class CustomerResource {
    @GET
    @Path("{id}")
    public Customer get(@PathParam Long id) {
        return Customer.findById(id);
    }
}