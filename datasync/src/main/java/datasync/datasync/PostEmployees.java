package datasync.datasync;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class PostEmployees {
	public static void main(String[] args) {
		CamelContext context = new DefaultCamelContext();
		try {
			context.addRoutes(new RouteBuilder() {
				public void configure() {
					from("jetty:localhost:9000/offers")
					.to("direct:getOffers")
					.end();
				}
			});
			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
