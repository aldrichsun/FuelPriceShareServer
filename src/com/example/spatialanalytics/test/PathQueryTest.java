package com.example.spatialanalytics.test;

//import java.net.URI;
//import java.util.ArrayList;
//
//import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.PathQuery;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PathQueryTest {

	// location: Uni Melbourne
	private double lat_s = -37.7963;
	private double lng_s = 144.9614;
	
	// location: St Kilda
	private double lat_e = -37.8640;
	private double lng_e = 144.9820;
	
	//range distance
	private double r_dist = 1.0; //km
	
	PathQuery pq;
	
	@Before
	public void setUp(){
		
	}
	
	/**
	 * Created by Yu Sun on 11/02/2015 for debug purposes.
	 * After testing, this test will be removed.
	 */
//	@Test
//	public void testA_APICall(){
//		
//		pq = new PathQuery(lat_s, lng_s, lat_e, lng_e, r_dist);
//		
////		UriBuilder builder = UriBuilder
////				.fromUri("https://maps.googleapis.com/")
////				.path("maps/api/directions/json");
////			builder.queryParam("mode", "driving");
////		builder.build();
//		
//		ArrayList<PathQuery.RouteSegment> res = pq.getDirections(lat_s, lng_s, lat_e, lng_e);
//		assertTrue( !res.isEmpty() );
//	}

	/**
	 * Created by Yu Sun on 11/02/2015 for debug purposes.
	 * Test the most common case for PathQuery.
	 * We may need to test other error or empty cases.
	 */
	@Test
	public void testB_PathQuery() throws JSONException{
		
		pq = new PathQuery(lat_s, lng_s, lat_e, lng_e, r_dist);
		
		JSONObject json = new JSONObject(pq.getResult());
		String direction =  json.getString("direction");
		
		assertTrue( direction.contains("OK") );
		
		//String station = json.getString("fuel_station");
		JSONArray jsonArray = json.getJSONArray("fuel_station");
		
		assertTrue( jsonArray != null );
		assertTrue( jsonArray.length() > 0 );
	}
	
	
}
