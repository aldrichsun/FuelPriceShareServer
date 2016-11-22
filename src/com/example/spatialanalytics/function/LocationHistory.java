package com.example.spatialanalytics.function;

import java.net.MalformedURLException;
import java.net.URL;
//import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class processes (stores and returns) the user's search history, which forms 
 * part of the search auto-complete.
 * The current design is that we keep ALL the user's search addresses, but when retrieving
 * we return only the k most recent addresses (k can be tuned according to bandwidth).
 * 
 * @author Yu Sun on 03/03/2015
 */
public class LocationHistory {
	
	private static final Logger logger = LogManager.getLogger(LocationHistory.class.getSimpleName());
	
	public static final String LOC_HISTORY_TABLE_NAME = ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY;
	
	private static final String LOCATION_HISTORY_KEY = ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY;
	private static final String ERROR_VALUE = "error";
	private static final int MAX_RETURN_NUMBER = 29;	// K
	
	/**
	 * Given the user id and the address to record, this function stores the address in the DB.
	 * Currently, if the store operation fails, we do nothing.
	 * @param u_id -- user id
	 * @param address -- user search address (each time we only record one address, as we need to
	 * maintain a proper order of these addresses, which is by default the function calling order.
	 */
	public void recordLocationHistory(String u_id, String address){
		
		String url_builder = ConstantConfig.HOST + "/" + LOC_HISTORY_TABLE_NAME + "/" + u_id;
		URL url;
		try {
			url = new URL(url_builder);
		} catch (MalformedURLException e) {
			logger.error("Melformed url from: " + url_builder, e);
			return;
		}
		
		PutGetDataTask getData = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getData.getResult();
		
		int added_pos = -1;
		JSONObject loc_history = null;
		try{
			if( res.contains("\"error\":\"not_found\"") ){ ///////// create new user record
				logger.info("User " + u_id + " has no location history");
				loc_history = new JSONObject();
				JSONArray new_array = new JSONArray();
				new_array.put( address ); // store the address in the json array
				loc_history.put( ConstantConfig.LOCATION_HISTORY_COLUMN_USER_ID , u_id );
				loc_history.put( ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY, new_array);
			}
			else{	///////// update user records
				loc_history = new JSONObject(res);
				JSONArray loc_array = loc_history.getJSONArray( 
						ConstantConfig.LOCATION_HISTORY_COLUMN_LOCATION_HISTORY );
				added_pos = this.addedBefore(loc_array, address);
				if( added_pos != -1 ){
					loc_array.remove( added_pos );
				}
				loc_array.put( address );
			}
		} catch (JSONException e){
			logger.error("Json operation error for user " + u_id + " and address " + address, e);
			return;
		}
		
		// else store the object into DB
		PutGetDataTask storeData = new PutGetDataTask(
				PutGetDataTask.REQUEST_PUT,
				url,
				loc_history.toString()
				);
		String res_put = storeData.getResult();
		if( res_put == null || !res_put.contains("\"ok\":true")){
			logger.error("Error when storing: " + loc_history.toString() + ". The DB server "
					+ "response is: " + res_put);
		}
		return;
	}
	
	/**
	 * Given the user id, this function returns a json object in which the key is LOCATION_HISTORY_KEY and value is a json array containing
	 * the location history addresses (in the format of strings) whose maximum length is MAX_RETURN_NUMBER.
	 * @param u_id -- the user id
	 * @return 
	 * 1) a json object in which the key is LOCATION_HISTORY_KEY and value is a json array containing
	 * the location history addresses (in the format of strings).
	 * 2) a json object in which the key is LOCATION_HISTORY_KEY and value is ERROR_VALUE when any error
	 * occurs. 
	 */
	public JSONObject getLocationHistory(String u_id ){
		
		JSONObject result = new JSONObject();
		JSONObject errorObj = new JSONObject();
		
		String url_builder = ConstantConfig.HOST + "/" + LOC_HISTORY_TABLE_NAME + "/" + u_id;
		URL url;
		try {
			url = new URL(url_builder);
		} catch (MalformedURLException e) {
			logger.error("Melformed url from: " + url_builder, e);
			try {
				errorObj.put(LOCATION_HISTORY_KEY, ERROR_VALUE);
			} catch (JSONException json_e) {
			}
			return errorObj;
		}
		
		PutGetDataTask getData = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getData.getResult();
		if( res == null || !res.contains("\"user_id\":\""+u_id+"\"") ){
			logger.info("Invalid request from: " + url_builder);
			try {
				errorObj.put(LOCATION_HISTORY_KEY, ERROR_VALUE);
			} catch (JSONException json_e) {
			}
			return errorObj;
		}
		
		//otherwise the returned MAX_RETURN_NUMBER recent results in a json array
		try {
			JSONObject resJson = new JSONObject(res);
			JSONArray address_list = resJson.getJSONArray( LOCATION_HISTORY_KEY );
			JSONArray res_list = new JSONArray();
			//for(int i = 0 ; i <= address_list.length() -1 && i <= MAX_RETURN_NUMBER - 1; i++ )
			for(int i = address_list.length() - 1; 
					i >= 0 && i >= address_list.length() - MAX_RETURN_NUMBER; i--){
				res_list.put( address_list.get(i) );
			}
			result.put(LOCATION_HISTORY_KEY, res_list);
		} catch (JSONException e) {
			logger.error("Error json array: " + res.toString(), e);
			try {
				errorObj.put(LOCATION_HISTORY_KEY, ERROR_VALUE);
			} catch (JSONException json_e) {
			}
			return errorObj;
		}
		
		return result;
	}
	
	/**
	 * Check whether the address has been added before.
	 * (Currently, the naive implementation)
	 * @param address_array -- the address array
	 * @param address -- the address to be checked
	 * @return
	 * i) the index of the address in the address array if the address has been added before
	 * ii) -1, if the address is a new one
	 * @throws JSONException 
	 */
	private int addedBefore(JSONArray address_array, String address) throws JSONException{
		
		// check whether the address has been added before (I really don't like this part)
		
		//HashSet hashSet = new HashSet(); // add the addresses linearly but only one query...
		for(int i = 0; i < address_array.length(); i++){
			
			String addr = address_array.getString(i);
			if( addr.equalsIgnoreCase( address ) )
				return i;
		}
		return -1;
	}

}
