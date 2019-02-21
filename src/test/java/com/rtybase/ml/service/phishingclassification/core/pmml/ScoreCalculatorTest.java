package com.rtybase.ml.service.phishingclassification.core.pmml;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.rtybase.ml.service.phishingclassification.core.UrlResource;

public class ScoreCalculatorTest {
	private static final String PMML_FILE = "src/main/resources/model/phishing-random-forest.pmml";

	private ScoreCalculator<UrlResource> calculator;

	@Before
	public void set() throws Exception {
		calculator = new ScoreCalculator<>(PMML_FILE, UrlResource.class);
	}

	@Test(expected = NullPointerException.class)
	public void testWithNull() {
		calculator.calculate(null);
	}

	@Test
	public void testPath() {
		assertEquals(calculator.getPmmlFilePath(), PMML_FILE);
	}

	@Test
	public void testDeserialisation() throws Exception {
		Map<String, ?> result = calculator.calculate(new UrlResource());
		assertEquals(result.get("y"), 1);
		assertEquals(result.get("probability_0"), 0.4459829535601516);
		assertEquals(result.get("probability_1"), 0.5540170464398483);
	}
}
