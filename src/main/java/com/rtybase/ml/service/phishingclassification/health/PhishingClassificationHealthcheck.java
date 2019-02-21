package com.rtybase.ml.service.phishingclassification.health;

import java.util.Objects;

import com.codahale.metrics.health.HealthCheck;
import com.rtybase.ml.service.phishingclassification.core.UrlResource;
import com.rtybase.ml.service.phishingclassification.core.pmml.ScoreCalculator;

public class PhishingClassificationHealthcheck extends HealthCheck {
	private final ScoreCalculator<UrlResource> phishingClassifier;

	public PhishingClassificationHealthcheck(ScoreCalculator<UrlResource> phishingClassifier) {
		this.phishingClassifier = Objects.requireNonNull(phishingClassifier, "phishingClassifier must not be null!");
	}

	@Override
	protected Result check() throws Exception {
		return Result.healthy("Model file used '%s'", phishingClassifier.getPmmlFilePath());
	}
}