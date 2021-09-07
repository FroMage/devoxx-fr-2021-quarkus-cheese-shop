package fr.fromage.cheeseshop.rest.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import fr.fromage.cheeseshop.model.Cheese;
import fr.fromage.cheeseshop.rest.UpstreamStock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("farms/la-grange-du-fermier")
@RegisterRestClient
public interface FarmLaGrangeDuFermier {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    UpstreamStock checkStock(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count);
}
