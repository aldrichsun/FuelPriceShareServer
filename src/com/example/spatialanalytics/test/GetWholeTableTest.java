package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GetWholeTableTest {

	long first_time = 0;
	long second_time = 0;
	
	// 06/03/2015 Test get whole table stored in a single document.
	@Test
	public void testA_getWholeTableStoredInSingleDoc(){
		
		String t_n = ConstantConfig.FUEL_STATION_TABLE_NAME;
		String d_id = ConstantConfig.ALL_STATIONS_DOC_ID;
		
		long st = System.currentTimeMillis();
		JSONArray ja = CouchDBWholeTable.getWholeTableStoredInSingleDoc(t_n, d_id);
		first_time = System.currentTimeMillis() - st;
		System.out.println("First time fetch time: " + first_time + " ms");
		
		assertTrue( ja != null && ja.length() > 0 );
		System.out.println( "The table size is: " + ja.length() );
	}
	
	// 06/03/2015 Test get whole table stored in a single document again, which shall be faster
	// than the first time.
	@Test
	public void testB_getWholeTableStoredInSingleDoc_again(){
		
		String t_n = ConstantConfig.FUEL_STATION_TABLE_NAME;
		String d_id = ConstantConfig.ALL_STATIONS_DOC_ID;
		
		long st = System.currentTimeMillis();
		JSONArray ja = CouchDBWholeTable.getWholeTableStoredInSingleDoc(t_n, d_id);
		second_time = System.currentTimeMillis() - st;
		System.out.println("Second time fetch time: " + second_time + " ms");
		
		assertTrue( ja != null && ja.length() > 0 );
		// The "first_time" is always 0, as the execution order of the test methods
		// should have no affect on the test result as per JUnit test philosophy.
		//assertTrue( second_time < first_time );
		
	}
	
}
