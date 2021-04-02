package fr.fromage.cheeseshop;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("customer")
public class CustomerResource {

    @GET
    public List<Customer> findAll() {
        return Customer.listAll();
    }
}
