package com.infy.ms.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Forecast {
	
	private List<ForecastDetails> list;

	public List<ForecastDetails> getList() {
		return list;
	}

	public void setList(List<ForecastDetails> list) {
		this.list = list;
	}
	
	

}
