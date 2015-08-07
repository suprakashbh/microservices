package com.infy.ms.rest.fc;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infy.ms.rest.model.Forecast;

@RestController
@EnableAutoConfiguration
public class ForecastMsController {
	
	@RequestMapping("/forecast/{city}")
	@ResponseBody
	Forecast getForecast(@PathVariable String city) {

		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Forecast forecast = restTemplate.getForObject(
				"http://api.openweathermap.org/data/2.5/forecast?q=" + city
						+ "&units=Metric", Forecast.class);

		return forecast;
	}

	@RequestMapping("/forecast")
	@ResponseBody
	Forecast getForecast() {
		// default to Pune
		String city = "Pune";

		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Forecast weather = restTemplate.getForObject(
				"http://api.openweathermap.org/data/2.5/forecast?q=" + city
						+ "&units=Metric", Forecast.class);

		return weather;
	}

}
