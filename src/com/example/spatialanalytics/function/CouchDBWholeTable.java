package com.example.spatialanalytics.function;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This function retrieves the whole table either stored in a CouchDB so-called 'database' or
 * a single document in a 'database'.
 * For efficiency purposes, this class caches the whole table.
 * If the document is updated somewhere else, it is better that the cache is also updated directly.
 * @author Yu Sun
 *
 */
public class CouchDBWholeTable {
	
	private static final Logger logger = LogManager.getLogger(CouchDBWholeTable.class.getSimpleName());
	// cache the fetch table
	private static HashMap<String, JSONArray> tableFetchCache = new HashMap<String, JSONArray>();
	// cache the fetch document, which is indexed by the "doc_id", as from 06/03/2015, we
	// use a single document to represent multiple rows (fuel stations) even all rows in a table for
	// efficiency purposes.
	private static HashMap<String, JSONArray> documentFetchCache = new HashMap<String, JSONArray>();
	// keep the newest revision id for update
	private static String _rev = "";
	// keep the last update time, we write to the database only if the last database update time is
	// more than 10 minutes ago, in order to relieve the serve load
	private static Long DB_UPDATE_INTERVAL = 10 * 60 * 1000L;
	private static Long last_db_update_time = 0L;
	
	public CouchDBWholeTable(){
	}
	
	/*
	 * Update the cache for a single doc (which represents a whole table)
	 */
	public static boolean updateWholeTableStoredInSingleDoc(
			String table_name, String doc_id, JSONArray new_content){
		
		// We write to the database only if the last database update time is
		// more than 10 minutes ago, in order to relieve the serve load.
		Long current_time = System.currentTimeMillis();
		if( current_time - last_db_update_time >= DB_UPDATE_INTERVAL ){
			
			URL url = null;
			try {
				url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
			} catch (MalformedURLException e) {
				logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
				return false;
			}
			
			JSONObject newJson = new JSONObject();
			try {
				newJson.put(doc_id, new_content);	//the doc id is also the json key for the json array
				newJson.put("_rev", _rev);	//the revision number required
			} catch (JSONException e) {logger.error("Error json format", e);}
			
			PutGetDataTask updateDoc = new PutGetDataTask(
					PutGetDataTask.REQUEST_PUT,
					url,
					newJson.toString()
					);
			String res = updateDoc.getResult();
			if( res == null )
				return false;
			
			// 06/04/2015 Yu Sun: parse the return json string and show in log
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(res);
				if(jsonObj.has("error")){
					logger.error(res);
					return false;
				}else if( jsonObj.has("rev") ){ // keep the revison id
					_rev = jsonObj.getString("rev");		// it is weird that they use "rev" instead of "_rev"
				}
			} catch (JSONException e) {
				logger.error("Error when parse the returned json string:" + res, e);
				return false;
			}
			// the update succeed
			last_db_update_time = current_time;
		}
		
		documentFetchCache.put(table_name + "#" +doc_id, new_content);
		
		return true;
	}
	
	/*
	 * Update the cache for a whole table
	 */
	public static void updateCacheWholeTable(String table_name, JSONArray new_content){
		
		tableFetchCache.put(table_name, new_content);
	}
	
	
	/**
	 * Given a table name and a doc id (for instance "table_name = fuel_station", "doc_id = all_stations"),
	 * this method returns the single document that represents a whole table (which is efficient for retrieval
	 * but slow for update).
	 * @param table_name -- the table where the doc is
	 * @param doc_id -- the doc id
	 * @return
	 * i) null if any error occurs;
	 * ii) the whole table as a json array where each entry represents one row in the table.
	 */
	public static JSONArray getWholeTableStoredInSingleDoc( String table_name, String doc_id ){

		// Yu Sun 01/03/2015: A straightforward approach that assumes the server has large enough
		// (if not infinite) memory. We can improve the implementation later.
		if( documentFetchCache.containsKey(table_name + "#" +doc_id) ){
			return documentFetchCache.get(table_name + "#" +doc_id);
		}
		
		URL url = null;
		try {
			url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
		} catch (MalformedURLException e) {
			logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
			return null;
		}
		PutGetDataTask getAll = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getAll.getResult();
		if( res == null )
			return null;
		
		// 06/03/2015 Yu Sun: parse the return json string and return a json array
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;
		try {
			jsonObj = new JSONObject(res);
			jsonArray = jsonObj.getJSONArray( doc_id ); //the doc id is also the json key for the json array
			_rev = jsonObj.getString("_rev");	//keep the revision number
		} catch (JSONException e) {
			logger.error("Error when parse the returned json string:" + res, e);
			return null;
		}
		
		documentFetchCache.put(table_name + "#" +doc_id, jsonArray);

		return jsonArray;
	}
	
	/**
	 * 06/03/2015 Yu Sun: Do NOT recommend to use as it is very slow when the table is fetched first
	 * time, which is even unacceptable when the table consists of thousands of rows.
	 * Later we may use the "lat#lng" as the doc id, which can be retrieved by one HTTP request (with
	 * "_all_docs" api). 
	 * 
	 * Yu Sun 29/01/2015
	 * Given this.table_name, it returns all the rows in this table.
	 * Alert -- current implementation may take quite a long time to finish.
	 * @return all rows in the table in a json array
	 */
	public static JSONArray getWholeTable( String table_name ){
		
		// Yu Sun 01/03/2015: A straightforward approach that assumes the server has large enough
		// (if not infinite) memory. We can improve the implementation later.
		if( tableFetchCache.containsKey(table_name) ){
			return tableFetchCache.get(table_name);
		}
		
		// 29/01/2015 Yu Sun: as per the CouchDB API: http://docs.couchdb.org/en/latest/api/database/bulk-api.html
		String whole_table = "_all_docs";

		URL url = null;
		try {
			url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + whole_table);
		} catch (MalformedURLException e) {
			logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + whole_table);
		}
		PutGetDataTask getAll = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getAll.getResult();
		
		// 29/01/2015 Yu Sun: parse the return json string and return a json array
		JSONObject jsonObj = null;
		JSONArray jsonArray_Id = null;
		try {
			jsonObj = new JSONObject(res);
			jsonArray_Id = jsonObj.getJSONArray("rows");	
		} catch (JSONException e) {
			logger.error("Error when parse the returned json string:" + res, e.toString());
		}
		// 30/01/2015 Yu Sun: I really don't want to write like this. Previously I thought
		// CouchDB will return all the fields in each row. But it turns out CouchDB only returns
		// the Id of the docs, which is really annoying. I want to turn to Views. After checking
		// the so called views, as commented above, it will require quite a long time to make
		// it work, which conflicts the initial reasons we first use CouchDB. It may not be a
		// good choice to use CouchDB. We will see.
		int i = -1;
		JSONObject idObj = null;
		JSONArray resArray = new JSONArray();
		try{
			for(i = 0; i < jsonArray_Id.length(); i++){
				
				idObj = jsonArray_Id.getJSONObject(i);
				String row_id = idObj.getString(ConstantConfig.FUEL_STATION_COLUMN_ID);
				URL row_url = null;
				try {
					row_url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + row_id);
				} catch (MalformedURLException e) {
					logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + row_id);
				}
				PutGetDataTask get_row = new PutGetDataTask(
						PutGetDataTask.REQUEST_GET,
						row_url
						);
				String row_str = get_row.getResult();
				idObj = new JSONObject(row_str);
				// Yu Sun 30/01/2015: a slightly better implementation is to do the filter here.
				// However, since this function may be used by other methods, we just return the whole table.
				resArray.put(idObj); // must be deep copy
			}// end for
		} catch (JSONException e) {
			if( idObj != null )
				logger.error("Error when processing the "+ i +"th row:" + idObj.toString());
			else
				logger.error("Error when parsing json array:" + jsonArray_Id);
		}

		// put the fetch result in the cache hashmap
		tableFetchCache.put(table_name, resArray);
		
		return resArray;
	}

}
