package org.wpb.integration.tssync.utils;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("myresource")
public class TargetSolutionsResource {

	public void init() {
		
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
        return "Got it!";
    }

	public static void main(String[] args) {
		System.out.println("test");
	}

}
