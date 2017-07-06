package org.wpb.lms.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonEmailDeserializer extends JsonDeserializer<List<Email>> {
	@Override
	public List<Email> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = p.getText();
		return value.isEmpty() ? new ArrayList<Email>() : null;

	}
}
