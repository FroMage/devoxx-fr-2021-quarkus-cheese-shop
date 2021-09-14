package fr.fromage.cheeseshop.rest.client;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
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
import java.util.Collections;

@Path("farms/dans-la-cave")
@RegisterRestClient
public interface FarmDansLaCave {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 4, delay = 1000)
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackCheckStock")
    Uni<UpstreamStock> checkStock(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    UpstreamStock checkStockBlocking(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count);


    default Uni<UpstreamStock> fallbackCheckStock(Cheese cheese, int count) {
        UpstreamStock stock = new UpstreamStock();
        stock.origin = "Dans la cave";
        stock.price = 0.0;
        stock.stock = Collections.emptyList();
        return Uni.createFrom().item(stock);
    }
}
