package com.weather.WeatherExport;

import java.util.HashMap;
import com.weather.WeatherExport.util.*;
import com.google.gson.Gson;
import com.weather.WeatherExport.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;



@SpringBootApplication
public class WeatherExportApplication implements CommandLineRunner {

	@Autowired
	Environment env;
	
	@Autowired
	Gson gson;
	
	public static final String LOCATION_URL = "location.xmlUrl";
	public static final String LOCATION_KEYWORD = "location.keyword";
	public static final String LOCATION_TITLE = "location.title";
	public static final String LOCATION_LOCATIONCODE = "location.locationCode";
	public static final String REST_URL="weather.api";
	public static final String REST_PARA1="weather.api.para1";
	public static final String locKey = "-l";
	public static final String pathKey = "-p";
	
	public static void main(String[] args) {
		SpringApplication.run(WeatherExportApplication.class, args);
	}
	
	
	@Override
	public void run(String... args) throws Exception{
		//Application.Propertiesに設定取得
		String location_url = env.getProperty(LOCATION_URL); //全国の地点定義表URL
		String location_key = env.getProperty(LOCATION_KEYWORD); //XML検索キーワード:city
		String location_title = env.getProperty(LOCATION_TITLE); //XML検索キーワード:title
		String location_locationCode = env.getProperty(LOCATION_LOCATIONCODE); //XML検索キーワード:id
		String apiURL = env.getProperty(REST_URL); //REST　API　URL
		String apiPara1 = env.getProperty(REST_PARA1); //REST地点引数
		
		/*
		 * UNICODEの引数を使っているため、	commons-cliライブラリが使えないとなる
		Options options = new Options();
		Option loc_name = new Option("l", "location", true, "Location name");
		loc_name.setRequired(true);
		Option file_path = new Option("p", "file", true, "File path");
		file_path.setRequired(true);
		options.addOption(loc_name);
		options.addOption(file_path);
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			cmd = parser.parse(options, args);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			formatter.printHelp("[Weather Data Export]", options);
			return;
		}
		String path1 = cmd.getOptionValue("file");
		String location1 = cmd.getOptionValue("location");
		*/
		
		OsNativeWindows windows = new OsNativeWindows();//UNICODEの引数受けるため、JNAを使う
		String[] arguments = windows.getCommandLineArguments(args);//UNICODEの引数受けるため、JNAを使う
		HashMap<String,String> argsMap = new HashMap<String,String>();//引数マップオブジェクトINIT
		if(!createArgumentsMap(argsMap, arguments)) {//引数データ取り
			return;
		}
		if(!checkInputs(argsMap)) {//引数データチェック
			return;
		}
		// 地点定義表データ取得＆地点定義マップ作成
		HashMap<String,String> location_map = WeatherExportUtil.
				retrieveLocations(location_url, location_key, 
							location_title, location_locationCode);
		
		//引数値取得
		String location = argsMap.get(locKey);//地域
		String path = argsMap.get(pathKey);//出力先のファイルのパス
		
		String locationCode = location_map.get(location);//地域コード取得
		
		
		if(locationCode == null) {
			System.out.println("Invalid location code!");
			return;
		}
		
		//天気データをAPIに取得
		String result = WeatherExportUtil.retrieveWeatherData(apiURL, apiPara1, locationCode);
		//GSONでWeatherオブジェクト作成
		Weather weather = gson.fromJson(result, Weather.class);
		//エクスポートファイル作る
		WeatherExportUtil.exportCsvM1(weather, path);
		Forecast[] forecasts = weather.getForecasts();
		//コンソール画面に天気情報を出力
		System.out.println(weather.getTitle());
		System.out.println("予報の発表日時: " + weather.getPublicTime());
		System.out.println("天気概況文: " );
		System.out.println(weather.getDesc_text());
		System.out.println("---------------------------------------------------------------------------------------");		
		System.out.println(String.format("%-18s%-14s%-10s%-10s%-10s%-10s%-10s", "予報日", "予報日", "天気", 
							"最高気温(摂氏)",  "最高気温(華氏)" , "最低気温(摂氏)" , "最低気温(華氏)"));
		for(Forecast forecast : forecasts) {
			String out = String.format("%-13s%-16s%-13s%-13s%-17s%-15s%-13s", forecast.getDate(), forecast.getDateLabel(), 
					forecast.getTelop(), forecast.getMax_celsius() == null ? "" : forecast.getMax_celsius(), 
					forecast.getMax_fahrenheit() == null ? "" : forecast.getMax_fahrenheit(),
					 forecast.getMin_celsius() == null ? "" : forecast.getMin_celsius(), 
							forecast.getMin_fahrenheit() == null ? "" : forecast.getMin_fahrenheit());
			System.out.println(out);
		}
		System.out.println("---------------------------------------------------------------------------------------");
		
		
		
	}
	//引数チェックファクション
	public boolean checkInputs(HashMap<String, String> map) {
		boolean report = false;
		String missing = "";
		StringBuilder helpme = new StringBuilder("[Weather Export]");
		if (map.get(locKey).equals("")) {//地域チェック
			missing += "l ";
			report = true;
		}
		if (map.get(pathKey).equals("")) {//ファイルパスチェック
			missing += "p ";
			report = true;
		}
		//チェックに失敗した場合、引数のフォーマットを表示
		if(report) {
			helpme.append("Missing required options: " + missing + "\n" );
			helpme.append("usage: \n ");
			helpme.append("-l,     Location name \n");
			helpme.append("-p,     File path \n");
			System.out.println(helpme.toString());
			return false;
		}
		return true;
	}
	//引数マップ作成ファクション
	public boolean createArgumentsMap(HashMap<String, String> map, String[] args) {
		map.put(locKey, "");
		map.put(pathKey, "");
		for(int i = 0; i < args.length; i++) {
			
			if(map.containsKey(args[i])) {
				//重複する引数をチェック
				if(!map.get(args[i]).equals("")) {
					System.out.println("Duplicate input: " + args[i]);
					return false;
				}
				else {
					try {
						map.put(args[i], args[i+1]);
					}catch (Exception e) {
						System.out.println("Invalid input format!");
						return false;
					}
				}
			}
			
		}
		return true;
	}
	
	
}
