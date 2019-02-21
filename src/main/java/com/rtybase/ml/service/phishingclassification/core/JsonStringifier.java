package com.rtybase.ml.service.phishingclassification.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringifier {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String asString(Object o) {
		if (o != null) {
			try {
				return OBJECT_MAPPER.writeValueAsString(o);
			} catch (JsonProcessingException ex) {
				return o.getClass().toString();
			}
		}
		return null;
	}
}
