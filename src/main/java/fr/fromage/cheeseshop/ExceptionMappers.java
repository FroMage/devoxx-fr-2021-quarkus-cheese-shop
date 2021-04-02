package fr.fromage.cheeseshop;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public final class ExceptionMappers {

    private ExceptionMappers() {
    }

    @Provider
    public static class NoCustomerFoundExceptionMapper implements ExceptionMapper<Exceptions.NoCustomerFound> {
        @Override
        public Response toResponse(Exceptions.NoCustomerFound e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer " + e.getId() + " not found")
                    .header("Content-Type", "text/plain")
                    .build();
        }
    }

    @Provider
    public static class NoOrderFoundExceptionMapper implements ExceptionMapper<Exceptions.NoOrderFound> {
        @Override
        public Response toResponse(Exceptions.NoOrderFound e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Order " + e.getId() + " not found")
                    .header("Content-Type", "text/plain")
                    .build();
        }
    }
}
