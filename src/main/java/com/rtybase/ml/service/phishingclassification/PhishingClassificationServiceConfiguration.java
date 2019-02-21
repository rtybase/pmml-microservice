package com.rtybase.ml.service.phishingclassification;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PhishingClassificationServiceConfiguration extends Configuration {
	@NotEmpty
	@JsonProperty
	private String pmmlFilePath;

	public String getPmmlFilePath() {
		return pmmlFilePath;
	}
}
