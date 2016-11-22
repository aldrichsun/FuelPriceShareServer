/**
 * 
 */
package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.example.spatialanalytics.function.*;

/**
 * To test the class RangeQuery
 * @author Yu Sun
 */
public class RangeQueryTest {

	double[] lat_list = {
			-37.795655,
			-37.842716,
			-38.409649,
			-37.815664,
			-37.902298
	};
	double[] lng_list = {
			144.981239,
			144.883618,
			144.186367,
			144.966770,
			144.653572
	};
	double[] range_dist = {
			5.0,
			4.0,
			10.0,
			1.0,
			50.0
	};
	
	private String user_id = "asun0715@gmail.com";
	
	/**
	 * Test the range query in RangeQuery class with the above five locations
	 * and range distances in this class.
	 * @throws JSONException
	 */
	@Test
	public void test() throws JSONException {
		
		for(int i = 0; i < lat_list.length; i++){
			
			RangeQuery rq = new RangeQuery(
				user_id,
				lat_list[i],
				lng_list[i],
				range_dist[i],
				ConstantConfig.FUEL_STATION_TABLE_NAME
			);
			
			String res = rq.getResult();
			System.out.println(res+"\n");
			assertNotNull(res);
			
			JSONObject jsonObj = new JSONObject(res);
			JSONArray jsonArray = (JSONArray) jsonObj.get(ConstantConfig.FUEL_STATION_TABLE_NAME);
			assertNotNull(jsonArray);
			assertTrue(jsonArray.length() >= 1);
		}
	}

}
