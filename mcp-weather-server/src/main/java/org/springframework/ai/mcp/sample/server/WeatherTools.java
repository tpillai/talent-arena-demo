/* 
* Copyright 2025 - 2025 the original author or authors.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* https://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.springframework.ai.mcp.sample.server;

import java.time.LocalDateTime;

import org.slf4j.Logger;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherTools {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(WeatherTools.class);

	private final RestClient restClient;

	public WeatherTools() {
		this.restClient = RestClient.create();
	}

	public record WeatherResponse(Current current) {
		public record Current(LocalDateTime time, int interval, double temperature_2m) {
		}
	}

	public record AirQualityResponse(AirQuality hourly) {
		public record AirQuality(LocalDateTime[] time, double[] pm10, double[] pm2_5, double[] carbon_monoxide,
				double[] nitrogen_dioxide, double[] sulphur_dioxide, double[] ozone, double[] european_aqi) {
		}
	}

	public record DailyForecastResponse(DailyForecast daily) {
		public record DailyForecast(
				LocalDateTime[] time,
				double[] temperature_2m_max,
				double[] temperature_2m_min,
				double[] precipitation_sum,
				double[] rain_sum,
				double[] snowfall_sum,
				double[] precipitation_hours,
				double[] wind_speed_10m_max,
				double[] wind_gusts_10m_max) {
		}
	}

	@Tool(description = "Get the temperature (in celsius) for a specific location")
	public WeatherResponse getTemperature(@ToolParam(description = "The location latitude") double latitude,
			@ToolParam(description = "The location longitude") double longitude,
			@ToolParam(description = "The city name") String city) {

		WeatherResponse response = restClient
				.get()
				.uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
						latitude, longitude)
				.retrieve()
				.body(WeatherResponse.class);

		logger.info("Check temparature for {}. Lat: {}, Lon: {}. Temp: {}", city, latitude, longitude,
				response.current);

		return response;
	}

	@Tool(description = "Get the air quality for a specific location")
	public AirQualityResponse getAirQuality(@ToolParam(description = "The location latitude") double latitude,
			@ToolParam(description = "The location longitude") double longitude,
			@ToolParam(description = "The city name") String city) {

		AirQualityResponse response = restClient
				.get()
				.uri("https://air-quality-api.open-meteo.com/v1/air-quality?latitude={latitude}&longitude={longitude}&hourly=pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,european_aqi",
						latitude, longitude)
				.retrieve()
				.body(AirQualityResponse.class);

		logger.info("Check air quality for {}. Lat: {}, Lon: {}. AQI: {}", city, latitude, longitude,
				response.hourly);

		return response;
	}

	@Tool(description = "Get the 7-day weather forecast for a specific location")
	public DailyForecastResponse getDailyForecast(@ToolParam(description = "The location latitude") double latitude,
			@ToolParam(description = "The location longitude") double longitude,
			@ToolParam(description = "The city name") String city) {

		String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,rain_sum,snowfall_sum,precipitation_hours,windspeed_10m_max,windgusts_10m_max&timezone=auto&forecast_days=7", latitude, longitude);
		DailyForecastResponse response = restClient
				.get()
				.uri(url)
				.retrieve()
				.body(DailyForecastResponse.class);

		logger.info("Check 7-day forecast for {}. Lat: {}, Lon: {}. Forecast: {}", city, latitude, longitude,
				response.daily);

		return response;
	}
}
