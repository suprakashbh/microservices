package com.infy.ms.client.weather;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infy.ms.client.model.Forecast;
import com.infy.ms.client.model.ForecastDetails;
import com.infy.ms.client.model.Weather;
import com.infy.ms.client.model.WeatherAggregated;

@RestController
public class WeatherServiceClient {
	
	 @Autowired
	 private LoadBalancerClient loadBalancer;
	
	@RequestMapping("/weatherclient/{city}")
	@ResponseBody
	WeatherAggregated getWeather(@PathVariable String city) {
		
		
		// Step 1 Call get weather		
        URI uri = getServiceUrl("weather",null);
        System.out.println(" from Discovery server -- URL from service weather ---"+uri.toString());
        String url = uri.toString() + "/weather/" + city;
		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Weather weather = restTemplate.getForObject(url, Weather.class);
		
		// Step 2 call get Forecast		
		
        URI furi = getServiceUrl("forecast",null);
        System.out.println(" from Discovery server -- URL from service weather ---"+uri.toString());
        String furl = furi.toString() + "/forecast/" + city;		
		Forecast forecast = restTemplate.getForObject(furl, Forecast.class);
		
		List<ForecastDetails> list = forecast.getList();
		
		ForecastDetails forecastDetails = new ForecastDetails();
		if(null != list && list.size() > 0){
			forecastDetails = list.get(0);
		}
		
		return new WeatherAggregated(weather,forecastDetails);
	}
	
	
	 private URI getServiceUrl(String serviceId, String fallbackUri) {
	        URI uri = null;
	        try {
	            ServiceInstance instance = loadBalancer.choose(serviceId);
	            uri = instance.getUri();
	            System.out.println("Resolved serviceId '{}' to URL '{}'."+ serviceId+"  "+uri);

	        } catch (RuntimeException e) {
	            // Eureka not available, use fallback
	            uri = URI.create(fallbackUri);
	            System.out.println("Failed to resolve serviceId '{}'. Fallback to URL '{}'."+ serviceId+" "+ uri);
	        }

	        return uri;
	    }
	
	
	

}
