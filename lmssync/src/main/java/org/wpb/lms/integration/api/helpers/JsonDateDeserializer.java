package org.wpb.lms.integration.api.helpers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 
 * Used to deserialize Java.util.Date, which is not a common JSON type, so we
 * have to create a custom deserialize method;.
 *
 * @author Abhilash Juluri
 */

public class JsonDateDeserializer extends JsonDeserializer<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String date = p.getText();
			try {
				return dateFormat.parse(date);
			} catch (ParseException e) {
				return null;
			}
	}
	
// ******** Sample code *********
//	@JsonSerialize(using = JsonDateSerializer.class)
//	public Date getEffective_hire() {
//		return effective_hire;
//	}
//
//	@JsonDeserialize(using = JsonDateDeserializer.class)
//	public void setEffective_hire(Date effective_hire) {
//		this.effective_hire = effective_hire;
//	}


}