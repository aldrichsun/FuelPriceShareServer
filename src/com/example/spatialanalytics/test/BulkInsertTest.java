package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.*;

/**
 * To test the BulkInsert class
 * @author Yu Sun
 * Technically speaking, such test is not unit test, since the test class is not fully isolated
 * and the executing sequence of the test methods affects the test result.
 * Anyway, for simplicity and convenience, we use such kind of test to make sure the correctness
 * and robust of the code. 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BulkInsertTest {
	
	/**
	 * 29/01/2015 Yu Sun: Test the insert. Note that the table (or database in CouchDB) bearing
	 * the table name must be created MANUALLY before the test.
	 */
	@Test
	public void testA_Insert() {
	
//		String filePath = "./data/test.in";
//		BulkInsert bi = new BulkInsert();
//		String res = bi.insertToDb(filePath, ConstantConfig.FUEL_STATION_TABLE_NAME + "_test");
//		
//		assertEquals(res, "done");
	}
	
	/**
	 * 06/03/2015 Yu Sun: Test the insert as a single document. 
	 * Note that the table (or database in CouchDB) bearing the table name must 
	 * be created MANUALLY before the test.
	 */
	@Test
	public void testB_InsertAsSingleDoc() {
		
		String realData_filePath = "./data/fuelStations.json"; // provided by Andy on 06/03/2015
		BulkInsert bi = new BulkInsert();
		String res = bi.insertAsSingleDoc(
				realData_filePath,
				ConstantConfig.FUEL_STATION_TABLE_NAME, // "fuel_station"
				ConstantConfig.ALL_STATIONS_DOC_ID // "all_stations"
		);
		
		assertEquals(res, "done");
	}

}
