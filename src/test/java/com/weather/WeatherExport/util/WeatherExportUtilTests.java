package com.weather.WeatherExport.util;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weather.WeatherExport.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class WeatherExportUtilTests {
	
	
	private final String apiUrl="http://weather.livedoor.com/forecast/webservice/json/v1";
	private final String apiPara1="city";
	
	private final String location_url = "http://weather.livedoor.com/forecast/rss/primary_area.xml";
	private final String loc_key="city";
	private final String loc_title="title";
	private final String loc_id="id";
	private String[] locations;
	private String[] locationCodes;
	
	
	private Gson gson;
	
	//init
	@Before
	public void init() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Weather.class, new WeatherDeserializer());
		gson = builder.create();
		try {
			FileReader fr = new FileReader("./locations.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			ArrayList<String> loc_list = new ArrayList<String>();
			ArrayList<String> locCode_list = new ArrayList<String>();
			while((line = br.readLine()) != null) {
                String[] current = line.split("=");
                loc_list.add(current[0].replaceAll("\\p{C}", ""));
                locCode_list.add(current[1]);
            }
			locations = loc_list.toArray(new String[0]);
			locationCodes = locCode_list.toArray(new String[0]);
            br.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//WEBから地域コード取得機能テスト
	@Test
	public void testRetrieveLocations() throws Exception {
		HashMap<String,String> map = WeatherExportUtil.retrieveLocations(location_url, loc_key, loc_title, loc_id);
		
		for(int i = 0; i < locations.length; i++) {
			String map_code = map.get(locations[i]);
			//ファイルの地域コードをWEBから取得した地域コードと比べ
			assertEquals(map_code, locationCodes[i]);
		}
		
	}
	
	//JSONデータ取得機能テスト
	//Weatherオブジェクト作成テスト
	//データエ0クスポートテスト
	@Test
	public void testRetrieveWeatherDataAndExportData() throws Exception {
		ArrayList<Weather> weather_list = new ArrayList<Weather>();
		for(int i = 0; i < locationCodes.length;i++) {
			String json = WeatherExportUtil.retrieveWeatherData(apiUrl, apiPara1, locationCodes[i]);
			//Weatherオブジェクト作成
			Weather weather = gson.fromJson(json, Weather.class);
			assertEquals(weather.getCity(),locations[i]);
			weather_list.add(weather);
		}
		
		//エクスポート
		Weather[] weathers = weather_list.toArray(new Weather[0]);
		for(int i = 0; i < weathers.length; i++) {
			String path = "./" + weathers[i].getCity() + ".txt";
			WeatherExportUtil.exportCsvM1(weathers[i], path);
			File file = new File(path);
			assertTrue(file.exists());
			//ファイル削除
			file.delete();
			
			
		}
	}
	
	
	//地域取得：URLが異なる場合: 区域間違っている場合
	@Test(expected=UnknownHostException.class)
	public void testRetrieveLocationsWrongUrl1() throws Exception {
		String wrongUrl = "http://weather.livedo.com/forecast/rss/primary_area.xml";
		WeatherExportUtil.retrieveLocations(wrongUrl, loc_key, loc_title, loc_id);
		
	}
	//地域取得：URLが異なる場合：　ファイルの名称間違っている場合
	@Test(expected=FileNotFoundException.class)
	public void testRetrieveLocationsWrongUrｌ2() throws Exception {
		String wrongUrl = "http://weather.livedoor.com/forecast/rss/primar_area.xml";
		WeatherExportUtil.retrieveLocations(wrongUrl, loc_key, loc_title, loc_id);
		
	}
	//API　URLが異なる場合: 区域間違っている場合
	@Test(expected=UnknownHostException.class)
	public void testRetrieveWeatherDataWrongUrl1() throws Exception {
		String wrongUrl = "http://weather.livedo.com/forecast/webservice/json/v1";
		WeatherExportUtil.retrieveWeatherData(wrongUrl, apiPara1, locationCodes[0]);
	
	}
	//API　URLが異なる場合：APIバージョン間違っている場合
	@Test(expected=ClassCastException.class)
	public void testRetrieveWeatherDataWrongUr2() throws Exception {
		String wrongUrl = "http://weather.livedoor.com/forecast/webservice/json/v3";
		String json = WeatherExportUtil.retrieveWeatherData(wrongUrl, apiPara1, locationCodes[0]);
		gson.fromJson(json, Weather.class);
		
	
	}

	
	
}
