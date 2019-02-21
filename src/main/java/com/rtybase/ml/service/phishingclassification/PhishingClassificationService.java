package com.rtybase.ml.service.phishingclassification;

import com.rtybase.ml.service.phishingclassification.core.UrlResource;
import com.rtybase.ml.service.phishingclassification.core.pmml.ScoreCalculator;
import com.rtybase.ml.service.phishingclassification.health.PhishingClassificationHealthcheck;
import com.rtybase.ml.service.phishingclassification.resource.PhishingClassificationResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PhishingClassificationService extends Application<PhishingClassificationServiceConfiguration> {
	public static void main(String[] args) throws Exception {
		new PhishingClassificationService().run(args);
	}

	@Override
	public String getName() {
		return "phishing-classification-service";
	}

	@Override
	public void initialize(Bootstrap<PhishingClassificationServiceConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void run(PhishingClassificationServiceConfiguration configuration, Environment environment)
			throws Exception {
		final ScoreCalculator<UrlResource> phishingClassifier = new ScoreCalculator<>(
				configuration.getPmmlFilePath(),
				UrlResource.class);
		registerHealthcheck(environment, phishingClassifier);
		registerResource(environment, phishingClassifier);
	}

	private void registerHealthcheck(Environment environment, final ScoreCalculator<UrlResource> phishingClassifier) {
		environment.healthChecks().register("classificationModel",
				new PhishingClassificationHealthcheck(phishingClassifier));
	}

	private void registerResource(Environment environment, final ScoreCalculator<UrlResource> phishingClassifier) {
		environment.jersey().register(new PhishingClassificationResource(phishingClassifier));
	}
}
