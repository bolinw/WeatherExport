package com.weather.WeatherExport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weather.WeatherExport.model.Weather;
import com.weather.WeatherExport.util.WeatherDeserializer;


@Configuration
public class WeatherExportConfig {
	
	//GSONオブジェクト作成設定
	@Bean
	public Gson getGson() {
		GsonBuilder gsonBldr = new GsonBuilder();
		gsonBldr.registerTypeAdapter(Weather.class, new WeatherDeserializer());
		
		return gsonBldr.create();
	}
	
}
