package com.rtybase.ml.service.phishingclassification.core;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtybase.ml.service.phishingclassification.core.pmml.PmmlProperty;

public class UrlResource {

	@PmmlProperty(name = "x1")
	@JsonProperty
	@Max(1)
	@Min(-1)
	private int urlLength;

	public int getUrlLength() {
		return urlLength;
	}

	public void setUrlLength(int urlLength) {
		this.urlLength = urlLength;
	}

	@Override
	public String toString() {
		return JsonStringifier.asString(this);
	}
}
