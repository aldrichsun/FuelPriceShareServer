package com.example.spatialanalytics.function;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import javax.ws.rs.core.UriBuilder;
import java.awt.geom.Line2D;

/**
 * Created by Yu Sun on 10/02/2015
 * Find the fuel stations along a direction path.
 * Function:
 * Input: an origin -- (lat, lng), an destination -- (lat, lng), the maximum distance to the path
 * Output: 
 * 	i) A Json object with 1) the name of "fuel_station" and value of a Json array contains a 
 * 	   list of the required stations, and 2) the name of "direction" and value of the response
 *     of google directions API call.
 *  ii) If there are errors in any field, the corresponding value would be "error".
 *  iii) If there are no query results, the corresponding value would be "empty".
 *  One example result is {"fuel_station":"empty","direction":"error"}
 * 
 * @author Yu Sun
 */
public class PathQuery {

	private static final Logger logger = LogManager.getLogger(PathQuery.class.getSimpleName());
	
	private static final String RESULT_DIRECTION_KEY = "direction";
	private static final String RESULT_STATION_KEY = ConstantConfig.FUEL_STATION_TABLE_NAME;
	private static final String ERROR = "error";
	private static final String EMPTY = "empty";
	
	// change this if query other points than fuel stations
	private String table_name = ConstantConfig.FUEL_STATION_TABLE_NAME;
	// added by Yu Sun on 06/03/2015 for efficiently fetching the whole table
	private String doc_id = ConstantConfig.ALL_STATIONS_DOC_ID;
	// store the response from google directions API calling
	private String API_response;
	
	private double lat_o;	// latitude of the origin
	private double lng_o;	// longitude of the origin
	private double lat_d;	// latitude of the destination
	private double lng_d;	// longitude of the destination
	private double dist_r;	// maximum distance to the path
	
	/**
	 * Constructor of the class
	 * @param lat_o -- latitude of the origin
	 * @param lng_o -- longitude of the origin
	 * @param lat_d -- latitude of the destination
	 * @param lng_d -- longitude of the destination
	 * @param dist_r -- maximum distance to the path
	 */
	public PathQuery(double lat_o, double lng_o, 
			  double lat_d, double lng_d, double dist_r){
		this.lat_o = lat_o;
		this.lng_o = lng_o;
		this.lat_d = lat_d;
		this.lng_d = lng_d;
		this.dist_r = dist_r;
	}
	
	/**
	 * Given the origin, destination, this method calls the google directions API to get a (shortest)
	 * path between the origin and destination, and then selects the points (currently the fuel stations
	 * in the table 'fuel_station') that are within the maximum distance to the path.   
	 * @return
	 *  i) A Json object with 1) the name of "fuel_station" and value of a Json array contains a 
	 * 	   list of the required stations, and 2) the name of "direction" and value of the response
	 *     of google directions API call.
	 *  ii) If there are errors in any field, the corresponding value would be "error".
	 *  iii) If there are no query results, the corresponding value would be "empty".
	 *  One example result is {"fuel_station":"empty","direction":"error"}
	 */
	public String getResult(){

		return this.filter().toString();
	}
	
	/**
	 * Call the google directions API, store the API response in the private instance variable API_response
	 * and extract the sub-routes, each of which is the maximal STRAIGHT line segment in a route and here called
	 * a RouteSegment, and store them in an array list.
	 * 
	 * @param lat_o -- latitude of the origin
	 * @param lng_o -- longitude of the origin
	 * @param lat_d -- latitude of the destination
	 * @param lng_d -- longitude of the destination
	 * @return A list of RouteSegments, each of which is the maximal STRAIGHT line segment in a route.
	 * 
	 * Note that this method also stores the Google directions API response in the instance variable
	 * API_response.
	 * If there are some internal errors, return null, if the API returned path is empty, return an
	 * empty list. 
	 * 
	 */
	private ArrayList<RouteSegment> getDirections(double lat_o, double lng_o,
			double lat_d, double lng_d){

		ArrayList<RouteSegment> result = new ArrayList<RouteSegment>();
		
		//For the Google Directions API: an example query is like:
		//https://maps.googleapis.com/maps/api/directions/json?origin=Melbourne&destination=Canberra&region=au&key=
		//final String HOST_BASE = "https://maps.googleapis.com/maps/api/directions/json";
		final String ORIGIN_PARAM = "origin";
		final String DESTINATION_PARAM = "destination";
		final String REGION_PARAM = "region";
		final String TRAVEL_MODE_PARAM = "mode";
		final String API_KEY_PARAM = "key";
		final String test_region = "au"; //change this if the app needs to support other national regions
		final String travel_mode = "driving";
		final String my_key = "AIzaSyBaLZ9LFvlLmVL16xGDQkKOF5Ml69_JcSI";
		
		///////// 11/02/2015 Yu Sun: this approach doesn't work out ///////////
		// TODO 11/02/2015 Yu Sun: try to use UriBuilder
//		UriBuilder builder = UriBuilder
//				.fromUri("https://maps.googleapis.com/")
//				.path("maps/api/directions/json");
//			builder.queryParam(ORIGIN_PARAM, String.valueOf(lat_o) + "," + String.valueOf(lng_o));
//			builder.queryParam(DESTINATION_PARAM, String.valueOf(lat_d) + "," + String.valueOf(lng_d));
//			builder.queryParam(REGION_PARAM, test_region);
//			builder.queryParam(API_KEY_PARAM, my_key);
//			builder.queryParam(TRAVEL_MODE_PARAM, travel_mode);
//		builder.build();
		///////////////////////////////////////////////////////////////////////
		
		///////////////////////// For temporary usage: Yu Sun 11/02/2015 ////////////////////////
		String builder = "https://maps.googleapis.com/" + "maps/api/directions/json" + 
				"?" + ORIGIN_PARAM + "=" + String.valueOf(lat_o) + "," + String.valueOf(lng_o) +
				"&" + DESTINATION_PARAM + "=" + String.valueOf(lat_d) + "," + String.valueOf(lng_d) +
				"&" + REGION_PARAM + "=" + test_region +
				"&" + API_KEY_PARAM + "=" + my_key +
				"&" + TRAVEL_MODE_PARAM + "=" + travel_mode;
		//////////////////////////////////////////////////////////////////////////////////////////
		
		URL url = null;
		try {
			url = new URL(builder.toString());
		} catch (MalformedURLException e) {
			logger.error("Error building URL from: " + builder.toString(), e);
		}
		
		logger.debug("The requested URL is: "+url.toString());
		
		//TODO we first don't use the polyline points, if the result is not good, we'll upgrade
		try {
			//////////////// Error occurs /////////////////
			APIRequest apiRequest = new APIRequest();
			String res = apiRequest.getResponse(url);
			if( res == null || res.isEmpty() )
				return null;
			////////////// else the API response is not empty //////////////
			////// first store the response in the instance variable API_response,
			////// no matter what status code the Google directions server returns.
			this.API_response = res;
			logger.debug("The response is: " + API_response);
			//////
			//then we extract the route segments.
			JSONObject resJson = new JSONObject(res);
			if( resJson.optString("status").equals("OK") ){ //the API returns the true route
				
				JSONArray legs = resJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
				for(int i = 0; i < legs.length(); i++){ // for each leg
					
					JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
					for(int j = 0; j < steps.length(); j++){ // for each step, i.e., route segment
						
						JSONObject step = steps.getJSONObject(j);
						
						RouteSegment seg = new RouteSegment(
								step.getJSONObject("start_location").getDouble("lat"),
								step.getJSONObject("start_location").getDouble("lng"),
								step.getJSONObject("end_location").getDouble("lat"),
								step.getJSONObject("end_location").getDouble("lng")
								);
						result.add(seg);
					}
				}				
			} else { // the status code is not "OK", report error
				return null;
			}
		} catch (JSONException e) {
			logger.error("Error wrong json formats", e);
		}
		
		return result;
	}
	
	/**
	 * The class that represents the maximal STRAIGHT line segment in a route.
	 * @author Yu Sun
	 *
	 */
	private static class RouteSegment{
		
		public double lat_s, lng_s, lat_e, lng_e;
		/**
		 * Constructor of the class
		 * @param lat_s -- latitude of the starting point at one STRAIGHT line segment of the path
		 * @param lng_s -- longitude of the starting point at one STRAIGHT line segment of the path
		 * @param lat_e -- latitude of the ending point at one STRAIGHT line segment of the path
		 * @param lng_e -- longitude of the ending point at one STRAIGHT line segment of the path
		 */
		public RouteSegment(double lat_s, double lng_s,
				double lat_e, double lng_e){
			this.lat_s = lat_s; this.lng_s = lng_s; this.lat_e = lat_e; this.lng_e = lng_e;
		}
		
		@Override
		public String toString() {
			return "RouteSegment [lat_s=" + lat_s + ", lng_s=" + lng_s
					+ ", lat_e=" + lat_e + ", lng_e=" + lng_e + "]";
		}
	}

	/**
	 * Select the fuel stations that are within the maximum distance to the path
	 * @param segments -- A list of RouteSegments, each of which is the maximal STRAIGHT line segment in a route.
	 * 
	 * @return
	 *  i) A Json object with 1) the name of "fuel_station" and value of a Json array contains a 
	 * 	   list of the selected stations, and 2) the name of "direction" and value of the response
	 *     of google directions API call.
	 *  ii) If there are errors in any field, the corresponding value would be "error".
	 *  iii) If there are no query results, the corresponding value of "fuel_station" would be "empty".
	 *  One example result is {"fuel_station":"empty","direction":"error"}
	 *  
	 *  Note the current implementation is the straightforward nested for loops, i.e., check each fuel
	 *  station against each leg of the given path.
	 */
	private JSONObject filter(){
		
		JSONObject result = new JSONObject();
		
		try{
			////////////////////////// get direction //////////////////
			ArrayList<RouteSegment> segments = this.getDirections(lat_o, lng_o, lat_d, lng_d);
			if( segments == null ){
				// if the server runs into errors, this.API_response is null and we return error
				result.put(PathQuery.RESULT_DIRECTION_KEY, PathQuery.ERROR);
				result.put(PathQuery.RESULT_STATION_KEY, PathQuery.ERROR);
				logger.error("Error getting the direction segments");
				return result;
			}
			else if ( segments.isEmpty() ){
				result.put(PathQuery.RESULT_DIRECTION_KEY, PathQuery.ERROR);
				result.put(PathQuery.RESULT_STATION_KEY, PathQuery.EMPTY);
				logger.error("Warning the returned direction is empty");
				return result;
			}
			
			////////////////// added on 24/02/2015 by Yu Sun: for directions only ///////////////
			// at this point we know the Google API response is not null and not empty, therefore
			if( this.dist_r <= 0.0 ){
				
				result.put(PathQuery.RESULT_DIRECTION_KEY, this.API_response);
				result.put(PathQuery.RESULT_STATION_KEY, PathQuery.EMPTY);
				logger.info("Return the directions only");
				return result;
			}
			
			/////////////////// get table ///////////////////////
			//JSONArray all_rows = GetCouchDBWholeTable.getWholeTable(this.table_name);
			// Changed by Yu Sun on 06/03/2015 for efficiently fetching the whole table
			JSONArray all_rows = CouchDBWholeTable
					.getWholeTableStoredInSingleDoc(this.table_name, this.doc_id);
			if( all_rows == null || all_rows.length() == 0 ){ // the returned table is empty
				//there are some internal errors when retrieving the table from DB server
				result.put(PathQuery.RESULT_DIRECTION_KEY, this.API_response);
				result.put(PathQuery.RESULT_STATION_KEY, PathQuery.ERROR);
				logger.error("Error getting the searching (fuel_station) table");
				return result;
			}
			///////////////// join the direction and table ////////////////
			// Here we use the straightforward nested loop join
			JSONArray filter_result = new JSONArray();
			for(int i = 0; i < all_rows.length(); i++){ // for each station
				
				JSONObject sta = all_rows.getJSONObject(i);
				double lat_t = sta.getDouble(ConstantConfig.FUEL_STATION_COLUMN_LATITUDE);
				double lng_t = sta.getDouble(ConstantConfig.FUEL_STATION_COLUMN_LONGITUDE);
				for(int j = 0; j < segments.size(); j++){ // for each segment
					
					RouteSegment seg = segments.get(j);
					double dist = this.pathDistance(seg, lat_t, lng_t);
					if( dist <= this.dist_r ){
						filter_result.put( sta ); //only add the station once
						break;
					}
				}
			}
			//construct the most common result
			result.put(PathQuery.RESULT_DIRECTION_KEY, this.API_response);
			result.put(PathQuery.RESULT_STATION_KEY, filter_result);
			
		} catch (JSONException e){
			logger.error("Error wrong json formats", e);
		}
		return result;
	}
	
	/**
	 * Compute the distance between a fuel station and a RouteSegment (a straight line) on the path.
	 * @param seg -- a RouteSegment (a straight line) on the path
	 * @param lat_p -- latitude of the fuel station
	 * @param lng_p -- longitude of the fuel station
	 * @return the distance between the fuel station and the leg (a straight line segment) on the path
	 *  starts from the starting point and ends at the ending point.
	 * Note that we approximate the earth surface between the starting and ending point by
	 * a straight line segment between them
	 */
	private double pathDistance(
			RouteSegment seg,
			double lat_p, double lng_p		//fuel station location
			)
	{
		
		double lat_s = seg.lat_s, lng_s = seg.lng_s, lat_e = seg.lat_e, lng_e = seg.lng_e;
		
		////////////// convert the lat/lng to earth surface coordinate ///////////////
		// We use the Spherical Mercator projection. For details, refer to the private method 
		// sphericalMercatorProjection(double lat) in this class.
		
		// the earth distance per degree after projection
		// Explanation for the value:
		// Although the surface of Earth is best modeled by an oblate ellipsoid of revolution, 
		// for small scale maps the ellipsoid is approximated by a sphere of radius with
		// mean value of 6,371 km and circumference of 40,030 km.  
		// Only high-accuracy cartography on large scale maps requires an ellipsoidal model.
		// Dividing the circumference 40,030 km by 360 degrees gives us the above value.
		final double EARTH_DIST = 111.111;	// km
		
		// the longitude in the Spherical Mercator project is not changed.
		lat_s = sphericalMercatorProjection(lat_s);
		lat_e = sphericalMercatorProjection(lat_e);
		lat_p = sphericalMercatorProjection(lat_p);
		///////////////////////////////////////////////////////////////////////////////
		
		// Line2D.ptSegDist Returns the distance from a point to a line segment. The distance measured is 
		// the distance between the specified point and the closest point between the specified end points.
		// If the specified point intersects the line segment in between the end points, this method returns 0.0. 
		
		double dist = EARTH_DIST * Line2D.ptSegDist(lng_s, lat_s, lng_e, lat_e, lng_p, lat_p);
		
		return dist;
				
	}

	/**
	 * We use the Spherical Mercator projection, but not Equidistant projections such as the Azimuthal Equidistant
	 * projection. Mercator can can give a very good approximation between latitude -50 and 50.
	 * For more details, please refer to 
	 * 1. http://webhelp.esri.com/arcgisdesktop/9.2/index.cfm?TopicName=About_coordinate_systems_and_map_projections
	 * 2. http://www.icsm.gov.au/mapping/map_projections.html
	 * 3. http://www.jhlabs.com/java/maps/proj/
	 * 4. http://wiki.openstreetmap.org/wiki/Mercator
	 * 5. and http://stackoverflow.com/questions/5983099/converting-longitude-latitude-to-x-y-coordinate
	 * 
	 * @param lat -- the latitude to be projected
	 * @return
	 */
	private static double sphericalMercatorProjection(double lat){
		
		return Math.toDegrees( Math.log( Math.tan( Math.PI/4 + Math.toRadians(lat)/2 ) ) );
	}

	
//	/**
//	 * Yu Sun 29/01/2015 -- Deleted on 01/03/2015
//	 * Given this.table_name, it returns all the rows in this table.
//	 * Alert -- current implementation may take quite a long time to finish.
//	 * @return a json array containing all the rows in the table
//	 * If any error occurs, it returns an empty Json array
//	 */
//	private JSONArray getWholeTable(){
//		
//		// 29/01/2015 Yu Sun: as per the CouchDB API: http://docs.couchdb.org/en/latest/api/database/bulk-api.html
//		String whole_table = "_all_docs";
//
//		URL url = null;
//		try {
//			url = new URL(ConstantConfig.HOST + "/" + this.table_name + "/" + whole_table);
//		} catch (MalformedURLException e) {
//			logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + this.table_name + "/" + whole_table);
//		}
//		PutGetDataTask getAll = new PutGetDataTask(
//				PutGetDataTask.REQUEST_GET,
//				url
//				);
//		String res = getAll.getResult();
//		
//		// 29/01/2015 Yu Sun: parse the return json string and return a json array
//		JSONObject jsonObj = null;
//		JSONArray jsonArray_Id = null;
//		try {
//			jsonObj = new JSONObject(res);
//			jsonArray_Id = jsonObj.getJSONArray("rows");	
//		} catch (JSONException e) {
//			logger.error("Error when parse the returned json string:" + res, e.toString());
//		}
//		// 30/01/2015 Yu Sun: I really don't want to write like this. Previously I thought
//		// CouchDB will return all the fields in each row. But it turns out CouchDB only returns
//		// the Id of the docs, which is really annoying. I want to turn to Views. After checking
//		// the so called views, as commented above, it will require quite a long time to make
//		// it work, which conflicts the initial reasons we first use CouchDB. It may not be a
//		// good choice to use CouchDB. We will see.
//		int i = -1;
//		JSONObject idObj = null;
//		JSONArray resArray = new JSONArray();
//		try{
//			for(i = 0; i < jsonArray_Id.length(); i++){
//				
//				idObj = jsonArray_Id.getJSONObject(i);
//				String row_id = idObj.getString(ConstantConfig.FUEL_STATION_COLUMN_ID);
//				URL row_url = null;
//				try {
//					row_url = new URL(ConstantConfig.HOST + "/" + this.table_name + "/" + row_id);
//				} catch (MalformedURLException e) {
//					logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + this.table_name + "/" + row_id);
//				}
//				PutGetDataTask get_row = new PutGetDataTask(
//						PutGetDataTask.REQUEST_GET,
//						row_url
//						);
//				String row_str = get_row.getResult();
//				idObj = new JSONObject(row_str);
//				// Yu Sun 30/01/2015: a slightly better implementation is to do the filter here.
//				// However, since this function may be used by other methods, we just return the whole table.
//				resArray.put(idObj); // must be deep copy
//			}// end for
//		} catch (JSONException e) {
//			if( idObj != null )
//				logger.error("Error when processing the "+ i +"th row:" + idObj.toString());
//			else
//				logger.error("Error when parsing json array:" + jsonArray_Id);
//		}
//
//		return resArray;
//	}
	
}
