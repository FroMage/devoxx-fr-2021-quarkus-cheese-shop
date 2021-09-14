package fr.fromage.cheeseshop.rest.client;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import fr.fromage.cheeseshop.model.Cheese;
import fr.fromage.cheeseshop.rest.UpstreamStock;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("farms/la-belle-vache")
@RegisterRestClient
public interface FarmLaBelleVache {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CircuitBreaker
    @Timeout(5000)
    Uni<UpstreamStock> checkStock(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    UpstreamStock checkStockBlocking(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count);
}
