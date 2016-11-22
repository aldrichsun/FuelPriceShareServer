package com.example.spatialanalytics.function;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 29/01/2015
 * Read in the assigned data (each line represents one JSON object in the format of 
 * JSON string) and store the data into the database.
 * 
 * At present, we use CouchDB and insert each row (fuel station) one by one.
 * TODO
 * Later, we can upgrade it by utilizing the bulk insertion for CouchDB or
 * any other database we use.
 * 
 * Updated on 06/03/2015
 * We provide another method that reads in the assigned data (each line represents one
 * JSON object in the format of a JSON string) and store the data as a SINGLE document,
 * which means there is only one id (for instance, "id=all_stations" or "id=VIC_stations")
 * and we store all the rows (fuel stations) represented by a json array into the document
 * associated with the id. 
 * 
 * @author Yu Sun
 *
 */
public class BulkInsert {
	
	private static String LOG_TAG = BulkInsert.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(BulkInsert.class.getSimpleName());
	
	// Deleted by Yu Sun on 06/03/2015
//	private String filePath = null;
//	private String table_name = null;
	
	/**
	 * Yu Sun 29/01/2015:
	 * Constructor of the BulkInsert
	 */
	public BulkInsert(){
		// Deleted by Yu Sun on 06/03/2015
//		this.filePath = filePath;
//		this.table_name = table_name;
	}
	
	/**
	 * Yu Sun 06/03/2015
	 * This method reads in the assigned data (a json array where each line represents one
	 * row (fuel station) in the format of a JSON string) and store the data as a SINGLE document,
	 * which means there is only one doc id (for instance, "id=all_stations" or "id=VIC_stations")
	 * and we store all the rows (fuel stations) represented by a json array into the document
	 * associated with the id.
	 * 
	 * The structure of the stored json object in the DB is: {"doc_id": jsonArray}
	 * 
	 * @param filePath -- the file to read in
	 * @param table_name -- the name of the table in which the data is stored
	 * @param doc_id -- the assigned document id
	 * @return String "done" if success, "error" otherwise.
	 */
	public String insertAsSingleDoc( String filePath, String table_name, String doc_id ){
		
		RWLocalFile rw = new RWLocalFile();
		String content = rw.readToSingleString(filePath);
		if( content == null ){
			logger.error(LOG_TAG, "Cannot read from file: " + filePath);
			return "error";
		}
		
		URL url = null;
		try{
			JSONArray jsonArray = new JSONArray(content);
			
			url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
			
			JSONObject jsonObject = new JSONObject();
			try{
				jsonObject.put(doc_id, jsonArray);
			} catch( JSONException e ) {
				logger.error(LOG_TAG, "Error creating json object", e);
				return "error";
			}
			PutGetDataTask store = new PutGetDataTask(
					PutGetDataTask.REQUEST_PUT,
					url,
					jsonObject.toString()
					);
			
			String res = store.getResult().trim();
			// to print out the reason of failed insertion
			JSONObject resJson = new JSONObject(res); 
			if( !resJson.optString("error").isEmpty() ){
				logger.error(res);
				return "error";
			}
			else
				logger.trace(res);
			
		} catch (JSONException e){
			logger.error(LOG_TAG, "Error json array format in file: " + filePath, e);
			return "error";
		} catch (MalformedURLException e) {
			logger.error("Error format of URL: " + url, e);
			return "error";
		}
		
		return "done";
	}
	
	/**
	 * Yu Sun 29/01/2015:
	 * Read in the assigned data (each line represents one JSON object) and 
	 * store the data into the database.
	 * @param filePath -- the file to read in
	 * @param table_name -- the name of the table in which the data is stored
	 * @return String "done" if success, "error" otherwise.
	 */
	public String insertToDb( String filePath, String table_name ){
		
		RWLocalFile rw = new RWLocalFile();
		ArrayList<String> content = rw.readToStringArray( filePath ); 
		Iterator<String> c_it = content.iterator();
		int line_num = 0;
		URL url = null;
		String s = null;
		try {
			// 29/01/2015 Yu Sun: we identify the bad JSON format here, rather than in the
			// PutGetDataTask class, so that we can timely check the input file.
			while( c_it.hasNext() ){

				line_num++;
				// TODO improve the implementation efficiency by using bulk-insertion  
				// 29/01/2015 Yu Sun: this is a very bad implementation
				{
					s = c_it.next();
					JSONObject jsonObj = new JSONObject( s );
					String id = jsonObj.getString(ConstantConfig.FUEL_STATION_COLUMN_ID);
					
					url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + id);
					
					PutGetDataTask store = new PutGetDataTask(
							PutGetDataTask.REQUEST_PUT, // 29/01/2015 Yu Sun: Be careful about this parameter
							url,
							jsonObj.toString()
							);
					
					// 29/01/2015 Yu Sun: the user should address the insert conflict 
					// or any other errors by herself.
					String res = store.getResult().trim();
					// 29/01/2015 Yu Sun: to print out failed insertion
					JSONObject resJson = new JSONObject(res); 
					if( !resJson.optString("error").isEmpty() ){
						logger.error(res);
						return "error";
					}
					else
						logger.trace(res);
				}
			}
		} catch (JSONException e) {
			logger.error("Error format of JSON at line " + line_num + ": " + s +  " in file " + filePath, e.toString());
			return "error";
		} catch (MalformedURLException e) {
			logger.error("Error format of URL: " + url, e);
			return "error";
		}
		return "done";
	}
	
}