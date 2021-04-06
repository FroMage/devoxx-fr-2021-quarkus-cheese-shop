package fr.fromage.cheeseshop;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.UUID;

@Path("order")
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
    public List<Order> allOrders() {
        return Order.listAll();
    }

    @POST
    public Order create(@Valid CreateOrderRequest createOrderRequest) {
        return orderService.order(createOrderRequest);
    }

    @Path("cancel/{orderId}")
    @POST
    public Order cancel(@PathParam("orderId") String orderId) {
        return orderService.cancel(UUID.fromString(orderId));
    }
}
