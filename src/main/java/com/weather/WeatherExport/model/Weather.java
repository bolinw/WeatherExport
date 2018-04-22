package com.weather.WeatherExport.model;


public class Weather {
	private String city;
	private String prefecture;
	private String area;
	private PinpointLocation[] pinpointLocations;
	private Forecast[] forecasts;
	private Provider[] cr_providers;
	private String title;
	private String link;
	private String publicTime;
	private String desc_text;
	private String desc_publicTime;
	private String cr_link;
	private String cr_title;
	private Image cr_image;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublicTime() {
		return publicTime;
	}
	public void setPublicTime(String publicTime) {
		this.publicTime = publicTime;
	}
	public PinpointLocation[] getPinpointLocations() {
		return pinpointLocations;
	}
	public void setPinpointLocations(PinpointLocation[] pinpointLocations) {
		this.pinpointLocations = pinpointLocations;
	}
	public Forecast[] getForecasts() {
		return forecasts;
	}
	public void setForecasts(Forecast[] forecasts) {
		this.forecasts = forecasts;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPrefecture() {
		return prefecture;
	}
	public void setPrefecture(String prefecture) {
		this.prefecture = prefecture;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDesc_text() {
		return desc_text;
	}
	public void setDesc_text(String desc_text) {
		this.desc_text = desc_text;
	}
	public String getDesc_publicTime() {
		return desc_publicTime;
	}
	public void setDesc_publicTime(String desc_publicTime) {
		this.desc_publicTime = desc_publicTime;
	}
	public String getCr_link() {
		return cr_link;
	}
	public void setCr_link(String cr_link) {
		this.cr_link = cr_link;
	}
	public String getCr_title() {
		return cr_title;
	}
	public void setCr_title(String cr_title) {
		this.cr_title = cr_title;
	}
	public Image getCr_image() {
		return cr_image;
	}
	public void setCr_image(Image cr_image) {
		this.cr_image = cr_image;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Provider[] getCr_providers() {
		return cr_providers;
	}
	public void setCr_providers(Provider[] cr_providers) {
		this.cr_providers = cr_providers;
	}
}
