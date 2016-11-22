package com.spatialanalytics.config;

public class URLConfig {
	
	public static final String ip="http://10.100.230.171"; //128.250.26.228";
	//public static final String ip="http://spatialanalytics.cis.unimelb.edu.au";
	public static final String couchDBPort="5984";
	public static final String database="fuel_user";
	//public static final String prifoleImageFolder="user_profile_photo";
	
	
	public static String getCouchDBAPI()
	{
		return 	ip+":"+couchDBPort+"/"+database+"/";

	}
	
	
	public static String getCouchDBContributePriceAPI()
	{
		return 	ip+":"+couchDBPort+"/user_contribute_fuel_price/";

	}
	
	
	public static String getCouchDBContributeNoPetroStationAPI()
	{
		return 	ip+":"+couchDBPort+"/user_contribute_fuel_price_no_fuel_station/";

	}
	/**
	 * ZHAOHE API data resource
	 * @return
	 */
	public static String getFuelInforTest()
	{
		return 	ip+":"+couchDBPort+"/"+"user_contribute_fuel_price/test";
	}
	
	
	/**
	 * SUNYU API data resource
	 * @return
	 */
	
	public static String getPetroStationTest()
	{

		return "http://128.250.26.229:8080/FuelPriceSharingServer/RangeQueryServlet?lat=-37.842716&lng=144.883618&r_dist=10";
	}
	
	

	

}
