package datasync.datasync;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/customerservice/")
public class CustomerServiceResource {

    // NOTE: The instance member variables will not be available to the
    // Camel Exchange. They must be used as method parameters for them to
    // be made available
    @Context
    private UriInfo uriInfo;

    public CustomerServiceResource() {
    }

    @GET
    @Path("/customers/{id}/")
    @Produces("text/json")
    public Customer getCustomer(@PathParam("id") String id) {
        return null;
    }

    @PUT
    @Path("/customers/")
    public Response updateCustomer(Customer customer) {
        return null;
    }
}