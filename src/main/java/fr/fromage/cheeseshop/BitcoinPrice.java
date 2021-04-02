package fr.fromage.cheeseshop;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("tobtc")
@RegisterRestClient
public interface BitcoinPrice {

    @GET
    @Produces("text/plain")
    double get(@QueryParam("currency") String currency, @QueryParam("value") int value);
}
