package com.rtybase.ml.service.phishingclassification.core.pmml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/*package */ class PmmlInputEvaluator<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PmmlInputEvaluator.class);

	private final Set<Field> fields;
	private final Set<Method> methods;
	private final Map<String, InputField> pmmlInputFileds;

	/* package */ PmmlInputEvaluator(Evaluator evaluator, Class<T> klass) {
		Objects.requireNonNull(evaluator, "evaluator must not be null!");
		Objects.requireNonNull(klass, "klass must not be null!");

		this.fields = collectClassFields(klass);
		this.methods = collectClassMethods(klass);
		this.pmmlInputFileds = collectPmmlInputFields(evaluator);
	}

	public Map<FieldName, FieldValue> prepareArgumentsFrom(T object) {
		Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

		collectInputsFromFields(arguments, object);
		collectInputsFromMethods(arguments, object);
		return arguments;
	}

	private void collectInputsFromFields(Map<FieldName, FieldValue> result, T object) {
		fields.forEach(f -> {
			PmmlProperty annotation = f.getAnnotation(PmmlProperty.class);
			final String propertyName = Strings.isNullOrEmpty(annotation.name()) ? f.getName() : annotation.name();

			try {
				Object value;

				if (!f.isAccessible()) {
					f.setAccessible(true);
					value = f.get(object);
					f.setAccessible(false);
				} else {
					value = f.get(object);
				}

				if (value != null) {
					addPropertyAndValueToMap(propertyName, value, result);
				}
			} catch (Exception e) {
				LOGGER.error("Failed to collect input values from fields!", e);
			}
		});
	}

	private void collectInputsFromMethods(Map<FieldName, FieldValue> result, T object) {
		methods.forEach(m -> {
			PmmlProperty annotation = m.getAnnotation(PmmlProperty.class);
			final String propertyName = annotation.name();
			if (!Strings.isNullOrEmpty(propertyName)) {
				try {
					Object value;

					if (!m.isAccessible()) {
						m.setAccessible(true);
						value = m.invoke(object);
						m.setAccessible(false);
					} else {
						value = m.invoke(object);
					}

					if (value != null) {
						addPropertyAndValueToMap(propertyName, value, result);
					}
				} catch (Exception e) {
					LOGGER.error("Failed to collect input values from methods!", e);
				}
			}
		});
	}

	private static Map<String, InputField> collectPmmlInputFields(Evaluator evaluator) {
		final Map<String, InputField> result = new HashMap<>();
		List<InputField> inputFields = evaluator.getInputFields();
		inputFields.stream().forEach(inputField -> {
			result.put(inputField.getName().getValue(), inputField);
		});
		return result;
	}

	private static <T> Set<Field> collectClassFields(Class<T> klass) {
		final Set<Field> fields = new HashSet<>();
		fields.addAll(Arrays.asList(klass.getFields()));
		fields.addAll(Arrays.asList(klass.getDeclaredFields()));
		return fields.stream().filter(f -> f.isAnnotationPresent(PmmlProperty.class)).collect(Collectors.toSet());
	}

	private static <T> Set<Method> collectClassMethods(Class<T> klass) {
		Set<Method> methods = new HashSet<>();
		methods.addAll(Arrays.asList(klass.getMethods()));
		methods.addAll(Arrays.asList(klass.getDeclaredMethods()));
		return methods.stream().filter(m -> m.isAnnotationPresent(PmmlProperty.class) && m.getParameterCount() == 0)
				.collect(Collectors.toSet());
	}

	private void addPropertyAndValueToMap(final String propertyName, Object value, Map<FieldName, FieldValue> result) {
		InputField pmmlInputField = pmmlInputFileds.get(propertyName);
		if (pmmlInputField != null) {
			FieldName inputFieldName = pmmlInputField.getName();
			FieldValue inputFieldValue = pmmlInputField.prepare(value);
			result.put(inputFieldName, inputFieldValue);
		}
	}
}
