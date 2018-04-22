package com.weather.WeatherExport.util;
import com.weather.WeatherExport.model.*;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WeatherDeserializer implements JsonDeserializer<Weather>{

	@Override
	public Weather deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
		Weather weather = new Weather();
		JsonObject object = (JsonObject) element;
		JsonObject location = object.getAsJsonObject("location");
		JsonObject description = object.getAsJsonObject("description");
		JsonObject copyright = object.getAsJsonObject("copyright");
		JsonArray cr_providers = copyright.getAsJsonArray("provider");
		JsonObject cr_image = copyright.getAsJsonObject("image");
		JsonArray forecasts = object.getAsJsonArray("forecasts");
		JsonArray pinpoints = object.getAsJsonArray("pinpointLocations");	
		weather.setArea(location.get("area").getAsString());
		weather.setPrefecture(location.get("prefecture").getAsString());
		weather.setCity(location.get("city").getAsString());
		weather.setTitle(object.get("title").getAsString());
		weather.setLink(object.get("link").getAsString());
		weather.setPublicTime(object.get("publicTime").getAsString());
		weather.setDesc_publicTime(description.get("publicTime").getAsString());
		weather.setDesc_text(description.get("text").getAsString());
		weather.setForecasts(this.processForecastData(forecasts));
		weather.setPinpointLocations(this.processPinpointLocationData(pinpoints));
		weather.setCr_providers(this.processCrProviderData(cr_providers));
		weather.setCr_title(copyright.get("title").getAsString());
		weather.setCr_link(copyright.get("link").getAsString());
		Image cr_img = new Image();
		cr_img.setHeight(cr_image.get("height").getAsString());
		cr_img.setWidth(cr_image.get("width").getAsString());
		cr_img.setUrl(cr_image.get("url").getAsString());
		cr_img.setTitle(cr_image.get("title").getAsString());
		cr_img.setLink(cr_image.get("link").getAsString());
		
		
		
		return weather;
	}
	private Forecast[] processForecastData(JsonArray forecasts) {
		Forecast[] forecast_array = new Forecast[forecasts.size()];
		for(int i = 0; i < forecasts.size();i++ ) {
			JsonObject forecast_json = (JsonObject)forecasts.get(i);
			JsonElement max_temp = forecast_json.getAsJsonObject("temperature")
										.get("max");
			JsonElement min_temp = forecast_json.getAsJsonObject("temperature")
										.get("min");
			JsonObject image = (JsonObject)forecast_json.getAsJsonObject("image");
			Forecast forecast = new Forecast();
			forecast.setTelop(forecast_json.get("telop").getAsString());
			forecast.setDateLabel(forecast_json.get("dateLabel").getAsString());
			forecast.setDate(forecast_json.get("date").getAsString());
			if (!max_temp.isJsonNull()) {
				forecast.setMax_celsius(max_temp.getAsJsonObject().get("celsius").getAsString());
				forecast.setMax_fahrenheit(max_temp.getAsJsonObject().get("fahrenheit").getAsString());
			}
			if(!min_temp.isJsonNull()) {
				forecast.setMin_celsius(min_temp.getAsJsonObject().get("celsius").getAsString());
				forecast.setMin_fahrenheit(min_temp.getAsJsonObject().get("fahrenheit").getAsString());
			}
			Image img = new Image();
			img.setHeight(image.get("height").getAsString());
			img.setTitle(image.get("title").getAsString());
			img.setUrl(image.get("url").getAsString());
			img.setWidth(image.get("width").getAsString());	
			forecast.setImage(img);
			forecast_array[i] = forecast;
		}
		return forecast_array;
	} 
	private PinpointLocation[] processPinpointLocationData(JsonArray pinpoints) {
		PinpointLocation[] pinpoint_array = new PinpointLocation[pinpoints.size()];
		for(int i = 0; i < pinpoint_array.length;i++) {
			JsonObject pinpoint = pinpoints.get(i).getAsJsonObject();
			PinpointLocation p_location = new PinpointLocation();
			p_location.setLink(pinpoint.get("link").getAsString());
			p_location.setName(pinpoint.get("name").getAsString());
			pinpoint_array[i] = p_location;
		}
		return pinpoint_array;
		
	}
	private Provider[] processCrProviderData(JsonArray providers) {
		Provider[] provider_array = new Provider[providers.size()];
		for(int i = 0; i < provider_array.length;i++) {
			JsonObject provider = providers.get(i).getAsJsonObject();
			Provider cr_provider = new Provider();
			cr_provider.setLink(provider.get("link").getAsString());
			cr_provider.setName(provider.get("name").getAsString());
			provider_array[i] = cr_provider;
		}
		return provider_array;
	}
}
