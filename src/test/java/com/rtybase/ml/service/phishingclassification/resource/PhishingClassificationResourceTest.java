package com.rtybase.ml.service.phishingclassification.resource;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.rtybase.ml.service.phishingclassification.core.UrlResource;
import com.rtybase.ml.service.phishingclassification.core.pmml.ScoreCalculator;
import com.rtybase.ml.service.phishingclassification.resource.PhishingClassificationResource;

import io.dropwizard.testing.junit.ResourceTestRule;

public class PhishingClassificationResourceTest {
	private static final Map<String, Object> RESULT = new HashMap<>();

	static {
		RESULT.put("y", 1);
		RESULT.put("probability_0", 0.1);
		RESULT.put("probability_1", 0.9);
	}

	@SuppressWarnings("unchecked")
	private ScoreCalculator<UrlResource> phishingClassifier = mock(ScoreCalculator.class);

	@Rule
	public final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(new PhishingClassificationResource(phishingClassifier)).build();

	@Before
	public void setup() throws Exception {
		doReturn(RESULT).when(phishingClassifier).calculate(any(UrlResource.class));
	}

	@Test
	public void testEvaluation() {
		Map<String, Object> map = resources.target(PhishingClassificationResource.PATH).request()
				.post(Entity.json(new UrlResource()), new GenericType<Map<String, Object>>() {
				});
		assertEquals(map, RESULT);
	}

	@Test
	public void testEvaluationWithInvalidInput() {
		UrlResource input = new UrlResource();
		input.setUrlLength(-2);

		try {
			resources.target(PhishingClassificationResource.PATH).request().post(Entity.json(input),
					new GenericType<Map<String, Object>>() {
					});
			fail("Should have not reached this line!");
		} catch (ClientErrorException ex) {
			assertEquals(ex.getResponse().getStatus(), 422);
		}
	}
}
