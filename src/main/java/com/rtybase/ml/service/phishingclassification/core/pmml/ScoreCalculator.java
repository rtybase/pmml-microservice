package com.rtybase.ml.service.phishingclassification.core.pmml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.PMMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreCalculator<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCalculator.class);

	private final String pmmlFilePath;
	private final Evaluator evaluator;
	private final PmmlInputEvaluator<T> inputEvaluator;

	public ScoreCalculator(String pmmlFilePath, Class<T> klass) throws Exception {
		this.pmmlFilePath = Objects.requireNonNull(pmmlFilePath, "pmmlFilePath must not be null!");
		Objects.requireNonNull(klass, "klass must not be null!");

		this.evaluator = buildEvaluator();
		this.inputEvaluator = new PmmlInputEvaluator<>(evaluator, klass);
	}

	public String getPmmlFilePath() {
		return pmmlFilePath;
	}

	public Map<String, ?> calculate(T obj) {
		Objects.requireNonNull(obj, "obj must not be null!");

		Map<FieldName, FieldValue> arguments = inputEvaluator.prepareArgumentsFrom(obj);
		Map<FieldName, ?> evaluationResult = evaluator.evaluate(arguments);
		return EvaluatorUtil.decodeAll(evaluationResult);
	}

	private Evaluator buildEvaluator() throws Exception {
		try (InputStream is = new FileInputStream(pmmlFilePath)) {
			PMML pmml = PMMLUtil.unmarshal(is);
			ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
			ModelEvaluator<?> modelEvaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
			return modelEvaluator;
		} catch (Exception ex) {
			LOGGER.error("Failed to load the model from '{}'", pmmlFilePath, ex);
			throw ex;
		}
	}
}
