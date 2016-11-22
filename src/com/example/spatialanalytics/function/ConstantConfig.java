package com.example.spatialanalytics.function;

import com.common.Constant;

/**
 * This class provides the constants used across the server side code.
 * At present, we hard code the values of the constants. Later, we can upgrade
 * it by making it read the values via a configuration file.
 * @author Yu Sun 29/01/2015
 */
public class ConstantConfig {
	
	/*For CouchDB configuration*/
	//public static final String HOST = "http://spatialanalytics.cis.unimelb.edu.au:5984";
	public static final String HOST = Constant.HOST_IP + ":" + Constant.couchDB_port;
	
	/*For CRUD database*/
	public static final String PARAM_TABLE_NAME = "table_name";
	public static final String PARAM_ROW_ID = "row_id";
	public static final String PARAM_JSON_STRING = "json_string";
	
	/*For user credit management*/
	public static final String PARAM_CREDIT_USER_ID = "user_id";
	
	/*For range query*/
	public static final String PARAM_ADDRESS = "addr";
	public static final String PARAM_LATITUDE = "lat";
	public static final String PARAM_LONGITUDE = "lng";
	public static final String PARAM_RANGE_DISTANCE = "r_dist";
	
	/*For path query*/
	public static final String PARAM_ORIGIN = "origin";
	public static final String PARAM_DESTINATION = "destination";
	public static final String PARAM_PATH_DISTANCE = "path_dist";
	
	/*For data model table name*/
	public static final String FUEL_STATION_TABLE_NAME = "fuel_station";
	public static final String ALL_STATIONS_DOC_ID = "all_stations"; // 06/03/2015 change to each doc as a table
	public static final String VIC_STATIONS_DOC_ID = "vic_stations"; // not used
	
	/*For data model field/column name, i.e., JSON field name or TABLE column name*/
	public static final String FUEL_STATION_COLUMN_ID = "id";
	public static final String FUEL_STATION_COLUMN_LONGITUDE = "longitude";
	public static final String FUEL_STATION_COLUMN_LATITUDE = "latitude";
	public static final String FUEL_STATION_COLUMN_BRAND = "brand";
	public static final String FUEL_STATION_COLUMN_SHORT_NAME = "short_name";
	public static final String FUEL_STATION_COLUMN_FUEL_PROVIDED = "fuel_provided";
	public static final String FUEL_STATION_COLUMN_FUEL_PROVIDED_NAME = "fuel_name";
	public static final String FUEL_STATION_COLUMN_FUEL_PROVIDED_PRICE = "price";
	
	/*For location history*/
	public static final String LOCATION_HISTORY_COLUMN_USER_ID = "user_id";
	public static final String LOCATION_HISTORY_COLUMN_LOCATION_HISTORY = "location_history";
	//public static final String PARAM_ADDRESS = "addr";
	
	
	
	
	/*Some very common functions*/
	
	//From: http://stackoverflow.com/questions/837872 \\
	//	/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	//Refer to: http://www.movable-type.co.uk/scripts/latlong.html
	public static double earthDistance(double lat1, double lng1, double lat2, double lng2) {
		
	    double earthRadius = 6371; //kilometers
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    
	    return dist;
    }

}
