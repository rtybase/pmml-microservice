package com.rtybase.ml.service.phishingclassification.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.rtybase.ml.service.phishingclassification.core.JsonStringifier;

public class JsonStringifierTest {

	@Test
	public void testAsStringWithNull() {
		assertNull(JsonStringifier.asString(null));
	}

	@Test
	public void testAsString() {
		Map<String, String> map = new TreeMap<>();
		map.put("a", "1");
		map.put("b", "2");
		assertEquals(JsonStringifier.asString(map), "{\"a\":\"1\",\"b\":\"2\"}");
	}
}
