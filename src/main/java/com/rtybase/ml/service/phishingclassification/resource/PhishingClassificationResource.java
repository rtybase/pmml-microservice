package com.rtybase.ml.service.phishingclassification.resource;

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.rtybase.ml.service.phishingclassification.core.UrlResource;
import com.rtybase.ml.service.phishingclassification.core.pmml.ScoreCalculator;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(PhishingClassificationResource.PATH)
public class PhishingClassificationResource {
	static final String PATH = "/v1/classifier/phishing";

	private final ScoreCalculator<UrlResource> phishingClassifier;

	public PhishingClassificationResource(ScoreCalculator<UrlResource> phishingClassifier) {
		this.phishingClassifier = Objects.requireNonNull(phishingClassifier, "phishingClassifier must not be null!");
	}

	@POST
	@Timed
	public Map<String, ?> classifyResource(@Valid UrlResource urlResource) {
		return phishingClassifier.calculate(urlResource);
	}
}
