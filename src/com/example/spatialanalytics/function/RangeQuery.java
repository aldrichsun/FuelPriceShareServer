package com.example.spatialanalytics.function;

//import java.net.MalformedURLException;
//import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.CreditManager;

/**
 * Given a location (lat, lng), this query returns the fuel stations within a
 * distance range.
 * At present, the detail implementation is that we get the whole table of fuel_station
 * and then sequentially check each fuel station.
 * 
 * 06/02/2015 Yu Sun: 
 * 		If the query result is empty, it returns a JSON object with an empty JSON array.
 * 		If there are internal server error, it returns a json object {"table_name":"error"}
 *
 * Yu Sun 01/04/2015: range query error type documentation
 * (all error handling shall be in a single place)
 * Type 3001 : not sufficient credit (including user id does not exist, which makes the credit 0)
 * Type 3002 : internal DB server error
 * 
 * Update history:
 * Yu Sun 01/04/2015: Check the remaining credit of the user, only if the
 * remaining credit is larger than or equal to the query credit deduction,
 * we return the query result and deduct query credit from the user's total
 * credit.
 * 
 * <p>
 * TODO
 * To upgrade this, we can either 1) use the VIEW method provided by CouchDB or 2) build
 * an index such as R-tree to improve the efficiency.
 * @author Yu Sun 29/01/2015
 * <p>
 * Additional notes: 30/01/2015
 * All advanced queries (such as select from a range) in CouchDB are handled by “views”. 
 * All the views are created and processed by Map/Reduce functions. All the Map/Reduce 
 * functions are exampled by Javascript, and it’s not clear how to write the Map/Reduce
 * functions in Java. These difficulties preclude us from developing an efficient 
 * implementation in a very short time. To conclude, we don’t care too much about efficiency, 
 * and hence always choose the straightforward approach for implementation.
 * 
 */
public class RangeQuery {
	
	private static final Logger logger = LogManager.getLogger(RangeQuery.class.getSimpleName());
	
	//change this if query other points than fuel stations
	private String table_name = ConstantConfig.FUEL_STATION_TABLE_NAME;
	// added by Yu Sun on 06/03/2015 for efficiently fetching the whole table
	private String doc_id = ConstantConfig.ALL_STATIONS_DOC_ID;
	
	private static final String ERROR = "error";
	
	private double lng;
	private double lat;
	private double range_dist = 5; //kilometers -- Default value 5km
	private String user_id;
	
	/**
	 * Constructor of the RangeQuery class
	 * Yu Sun 29/01/2015
	 * @param lat -- latitude
	 * @param lng -- longitude
	 * @param range_dist -- the radius of the range
	 * @param user_id -- the user who issues the query
	 */
	public RangeQuery(String user_id, double lat, double lng, double range_dist){
		this.lat = lat;
		this.lng = lng;
		this.range_dist = range_dist;
		this.user_id = user_id;
	}
	
	/**
	 * Constructor of the RangeQuery class
	 * Yu Sun 29/01/2015
	 * @param lat -- latitude
	 * @param lng -- longitude
	 * @param range_dist -- the radius of the range
	 * @param table_name -- the name of the query table, e.g., fuel_station
	 * @param user_id -- the user who issues the query
	 */
	public RangeQuery(String user_id, double lat, double lng, double range_dist, String table_name){
		this.lat = lat;
		this.lng = lng;
		this.range_dist = range_dist;
		this.table_name = table_name;
		this.user_id = user_id;
	}
	
	
	/**
	 * Yu Sun 02/04/2015
	 * Constructor used internally
	 */
	public RangeQuery(double lat, double lng, double range_dist){
		this.lat = lat;
		this.lng = lng;
		this.range_dist = range_dist;
	}
	
	
	/**
	 * Yu Sun 29/01/2015
	 * Get the result of the range query.
	 * 
	 * Yu Sun 01/04/2015
	 * Deduct the user's credit is the returned result is non-empty.
	 * 
	 * @return
	 * i) the fuel stations in the range in a json object with a key
	 * being the table name and value being the fuel stations as a json array, i.e., 
	 * {"%table_name (fuel_station)":["list of points (fuel stations)"]};
	 * 
	 * ii) if there are no fuel stations in the range, the json array is empty;
	 * 
	 * iii) json object {"error":3002} if internal DB error occurs in the process;
	 * 
	 * iv) json object {"error":3001} if the user doesn't have sufficient credit.
	 */
	public String getResult(){
		
		//return this.filter(GetCouchDBWholeTable.getWholeTable(this.table_name));
		// Changed by Yu Sun on 06/03/2015 for efficiently fetching the whole table
//		return this.filter(
//				GetCouchDBWholeTable.getWholeTableStoredInSingleDoc(this.table_name, this.doc_id),
//				this.table_name);
		/**
		 * First check the user's credit. 
		 * If the user doesn't has sufficient credit, we return json object
		 * {"error":1} (which means not sufficient credit).
		 */
		if( !CreditManager.hasSufficientCredit( user_id ) ){
			JSONObject res = new JSONObject();
			try{
				res.put(ERROR, 3001);
			}catch (JSONException e){}	
			return res.toString();	
		}
		/**else if the returned result is not empty, deduct the credit */
		JSONObject res = this.filter(
				CouchDBWholeTable.getWholeTableStoredInSingleDoc(this.table_name, this.doc_id),
				this.table_name
			);
		////////// deduct credit /////////////
		try{
			if( res.optInt(ERROR) == 0 && //there is no internal DB error
				res.getJSONArray(this.table_name).length() > 0 ){ //the returned result is not empty
				// deduct the credit
				CreditManager.deductCredit( user_id );
			}
		}catch (JSONException e){
			logger.error("Error when deducting the user's credit");
		}
		//////////////////////////////////////
		//return the result anyway
		return res.toString();
	}
	
	
	/**
	 * Yu Sun 02/04/2015
	 * Used exclusively for internal program.
	 * Get the result of a range query. No credit is checked or deducted.
	 * 
	 * @return
	 * i) the fuel stations in the range in a json object with a key
	 * being the table name and value being the fuel stations as a json array, i.e., 
	 * {"%table_name (fuel_station)":["list of points (fuel stations)"]};
	 * 
	 * ii) if there are no fuel stations in the range, the json array is empty;
	 * 
	 * iii) json object {"error":3002} if internal DB error occurs in the process;
	 */
	public JSONObject internalGetResult(){
		
		return this.filter(
				CouchDBWholeTable.getWholeTableStoredInSingleDoc(this.table_name, this.doc_id),
				this.table_name);
	}

	
	/**
	 * Yu Sun 29/01/2015
	 * Select out the fuel stations that are in the preferred range.
	 * @param jsonArray -- all the rows in the table to select from.
	 * @param table_name -- the key of the result json object
	 * @return
	 * i) json object containing the information of the fuel stations in the range
	 * with the key being the table name and value being the fuel stations in a json array;
	 * ii) if there are no fuel stations in the range, the json array is empty;
	 * iii) json obejct {"error":2} is any error occurs.
	 */
	private JSONObject filter(JSONArray jsonArray, String table_name){
		
		JSONObject res = new JSONObject();
		
		if( jsonArray == null ){
			try{
				res.put(ERROR, 3002);
			}catch (JSONException e){}
			
			return res;
		}
		
		int i = -1;
		JSONObject jsonObj = null;
		try{
			JSONArray filter_result = new JSONArray();
			for(i = 0; i < jsonArray.length(); i++){
				
				jsonObj = jsonArray.getJSONObject(i);
				double lat = jsonObj.getDouble(ConstantConfig.FUEL_STATION_COLUMN_LATITUDE);
				double lng = jsonObj.getDouble(ConstantConfig.FUEL_STATION_COLUMN_LONGITUDE);
				double distance = ConstantConfig.earthDistance(lat, lng, this.lat, this.lng);
				if( distance <= this.range_dist ){
					filter_result.put(jsonObj);
				}
				//else continue
			}
			// 29/01/2015 Yu Sun: We use the table name as the key for the result JSON array
			res.put(table_name, filter_result);
			
		}catch (JSONException e){
			if( jsonObj != null )
				logger.error("Error when filter the json string:" + jsonObj.toString());
			else
				logger.error("Error the "+ i +"th object in " + jsonArray.toString() + " is null");
			
			// retur the error object
			JSONObject errorObj = new JSONObject();
			try{
				errorObj.put(ERROR, 3002);
			}catch (JSONException error_e){}
			return errorObj;
		}
		
		return res;
	}

}