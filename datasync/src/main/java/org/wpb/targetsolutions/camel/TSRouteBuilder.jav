package org.wpb.targetsolutions.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class TSRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("direct:inPojoAgeView").marshal().json(JsonLibrary.Jackson);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
