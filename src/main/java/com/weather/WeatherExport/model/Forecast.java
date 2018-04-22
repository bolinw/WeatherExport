package com.weather.WeatherExport.model;

public class Forecast {
	private String date;
	private String dateLabel;
	private String telop;
	private Image image;
	private String max_celsius;
	private String max_fahrenheit;
	private String min_celsius;
	private String min_fahrenheit;
	
	
	public String getDateLabel() {
		return dateLabel;
	}
	public void setDateLabel(String dateLabel) {
		this.dateLabel = dateLabel;
	}
	public String getTelop() {
		return telop;
	}
	public void setTelop(String telop) {
		this.telop = telop;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}	
	public String getDate() {
		return this.date;
	}
	public void setDate(String date) {
		this.date =date;
	}
	public String getMax_celsius() {
		return max_celsius;
	}
	public void setMax_celsius(String max_celsius) {
		this.max_celsius = max_celsius;
	}
	public String getMax_fahrenheit() {
		return max_fahrenheit;
	}
	public void setMax_fahrenheit(String max_fahrenheit) {
		this.max_fahrenheit = max_fahrenheit;
	}
	public String getMin_celsius() {
		return min_celsius;
	}
	public void setMin_celsius(String min_celsius) {
		this.min_celsius = min_celsius;
	}
	public String getMin_fahrenheit() {
		return min_fahrenheit;
	}
	public void setMin_fahrenheit(String min_fahrenheit) {
		this.min_fahrenheit = min_fahrenheit;
	}
}
