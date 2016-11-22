package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.common.UpdateFuelPrice;

/**
 * 06/04/2015 Yu Sun
 * @author Yu Sun
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UpdateFuelPriceTest {
	
	UpdateFuelPrice ufp;
	JSONObject fuel_1, fuel_2, fuel_3, fuel_4;
	JSONArray old_array, new_array;
	Long s_time, e_time;
	
	@Before
	public void setUp() throws JSONException{
		
		s_time = System.currentTimeMillis();
		
		ufp = new UpdateFuelPrice();
		
		fuel_1 = new JSONObject();
		fuel_1.put("fuel_name", "Unleaded");
		fuel_1.put("price", 102.2);
		
		fuel_2 = new JSONObject();
		fuel_2.put("fuel_name", "E85");
		fuel_2.put("price", 60.1);
		
		fuel_3 = new JSONObject();
		fuel_3.put("fuel_name", "Unleaded");
		fuel_3.put("price", 104.2);
		
		fuel_4 = new JSONObject();
		fuel_4.put("fuel_name", "LPG");
		fuel_4.put("price", 52.0);
		
		old_array = new JSONArray();
		new_array = new JSONArray();
		old_array.put( fuel_1 );
		old_array.put( fuel_2 );
		new_array.put( fuel_3 );
		new_array.put( fuel_4 );
	}
	
	@Test
	public void testA_Merge(){
		
		JSONArray res = ufp.mergeTypePriceJSONArray(old_array, new_array);
		assertTrue(res.length() == 3);
	}
	
	@Test
	public void testB_Update(){
		
		assertTrue( ufp.updatePrice("0", old_array) );
		assertTrue( ufp.updatePrice("1", new_array) );
	}
	
	@Test
	public void testC_Insert() throws JSONException{
		
		JSONObject n_s = new JSONObject();
		n_s.put("brand", "Shell");
		n_s.put("latitude", -30.0);
		n_s.put("longitude", 127.2);
		n_s.put("source", 2);
		n_s.put("fuel_provided", old_array);
		
		assertTrue( ufp.insertStationAndUpdatePrice( n_s , new_array) );
		
		e_time = System.currentTimeMillis();
		
		System.out.println("Elapsed time: " + ((double)(e_time - s_time)/1000.0) + "s.");
	}

}
