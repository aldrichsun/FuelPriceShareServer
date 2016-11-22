package com.spatialanalytics.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.spatialanalytics.function.RangeQuery;
import com.spatialanalytics.config.ConstantConfig;
import com.spatialanalytics.config.URLConfig;

/**
 * 
 * @author Han
 * use to get the fuel station given the latitude and longitude information
 *
 * Update history:
 * Yu Sun 04/04/2015: change class name from SUNYUAPI to NearbyStationAPI
 */

public class NearbyStationAPI {
	
	private final static String TAG = "NearbyStationAPI";
	private final static MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);
	private static RangeQuery rangeQuery;
	private static Logger logger = LogManager.getLogger(NearbyStationAPI.class.getSimpleName());

	public static  ArrayList<JSONObject>  getPetroStations(
			boolean canGetLocation, double latitute, double longitude)
	{
		ArrayList<JSONObject> petrostations = new ArrayList<JSONObject>();
		logger.debug("---NearbyStationAPI----");

		if(canGetLocation)
		{
			try {
				
				//JSONObject json = new JSONObject(performGet());
				rangeQuery = new RangeQuery(latitute, longitude, ConstantConfig.DEFAULT_RANGE_DIST);
				
				
				JSONObject json = rangeQuery.internalGetResult();
				if(!json.has("error")){
					logger.debug("parsing jsonarray...");
					JSONArray jsonArray = json.getJSONArray(ConstantConfig.KEY_PETROL_STATION);
					for(int i=0; i<jsonArray.length(); i++)
					{
						logger.debug("parsing jsonarray..."+i);
						petrostations.add(jsonArray.getJSONObject(i));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				myExceptinHandler.getJsonError(e.toString());
			}
			logger.debug("petrostations: "+petrostations.toString());
			logger.debug("petrostations: "+petrostations.size());
		}else
		{			
			logger.error(TAG,"canGetLocation is "+canGetLocation);
		}
		return petrostations;
	}

	/**
	 * Author: Han Li
	 * For test purpose only!
	 * @return
	 */
	public static String performGet() 
	{ 
		try 
		{
			HttpURLConnection connection;

			String returnResponse = "";
			URL url;
			
			/* Commented by Yu Sun on 02/04/2015: Why don't use user's real current location? */
			/* We shall change it to calling the range query function */
			String dbUrl = URLConfig.getPetroStationTest();
			
			logger.debug(dbUrl);

			url = new URL(dbUrl);

			connection = (HttpURLConnection)url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(ConstantConfig.MAX_TIME_OUT);//60000

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				returnResponse = returnResponse+inputLine;
			in.close(); 

			return returnResponse;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return myExceptinHandler.getJsonError(e.toString());

			//						String errorMsg="{\"error\":"+"\""+e.toString()+"\""+"}";return errorMsg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//						String errorMsg="{\"error\":"+"\""+e.toString()+"\""+"}";return errorMsg;
			return myExceptinHandler.getJsonError(e.toString());

		}
	}

	

}