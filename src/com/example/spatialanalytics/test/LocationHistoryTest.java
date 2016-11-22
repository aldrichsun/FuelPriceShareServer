package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.LocationHistory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocationHistoryTest {

	private String u_id = "test_u_id";
	private String addr_0 = "St Kilda, VIC";
	private String addr_1 = "Box Hill, VIC";
	
	private int test_length = 30;
//	private String[] address;
	private LocationHistory lh;
	
	@Before
	public void setUp(){
		
		lh = new LocationHistory();
	}
	
	@Test
	public void testA() throws JSONException{
		
		lh.recordLocationHistory(u_id, addr_0);
		lh.recordLocationHistory(u_id, addr_1);
		JSONObject res = lh.getLocationHistory(u_id);
		String resStr = res.toString();
		assertTrue( resStr.contains(addr_0) );
		assertTrue( resStr.contains(addr_1) );
		
		lh.recordLocationHistory(u_id, addr_0);
		lh.recordLocationHistory(u_id, addr_1);
		JSONObject res_new = lh.getLocationHistory(u_id);
		JSONArray resArr = res_new.getJSONArray(ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY);
		assertTrue( resArr.length() == 2 );
	}
	
	@Test
	public void testB() throws JSONException{
		
		for(int i = 0; i < test_length; i++)
			lh.recordLocationHistory(u_id, "address " + i);
		JSONObject res = lh.getLocationHistory(u_id);
		JSONArray resArr = res.getJSONArray(ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY);
		assertTrue( resArr.length() == 29 ); // MAX_RETURN_NUMBER
	}
	
	
}
