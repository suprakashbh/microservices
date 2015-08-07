package com.infy.ms.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ForecastDetails {
	
	private String dt;
	private String dt_txt;
	private List<FWeather> weather;
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getDt_txt() {
		return dt_txt;
	}
	public void setDt_txt(String dt_txt) {
		this.dt_txt = dt_txt;
	}
	public List<FWeather> getWeather() {
		return weather;
	}
	public void setWeather(List<FWeather> weather) {
		this.weather = weather;
	}
	
	

}
