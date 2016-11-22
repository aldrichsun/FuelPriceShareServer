package com.spatialanalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.CreditManager;
import com.common.UpdateFuelPrice;
import com.spatialanalytics.config.ConstantConfig;
import com.spatialanalytics.config.URLConfig;
import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;
import com.spatialanalytics.model.MyTime;

/**
 * Servlet implementation class UploadRefinedResultServlet
 * receive the refined fuel result from the user
 * 
 * More comments added by Yu Sun on 02/04/2015
 * After confirming or editing recognized fuel type and price (and choosing the right station)
 * this class process the final contributed price.
 * 
 * FuelPriceImageProcessServlet is the class that accepts the image taken by the user, hand
 * the image to the text recognition server (which is currently developed and maintained by
 * He Zhao and Yuan Li), receive the recognized text and send the text back to the client end
 * for confirmation (and editing).
 */
public class UploadRefinedResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * for error control
	 */
	private final  String TAG="UploadRefinedResultServlet";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);

	private Logger logger = LogManager.getLogger(UploadRefinedResultServlet.class.getSimpleName());

	//////////// added by Yu Sun on 06/04/2015 //////////////
	UpdateFuelPrice updatePrice;
	////////////////////////////////////////////////////////

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadRefinedResultServlet() {
		super();
	
		////////////added by Yu Sun on 06/04/2015 //////////////
		updatePrice = new UpdateFuelPrice();
		////////////////////////////////////////////////////////
	}

	/**
	 * Commented by Yu Sun on 03/04/2015
	 * This function does the following two tasks:
	 * 
	 * i) store the contributed price into the table
	 * 		1) 'user_contribute_fuel_price', if there is a station id, which indicates the contribu-
	 * 		   ted price is valid.
	 *      2) 'user_contribute_fuel_price_no_fuel_station', otherwise.
	 *      
	 * ii) (by current implementation) give the user credits directly.
	 * 
	 * Updated on 06/04/2015 Yu Sun
	 * i) store the contributed price into the table
	 * 		1) 'user_contribute_fuel_price' if there is a station id, and
	 * 			we immediately update the price of the station by the contributed price.
	 * 		
	 * 		2) 'user_contribute_fuel_price_no_fuel_station' if there is no station id, and 
	 * 			we build a new station whose
	 * 				brand is selected by the user,
	 * 				location is the user's current location,
	 * 				provided fuel type and price is contributed by the user.
	 * 
	 * ii) (by current implementation) give the user credits directly.
	 * 
	 * 
	 * TODO
	 * i) The stored price is not "paired", which means we don't know which
	 * price is for which fuel type. We need improve that.
	 * 
	 * ii) Credits to the user shall be given after the contribution is validated.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter out = response.getWriter();
		logger.debug("-----UploadRefinedResultServlet-----");

		String act = request.getParameter("action");
		if(myExceptinHandler.isParameterEmpty(act))
		{
			out.println(myExceptinHandler.getJsonRequestParseError("action"));
			return;
		}
		act = act.toString();	

		String reply = myExceptinHandler.getJsonOK();
		try {

			if ( act.equals("Upload") )
			{
				/**
				 * get the data information
				 */
				String data = request.getParameter("json");
				if( myExceptinHandler.isParameterEmpty( data ) )
				{
					out.println(myExceptinHandler.getJsonRequestParseError(data));
					return;
				}
				//data = data.toString();

				JSONObject json = new JSONObject(data);
				logger.debug(json.toString());

				//parse the json
				///////////////////////////////////////////////////////////////////////
				//validate the JSON; it has to contain the following mandatory fields
				if(!json.has(ConstantConfig.KEY_CONTRIBUTE_PRICE_TRANSACTION_ID))
				{
					String errorMsg = "The field of KEY_CONTRIBUTE_PRICE_TRANSACTION_ID is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				if(!json.has(ConstantConfig.KEY_USER))
				{
					String errorMsg = "The field of KEY_USER is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				// Deleted by Yu Sun on 06/04/2015
//				if(!json.has(ConstantConfig.KEY_FUEL))
//				{
//					String errorMsg = "The field of KEY_FUEL is null";
//					logger.error(errorMsg);
//					reply = myExceptinHandler.getJsonError(errorMsg);
//					return;
//				}
//				if(!json.has(ConstantConfig.KEY_PETROL_STATION))
//				{
//					String errorMsg = "The field of KEY_PETROL_STATION is null";
//					logger.error(errorMsg);
//					reply = myExceptinHandler.getJsonError(errorMsg);
//					return;
//				}
				/////////////// added by Yu Sun on 06/04/0215 /////////////////
				if( !json.has(ConstantConfig.KEY_FUEL_PROVIDED) ){
					
					String errorMsg = "The field of KEY_FUEL_PROVIDED is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				///////////////////////////////////////////////////////////////
				if(!json.has(ConstantConfig.KEY_LATITUDE))
				{
					String errorMsg = "The field of KEY_LATITUDE is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				if(!json.has(ConstantConfig.KEY_LONGITUDE))
				{
					String errorMsg = "The field of KEY_LONGITUDE is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				///////////////////////////////////////////////////////////////////////
				
				// Commented by Yu Sun on 03/04/2015: Other fields of the stored json is already there,
				// here we only add the additional fields including contribute time, credit to user and
				// station id (if there is any).
				String doc_id = json.getString(ConstantConfig.KEY_CONTRIBUTE_PRICE_TRANSACTION_ID); //ID String
				String user_id = json.getString(ConstantConfig.KEY_USER);
				int credit_to_user = CreditManager.getContributeGain();

				json.put(ConstantConfig.COLUMN_CONTRIBUTE_TIME, MyTime.getCurrenttimeMillis());
				json.put(ConstantConfig.COLUMN_CREDIT_TO_USER, credit_to_user);

				CreditManager.addCredit(user_id, credit_to_user);

				/**
				 * parse the json
				 */
				String station_id = null;
				//modified by Han
				JSONObject fuelStationJson=json.getJSONObject(ConstantConfig.KEY_PETROL_STATION);
				
				if(!fuelStationJson.has(ConstantConfig.KEY_BRAND))
				{
					String errorMsg = "The field of KEY_BRAND is null";
					logger.error(errorMsg);
					reply = myExceptinHandler.getJsonError(errorMsg);
					return;
				}
				if( fuelStationJson.has("id") )
				{
					station_id = (json.getJSONObject(ConstantConfig.KEY_PETROL_STATION)).getString("id");
					json.put(ConstantConfig.KEY_PETROL_STATION, station_id);
					reply = DBController.performDirectPUT(URLConfig.getCouchDBContributePriceAPI(), doc_id, json.toString());
					
					///////////////////////////////////////////////////////////////////////////////
					// Added by Yu Sun on 06/04/2015: use another thread to update the fuel price
					final JSONArray new_type_and_price = json.getJSONArray(ConstantConfig.KEY_FUEL_PROVIDED);
					final String updateStationId = station_id;
					new Thread(){
						public void run() {
							updatePrice.updatePrice(updateStationId, new_type_and_price);
						}}.start();
					///////////////////////////////////////////////////////////////////////////////
				}
				else // no station id is found
				{
					String errorMsg="has no petrol station";
					logger.error(errorMsg);
					json.put(ConstantConfig.KEY_PETROL_STATION, station_id);
					reply = DBController.performDirectPUT(URLConfig.getCouchDBContributeNoPetroStationAPI(),doc_id, json.toString());
					
					
					///////////////////////////////////////////////////////////////////////////////
					// Added by Yu Sun on 06/04/2015: use another thread to insert the new station
					String brand = fuelStationJson.getString(ConstantConfig.KEY_BRAND);
					double latitude = json.getDouble(ConstantConfig.KEY_LATITUDE);
					double longitude = json.getDouble(ConstantConfig.KEY_LONGITUDE);
					JSONArray new_type_and_price = json.getJSONArray(ConstantConfig.KEY_FUEL_PROVIDED);
					int source = 2;
					final JSONObject new_station = new JSONObject();
					new_station.put(ConstantConfig.KEY_BRAND, brand);
					new_station.put(ConstantConfig.KEY_LATITUDE, latitude);
					new_station.put(ConstantConfig.KEY_LONGITUDE, longitude);
					new_station.put(ConstantConfig.KEY_FUEL_PROVIDED, new_type_and_price);
					new_station.put(ConstantConfig.KEY_SOURCE, source); // from user
					new Thread(){
						public void run() {
							updatePrice.insertStationAndUpdatePrice(new_station, null);
						}}.start();
					///////////////////////////////////////////////////////////////////////////////
				}
				logger.debug("now the updated information is : " + json.toString());
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
			reply = myExceptinHandler.getJsonError(e.toString());
		}
		out.println(reply);
		logger.debug("server run success (doesn't mean upload refined result success)");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		doGet(request,response);
	}

}
