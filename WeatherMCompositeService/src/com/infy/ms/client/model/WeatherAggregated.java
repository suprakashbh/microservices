package com.infy.ms.client.model;

public class WeatherAggregated {
	
	private Weather weather;
	private ForecastDetails forecast;
	
	public WeatherAggregated(Weather weather,ForecastDetails forecast) {
		this.weather = weather;
		this.forecast = forecast; 
	}
	
	public Weather getWeather() {
		return weather;
	}
	public void setWeather(Weather weather) {
		this.weather = weather;
	}
	public ForecastDetails getForecast() {
		return forecast;
	}
	public void setForecast(ForecastDetails forecast) {
		this.forecast = forecast;
	}
	
	

}
