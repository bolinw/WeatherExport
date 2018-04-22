package com.weather.WeatherExport;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.context.SpringBootTest;



@RunWith(Parameterized.class)
@SpringBootTest
public class WeatherExportApplicationTests {
	private String[] args;
	private Boolean e_result;
	
	private WeatherExportApplication app;
	
	public WeatherExportApplicationTests(String[] args, Boolean er1) {
		this.args = args;
		this.e_result = er1;
	}
	
	@Before
	public void init() {
		app = new WeatherExportApplication();
	}
	//ファイルからテストケース作成
	@Parameterized.Parameters
	public static Collection arguments() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		ArrayList<Boolean> result1 = new ArrayList<Boolean>();
		try {
			FileReader fr = new FileReader("./appInput.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
                String[] current = line.split(" ");
                current[0] = current[0].replaceAll("\\p{C}", "");
                list.add(current);
            }
			
            br.close();
            fr = new FileReader("./ExpectedResult.txt");
            br = new BufferedReader(fr);
            while((line = br.readLine()) != null) {
                result1.add(new Boolean(line));
            }
		}catch (Exception e) {
			e.printStackTrace();
		}
		Object[][] out = new Object[list.size()][2];
		for(int i = 0; i < list.size();i++) {
			out[i][0] = list.get(i);
			out[i][1] = result1.get(i);
		}
		return Arrays.asList(out);
	}
	
	//引数チェックファクションのテスト
	@Test
	public void testCreateArgumentsMapAndInputCheck() {
		HashMap<String, String> temp = new HashMap<String, String>();
		boolean test1 = app.createArgumentsMap(temp, args );
		assertEquals(test1 ? app.checkInputs(temp) : false,e_result.booleanValue());
	}
	
	

}
