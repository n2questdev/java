package org.wpb.lms.integration.api.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
	public static final String PROPERTIES_FILE = "application.properties";
	
	private static Properties readProperties() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(PROPERTIES_FILE);
		Properties properties = new Properties();
		properties.load(is);
		return properties;
	}

	public static String getAccessToken() throws IOException {
		return (String) readProperties().get("accesstoken");
	}
	
	public static String getSiteURI() throws IOException {
		return (String) readProperties().getProperty("site");
	}
	
	public static String getSiteID() throws IOException {
		return (String) readProperties().getProperty("siteID");
	}
}
