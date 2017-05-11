package datasync.datasync;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class MyFirstCamelRoute {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		RouteBuilder builder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				from("timer:kickoff?fixedRate=true&delay=0&period=30000")
				.log(LoggingLevel.DEBUG, "Triggered here ");
//						.setHeader("AccessToken",
//								constant("y32O919rSTVgDxlj3Ne/lvQM0/wtfSm06u4G1r9d1oK85rvElX1NkMIPCFTlRmw9"))
//						.to("http://devsandbox.targetsolutions.com/v1/users/1339145").log("${body}");
			}
		};
		context.addRoutes(builder);

		context.start();

//		Main main = new Main();
//		main.addRouteBuilder(builder);
//		main.run();
	}

//	private void restConfiguration(CamelContext context) {
//		RestConfiguration restConfiguration = new RestConfiguration();
//		restConfiguration.setComponent("servlet");
//		restConfiguration.setBindingMode(RestConfiguration.RestBindingMode.json);
//		restConfiguration.setHost("localhost");
//		restConfiguration.setPort(serverPort);
//
//		context.setRestConfiguration(restConfiguration);
//	}

}
