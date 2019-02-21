package com.rtybase.ml.service.phishingclassification.core.pmml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PmmlInputEvaluatorTest {
	@Mock
	private Evaluator pmmlEvaluator;

	private PmmlInputEvaluator<TestPojo> inputEvaluator;
	private List<InputField> inputFields;

	@Before
	public void set() {
		MockitoAnnotations.initMocks(this);
		generatePmmlInputFields();
		when(pmmlEvaluator.getInputFields()).thenReturn(inputFields);

		inputEvaluator = new PmmlInputEvaluator<>(pmmlEvaluator, TestPojo.class);
	}

	@Test
	public void testInputEvaluation() {
		TestPojo object = new TestPojo();
		object.bigBoolean = true;
		object.bigDouble = 1.0;
		object.bigFloat = 2.0F;
		object.bigInt = 3;
		object.bigLong = 4L;
		object.justString = "test";
		object.notDefinedInPmmlString = "another_test";
		object.nonPmmlString = "5";
		object.smallBoolean = false;
		object.smallDouble = 6.0;
		object.smallFloat = 7.0F;
		object.smallInt = 8;
		object.smallLong = 9L;

		Map<FieldName, FieldValue> result = inputEvaluator.prepareArgumentsFrom(object);
		assertEquals(result.size(), 12);
		assertNull(getFieldNameFor("nullString", result));
		assertNull(getFieldNameFor("nonPmmlString", result));

		verifyInvokedWith("small_boolean", result, false);
		verifyInvokedWith("big_boolean", result, true);
		verifyInvokedWith("small_int", result, 8);
		verifyInvokedWith("big_int", result, 3);
		verifyInvokedWith("small_long", result, 9L);
		verifyInvokedWith("big_long", result, 4L);
		verifyInvokedWith("smallDouble", result, 6.0);
		verifyInvokedWith("bigDouble", result, 1.0);
		verifyInvokedWith("smallFloat", result, 7.0F);
		verifyInvokedWith("bigFloat", result, 2.0F);
		verifyInvokedWith("justString", result, "test");
		verifyInvokedWith("int_from_non_pmml_string", result, 5);

		verifyNeverInvoked("some_crazy_method", result);
		verifyNeverInvoked("return_null_method", result);
	}

	private void generatePmmlInputFields() {
		inputFields = new ArrayList<>();
		inputFields.add(mockInputFieldFor("small_boolean"));
		inputFields.add(mockInputFieldFor("big_boolean"));
		inputFields.add(mockInputFieldFor("small_int"));
		inputFields.add(mockInputFieldFor("big_int"));
		inputFields.add(mockInputFieldFor("small_long"));
		inputFields.add(mockInputFieldFor("big_long"));
		inputFields.add(mockInputFieldFor("smallDouble"));
		inputFields.add(mockInputFieldFor("bigDouble"));
		inputFields.add(mockInputFieldFor("smallFloat"));
		inputFields.add(mockInputFieldFor("bigFloat"));
		inputFields.add(mockInputFieldFor("justString"));
		inputFields.add(mockInputFieldFor("nullString"));
		inputFields.add(mockInputFieldFor("int_from_non_pmml_string"));
		inputFields.add(mockInputFieldFor("some_crazy_method"));
		inputFields.add(mockInputFieldFor("return_null_method"));
	}

	private void verifyNeverInvoked(String inputFieldName, Map<FieldName, FieldValue> fieldMap) {
		assertNull(getFieldNameFor(inputFieldName, fieldMap));
		boolean hit = false;
		for (InputField inputField : inputFields) {
			if (inputFieldName.equals(inputField.getName().getValue())) {
				verify(inputField, never()).prepare(any());
				hit = true;
			}
		}
		assertTrue(hit);
	}

	private void verifyInvokedWith(String inputFieldName, Map<FieldName, FieldValue> fieldMap, Object obj) {
		assertNotNull(getFieldNameFor(inputFieldName, fieldMap));
		boolean hit = false;
		for (InputField inputField : inputFields) {
			if (inputFieldName.equals(inputField.getName().getValue())) {
				verify(inputField).prepare(obj);
				hit = true;
			}
		}
		assertTrue(hit);
	}

	private FieldName getFieldNameFor(String fieldName, Map<FieldName, FieldValue> fieldMap) {
		for (Map.Entry<FieldName, FieldValue> entry : fieldMap.entrySet()) {
			if (fieldName.equals(entry.getKey().getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	private InputField mockInputFieldFor(String name) {
		InputField input = mock(InputField.class);
		FieldValue value = mock(FieldValue.class);

		FieldName field = new FieldName(name);
		when(input.getName()).thenReturn(field);
		when(input.prepare(any())).thenReturn(value);
		return input;
	}

	/* package */ static class TestParentPojo {
		@PmmlProperty(name = "big_boolean")
		public Boolean bigBoolean;
	}

	/* package */ static class TestPojo extends TestParentPojo {
		@PmmlProperty(name = "small_boolean")
		private boolean smallBoolean;

		@PmmlProperty(name = "small_int")
		private int smallInt;
		@PmmlProperty(name = "big_int")
		private Integer bigInt;

		@PmmlProperty(name = "small_long")
		private long smallLong;
		@PmmlProperty(name = "big_long")
		private Long bigLong;

		@PmmlProperty
		private double smallDouble;
		@PmmlProperty
		private Double bigDouble;

		@PmmlProperty
		private float smallFloat;
		@PmmlProperty
		public Float bigFloat;

		@PmmlProperty
		private String justString;

		private String nonPmmlString;
		@PmmlProperty
		private String notDefinedInPmmlString;

		@PmmlProperty
		private String nullString;

		@PmmlProperty(name = "int_from_non_pmml_string")
		public int getIntFromNonPmmlString() {
			return Integer.parseInt(nonPmmlString);
		}

		@PmmlProperty(name = "some_crazy_method")
		public int someCrazyMethod(String someParameter) {
			return 100;
		}

		@PmmlProperty(name = "return_null_method")
		public Integer returnNullMethod() {
			return null;
		}

		@PmmlProperty()
		public int missingNameInAttrinute() {
			return 100;
		}
	}
}
