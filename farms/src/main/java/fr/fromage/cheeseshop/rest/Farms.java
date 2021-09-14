package fr.fromage.cheeseshop.rest;

import fr.fromage.cheeseshop.model.Cheese;
import fr.fromage.cheeseshop.rest.UpstreamStock.OrderLine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Random;

@Path("farms")
public class Farms {

    @Path("la-belle-vache")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UpstreamStock stockLaBelleVache(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count) {
        delay();
        return getStock(cheese, count, "La Belle Vache");
    }

    @Path("dans-la-cave")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UpstreamStock stockDansLaCave(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count) {
        fault();
        return getStock(cheese, count, "Dans La Cave");
    }

    @Path("la-grange-du-fermier")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UpstreamStock stockLaGrangeDuFermier(@QueryParam("cheese") Cheese cheese, @QueryParam("count") int count) {
        return getStock(cheese, count, "La Grange Du Fermier");
    }

    private UpstreamStock getStock(Cheese cheese, int count, String origin) {
        UpstreamStock order = new UpstreamStock();
        // min price 1 up until resale price  - 1
        order.price = 1 + Math.random() * (cheese.price - 2);
        order.origin = origin;
        // from 0 to count available now
        int availableNow = (int) Math.ceil(Math.random() * (count + 1));
        if(availableNow > 0) {
            OrderLine line1 = new OrderLine();
            line1.count = availableNow;
            order.stock.add(line1);
        }
        // more can be made later
        if(count > availableNow) {
            int delay = (int) Math.ceil(Math.random() * 7);
            LocalDate date = LocalDate.now().plusDays(delay);
            OrderLine line2 = new OrderLine();
            line2.count = count - availableNow;
            line2.availableDate = date;
            order.stock.add(line2);
        }
        // fake that we're slow
        try {
            long sleep = (long)Math.floor(Math.random() * 2000);
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    private final Random random = new Random();

    private void delay() {
        if (random.nextBoolean()) {
            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void fault() {
        if (random.nextBoolean()) {
            throw new RuntimeException("Pas de bras, pas de gouda!");
        }
    }
}
