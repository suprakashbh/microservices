package com.infy.microservice.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infy.model.Weather;

@RestController
@EnableAutoConfiguration
public class WeatherForecastController {

	@RequestMapping("/weather/{city}")
	@ResponseBody
	Weather getWeather(@PathVariable String city) {

		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Weather weather = restTemplate.getForObject(
				"http://api.openweathermap.org/data/2.5/weather?q=" + city
						+ "&units=Metric", Weather.class);

		return weather;
	}

	@RequestMapping("/weather")
	@ResponseBody
	Weather getWeather() {
		// default to Pune
		String city = "Pune";

		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Weather weather = restTemplate.getForObject(
				"http://api.openweathermap.org/data/2.5/weather?q=" + city
						+ "&units=Metric", Weather.class);

		return weather;
	}

}
