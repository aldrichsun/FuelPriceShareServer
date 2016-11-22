package com.spatialanalytics.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spatialanalytics.config.URLConfig;
import com.spatialanalytics.servlet.RegisterServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by hanl4 on 29/01/2015.
 */
public class ZHAOHEAPI {


	/**
	 * Fuel Settings Newest Settings
	 */
	static final String KEY_FUEL_PRICE = "price";
	static final String KEY_FUEL_BRAND = "fuel";

	static final String KEY_RECT_LEFT = "left";
	static final String KEY_RECT_TOP = "top";
	static final String KEY_RECT_RIGHT = "right";
	static final String KEY_RECT_BOTTOM = "bottom";

	static final String FLAG_IS_SELECTED="isSelected";
	
	private final static String TAG="ZHAOHEAPI";
	private final static MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);

	private static Logger logger = LogManager.getLogger(ZHAOHEAPI.class.getSimpleName());



	public static ArrayList<JSONObject> getFuelObject() {
		ArrayList<JSONObject> allfuelPrices= new ArrayList<JSONObject>();
		try {


			JSONObject json=new JSONObject(performGet());
			if(!json.has("error")){

				JSONArray jsonArray=json.getJSONArray("values");
				for(int i=0;i<jsonArray.length();i++)
				{
					allfuelPrices.add(jsonArray.getJSONObject(i));
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
	
			myExceptinHandler.getJsonError(e.toString());


		}
		logger.debug("allfuelPrices: "+allfuelPrices.toString());


		return allfuelPrices;
	}


	public static String performGet() 
	{ 
		try {
			HttpURLConnection connection;

			String returnResponse = "";
			URL url;
			String dbUrl = URLConfig.getFuelInforTest();

			url = new URL(dbUrl);

			connection = (HttpURLConnection)url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(3000);

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


	public static ArrayList<JSONObject> getFuelObject2() {
		ArrayList<JSONObject> 	allfuelPrices;
		allfuelPrices = new ArrayList<JSONObject>();

		try {



			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "E85").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 55).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 100).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "ULP").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 105).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 150).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "E10").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 155).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 200).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "PULP").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 205).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 250).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "UPULP").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 255).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 300).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "LPG").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 305).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 350).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "tDiesel").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 355).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 400).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "Biodiesel").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 405).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 450).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_BRAND, "Premium Diesel").put(KEY_RECT_LEFT, 1).put(KEY_RECT_TOP, 455).put(KEY_RECT_RIGHT, 225).put(KEY_RECT_BOTTOM, 500).put(FLAG_IS_SELECTED,false));


			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 55).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 100).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 105).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 150).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 155).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 200).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 205).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 250).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 255).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 300).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 305).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 350).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 355).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 400).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 405).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 450).put(FLAG_IS_SELECTED,false));
			allfuelPrices.add(new JSONObject().put(KEY_FUEL_PRICE, 100.00).put(KEY_RECT_LEFT, 235).put(KEY_RECT_TOP, 455).put(KEY_RECT_RIGHT, 370).put(KEY_RECT_BOTTOM, 500).put(FLAG_IS_SELECTED,false));



		} catch (JSONException e) {
			e.printStackTrace();
		}

		return allfuelPrices;

	}





	static public ArrayList<String> getAllFuelTypes() {

		ArrayList<String> allFuelType = new ArrayList<String>();
		allFuelType.add("E85");
		allFuelType.add("ULP");
		//   allFuelType.add("E10");
		allFuelType.add("PULP");
		allFuelType.add("UPULP");
		allFuelType.add("LPG");
		allFuelType.add("tDiesel");
		allFuelType.add("Biodiesel");
		allFuelType.add("Premium Diesel");
		allFuelType.add("UNLEADED E10");
        //allFuelType.add("Autogas");//added by Han. Might not be necessary.

		return allFuelType;

	}








}
