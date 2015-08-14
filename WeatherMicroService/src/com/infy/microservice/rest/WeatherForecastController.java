package com.infy.microservice.rest;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${spring.application.stub}")
	private boolean stub;

	@RequestMapping("/weather/{city}")
	@ResponseBody
	Weather getWeather(@PathVariable String city) {
		
		System.out.println("stub ---->"+stub);
		if(!stub){
			RestTemplate restTemplate = new RestTemplate();

			System.out.println(" restTemplate :::" + restTemplate);
			Weather weather = restTemplate.getForObject(
					"http://api.openweathermap.org/data/2.5/weather?q=" + city
							+ "&units=Metric", Weather.class);

			return weather;
		}else {
			return getStubWeatherData(city);
		}
		
	}

	@RequestMapping("/weather")
	@ResponseBody
	Weather getWeather() {
		// default to Pune
		String city = "Pune";
		if(stub){
			return getStubWeatherData(city);
		}

		RestTemplate restTemplate = new RestTemplate();

		System.out.println(" restTemplate :::" + restTemplate);
		Weather weather = restTemplate.getForObject(
				"http://api.openweathermap.org/data/2.5/weather?q=" + city
						+ "&units=Metric", Weather.class);

		return weather;
	}
	
	private Weather getStubWeatherData(String city){
		String mockFile = "/data/weather_"+city.toLowerCase()+".json";
		ObjectMapper objectMapper = new ObjectMapper();
		Weather weather = null;
		try {
			InputStream stream = getClass().getResourceAsStream(mockFile);
			weather = objectMapper.readValue(stream, Weather.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return weather;
	}
	

}
