package com.weather.WeatherExport.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.weather.WeatherExport.model.Forecast;
import com.weather.WeatherExport.model.Weather;

	
public class WeatherExportUtil {
	// 地点定義表データ取得＆地点定義マップ作成ファクション
	public static HashMap<String, String> retrieveLocations(String url, String key, String title_key, String location_key)
	throws Exception
	{
		
		HashMap<String, String> output = new HashMap<String,String>();       
		//Documentスタイルで定義データを検索
	    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	    documentFactory.setNamespaceAware(false);
	    documentFactory.setValidating(false);
	    
	    DocumentBuilder builder = documentFactory.newDocumentBuilder();
	    //設定されているリンクから定義表取得
	    URLConnection urlConnection = new URL(url).openConnection();
	    urlConnection.addRequestProperty("Accept", "application/xml");
	    
	    Document doc = builder.parse(urlConnection.getInputStream());
	    doc.getDocumentElement().normalize();
	    
	    NodeList cityNodes = doc.getElementsByTagName(key);
	    int num_loc = cityNodes.getLength();
	    
	    //地点定義マップ作成
	    for(int i = 0; i < num_loc; i++) {
	    	NamedNodeMap attributes = cityNodes.item(i).getAttributes();
	    	output.put(attributes.getNamedItem(title_key).getNodeValue(), 
	    				attributes.getNamedItem(location_key).getNodeValue());
	    }
		    
		return output;
		
		
	}
	
	public static String retrieveWeatherData(String requestURL,String para,String cityCode ) 
	throws Exception{
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		//リトライについての設定
		int maxRetries = 5;//リトライ回数
		int waitRetries = 5000;//再試行間隔
		//http client作成＆リトライ回数設定＆再試行間隔設定
		HttpClient client = httpBuilder.setRetryHandler(retryHandler(maxRetries))
						.setServiceUnavailableRetryStrategy(getStrategy(waitRetries,maxRetries)).build();
		//HttpClient client2 = httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(5,true)).build();
		StringBuilder request = new StringBuilder();
		//API引数設定
		request.append(requestURL);
		request.append("?");
		request.append(para + "=");
		request.append(cityCode);
		
		HttpGet getRequest = new HttpGet(request.toString());
		getRequest.addHeader("accept","application/json");
		
		String json = "";
		//レスポンス取得
		HttpResponse response = client.execute(getRequest);
		json = IOUtils.toString(response.getEntity().getContent());		

		return json;
	}
	//ファイルエクスポートファクション
	public static void exportCsvM1(Weather weather, String path) {
		try {
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL);
			Forecast[] forecasts = weather.getForecasts();
			ArrayList<String> labels = new ArrayList<String>();
			//項目作成
			labels = addInfoLabels(labels);
			labels = addForecastLabels(labels);
			//項目出力
			csvPrinter.printRecord(labels);	
			//データ出力
			for(int i = 0; i < (forecasts == null ? -1 : forecasts.length);i++) {
				Forecast cur = forecasts[i];
				csvPrinter.printRecord(weather.getPublicTime(), weather.getTitle(),
									 	weather.getDesc_publicTime(), weather.getDesc_text().replaceAll("\n", ""),
									 	weather.getArea(),weather.getPrefecture(), 
									 	weather.getCity(), cur.getDate(), cur.getDateLabel(),
									 	cur.getTelop(), cur.getMax_celsius(), cur.getMax_fahrenheit(),
									 	cur.getMin_celsius(), cur.getMin_fahrenheit());		
			}
			csvPrinter.flush();
			csvPrinter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	//自製リトライ処理
	private static HttpRequestRetryHandler retryHandler(int maxC) {
		return (exception, executionCount, context) -> {
			if(executionCount > maxC) {
				return false;
			}
			
			if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
		};
	}
	//自製再試行間隔処理
	private static ServiceUnavailableRetryStrategy getStrategy(int maxWait, int maxRetries) {
		return new ServiceUnavailableRetryStrategy() {
			@Override
			public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                return executionCount <= maxRetries &&
                        response.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE;
            }
			@Override
            public long getRetryInterval() {
                return maxWait;
            }
		};
	}
	//項目作成1
	public static ArrayList<String> addInfoLabels(ArrayList<String> list){
		list.add("発表日時");
		list.add("タイトル");
		list.add("天気概況文の発表時刻");
		list.add("天気概況文");
		list.add("地方名");
		list.add("都道府県名");
		list.add("1次細分区名");
		

		return list;
	}
	//項目作成2
	public static ArrayList<String> addForecastLabels(ArrayList<String> list){
		list.add("予報日");
		list.add("予報日");
		list.add("天気");
		list.add("最高気温(摂氏)");
		list.add("最高気温(華氏)");
		list.add("最低気温(摂氏)");
		list.add("最低気温(華氏)");
		return list;
	}
	
}
