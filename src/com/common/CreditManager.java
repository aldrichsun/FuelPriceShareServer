package com.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.PutGetDataTask;

/**
 * Yu Sun 01/04/2015
 * This class manages the user's credit.
 * We only deduct the credit for range query.
 * The current implementation is as follows:
 * 		i) initially, the user has 10 credits,
 * 		ii) each range query uses 1 credit,
 * 		iii) each time the user contributes price, he or she gains 10 credits.
 * 
 * @author Yu Sun
 */
public class CreditManager {
	
	private static final Logger logger = LogManager.getLogger(CreditManager.class.getSimpleName());
	
	private static final String table_name = "fuel_user";
	
	private static final int INITIAL_CREDIT = 10;
	private static final int RANGE_QUERY_CREDIT_USAGE = 1;
	private static final int CONTRIBUTE_GAIN = 10;
	
	/**
	 * Check whether the user has sufficient credit for the (range) query.
	 * @param user_id -- the user identity
	 * @return
	 * i) true, 
	 * 		if the user can afford at least one time query or
	 * 		if any error occurs during fetching data from database
	 * ii) false, otherwise
	 */
	public static boolean hasSufficientCredit( String user_id ){
		
		URL url = null;
		try {
			url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + user_id);
		} catch (MalformedURLException e) {
			logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + user_id);
			return true;
		}
		PutGetDataTask getUser = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getUser.getResult();
		if( res == null ) //internal error occurs when getting user's info
			return true;
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(res);
			if( jsonObj.optString("error").equals("not_found") ){ //no such user exists
				logger.error("no such user is found, whose id is " + user_id);
				logger.error("If this happens frequently, please check the security of the server and API.");
				return false;
			}
			//else check the user credit
			int remaining_credit = jsonObj.getInt(Constant.COLUMN_USER_CREDIT);
			if( remaining_credit < getRangeQueryCreditUsage() )
				return false;
			
		} catch (JSONException e) {
			logger.error("Error when parse the returned json string:" + res, e);
			return true;
		}
				
		return true;
	}
	
	/**
	 * Given the user id, this function deduct the credit when the user successfully
	 * get a valid query result.
	 * @param user_id -- the user
	 * 
	 */
	public static void deductCredit( String user_id ){

		updateCredit( user_id, 0 - getRangeQueryCreditUsage() );
	}
	
	/**
	 * This function adds credit to the user
	 * @param user_id -- the user
	 * @param credit_gain -- the amount of credit to be added
	 *  
	 */
	public static void addCredit( String user_id, int credit_gain ){
		
		updateCredit( user_id, getContributeGain() );
	}
	
	/**
	 * This function updates the credit of the given user by adding difference
	 * (maybe negative) to the current credit.
	 * 
 	 * If error occurs, we shall update the credit again (later).
	 * But at present, for simplicity, let's assume there will be no error in this process.
	 * 
	 * @param user_id -- the user
	 * @param difference -- the difference (maybe negative) 
	 */
	private static void updateCredit( String user_id, int difference ){
		
		URL url = null;
		try {
			url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + user_id);
		} catch (MalformedURLException e) {
			logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + user_id);
			return;
		}
		PutGetDataTask getUser = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String res = getUser.getResult();
		if( res == null ) //internal error occurs when getting user's info
			return;
		
		JSONObject jsonObj = null;
		try {
			
			jsonObj = new JSONObject(res);
			int remaining_credit = jsonObj.getInt(Constant.COLUMN_USER_CREDIT);			
			remaining_credit += difference;
			jsonObj.put(Constant.COLUMN_USER_CREDIT, remaining_credit);
			
		} catch (JSONException e) {
			logger.error("Error when parse the returned json string:" + res, e);
			return;
		}
		
		///////////////////////////////////////////////////////////
		//Put the user json object back to the DB
		PutGetDataTask putUser = new PutGetDataTask(
				PutGetDataTask.REQUEST_PUT,
				url,
				jsonObj.toString()
				);
		res = putUser.getResult();
		if( res == null ) //internal error occurs when getting user's info
			return;
		JSONObject resObj = null;
		try {
			resObj = new JSONObject(res);
			if( !resObj.toString().contains("error") )
				logger.info("User " + user_id + "'s new credit has been updated");
			
		} catch (JSONException e) {
			logger.error("Error when parse the returned json string:" + res, e);
			return;
		}
		
	}
	
	////// To implement more complicated credit system, change these functions ////////  
	
	public static int getRangeQueryCreditUsage(){
		return RANGE_QUERY_CREDIT_USAGE;
	}
	
	public static int getInitialCredit(){
		return INITIAL_CREDIT;
	}
	
	public static int getContributeGain(){
		return CONTRIBUTE_GAIN;
	}
}
