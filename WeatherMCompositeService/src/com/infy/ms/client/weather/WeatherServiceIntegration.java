package com.infy.ms.client.weather;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.infy.ms.client.model.Forecast;
import com.infy.ms.client.model.Weather;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component("integration")
public class WeatherServiceIntegration {
	

	@Autowired
	private LoadBalancerClient loadBalancer;
	
	RestTemplate restTemplate = new RestTemplate();	

	
	@HystrixCommand(fallbackMethod = "defaultForecast")
	public Forecast getForecast(String city) {
		URI furi = getServiceUrl("forecastms", null);
		System.out
				.println(" from Discovery server -- URL from service forecast ---"
						+ furi.toString());
		String furl = furi.toString() + "/forecast/" + city;

		Forecast forecast = restTemplate.getForObject(furl, Forecast.class);
		return forecast;
	}
	
	public Forecast defaultForecast(String city){
		System.out.println(" Warning :  Using fallback method for Forecast Service------->");
		Forecast forecast = new Forecast();
		return forecast;
	}
	
	
	@HystrixCommand(fallbackMethod = "defaultWeather")
	public Weather getWeather(String city) {

		URI uri = getServiceUrl("weatherms", null);
		System.out
				.println(" from Discovery server -- URL from service weather ---"
						+ uri);
		String url = uri.toString() + "/weather/" + city;
		Weather weather = restTemplate.getForObject(url, Weather.class);
		return weather;
	}
	
	public Weather defaultWeather(String city){
		System.out.println(" Warning :  Using fallback method for Weather Service------->");
		Weather weather = new Weather();
		weather.setName("Data from fallback Method...");
		return weather;
	}
	
	
	private URI getServiceUrl(String serviceId, String fallbackUri) {
		URI uri = null;
		try {
			ServiceInstance instance = loadBalancer.choose(serviceId);
			uri = instance.getUri();
			System.out.println("Resolved serviceId '{}' to URL '{}'."
					+ serviceId + "  " + uri);

		} catch (RuntimeException e) {
			// Eureka not available, use fallback
			uri = URI.create(fallbackUri);
			System.out
					.println("Failed to resolve serviceId '{}'. Fallback to URL '{}'."
							+ serviceId + " " + uri);
		}

		return uri;
	}

}
