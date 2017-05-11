package datasync.datasync;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class GetEmployees {
	public static void main(String[] args) {
		CamelContext context = new DefaultCamelContext();

		try {
			context.addRoutes(new RouteBuilder(){
			     public void configure(){
			          from("direct:getOffers")
			          .to("jetty:http://localhost:9008/api/v2.0/offers?bridgeEndpoint=true")
			         .end();
			     }
			});
			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
