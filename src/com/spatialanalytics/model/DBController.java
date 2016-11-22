package com.spatialanalytics.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spatialanalytics.config.URLConfig;
import com.spatialanalytics.servlet.RegisterServlet;


public class DBController {

	private static final String dbUrl = URLConfig.getCouchDBAPI();
	//	private static final String dbUrl = "http://spatialanalytics.cis.unimelb.edu.au:5984/dest_pred_test/";

	static String dataStr;
	String doc;

	/**
	 * for logger & error
	 */
	private final static String TAG="DBController";
	private final static MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);



	private static  Logger logger= LogManager.getLogger(DBController.class.getSimpleName());


	/**
	 * Check the revision id of a document by fetching the latest rev infomation of a given document; it can be used in updating/deletion
	 * @param doc
	 * @param data
	 * @return updating data with the latest revision id in the filed of JSON
	 * @throws Exception
	 */
	public static String checkRevision(String doc, String data) throws Exception{

		try {
			logger.debug("check document revision");

			JSONObject repJson=new JSONObject(performGet(doc));
			if (repJson.has("error"))
			{
				//if (repJson.getString("error").startsWith("java.io.FileNotFoundException")) 
				return data;
			}
			else
			{
				logger.debug("Document exists! Update the revision and over write it...: "+doc);

				JSONObject dataJson;
				dataJson = new JSONObject(data);
				dataJson.put("_rev", repJson.get("_rev"));
				data=dataJson.toString();


			}

			return data;



		} catch (JSONException e) {
			// TODO Auto-generated catch block

			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());

		}
	}

	/**
	 * Force to PUT regardless the doc exist or not; Better used in updating
	 * @param doc
	 * @param data
	 * @return
	 */
	public static String performPUT(String doc, String data)  
	{ 	
		try {
			logger.debug("performing PUT");

			dataStr=data;
			data=checkRevision( doc,  data) ;
			HttpURLConnection connection;
			String returnResponse = "";
			URL url = null;
			url= new URL(dbUrl + doc);
			logger.debug(url.toString());

			connection = (HttpURLConnection)url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(data);
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				returnResponse = returnResponse+inputLine;
			in.close();      

			logger.debug("server run success (doesn't mean PUT success)");
			return returnResponse;

		}catch(FileNotFoundException e)
		{
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		}

	}
	/**
	 * Put without checking the revision id. Usually it is used in creating a new document; if the doc already
	 * exixts, error will occur. 
	 * @param doc
	 * @param data
	 * @return
	 */


	public static String performDirectPUT(String doc, String data)  
	{ 	try {

		logger.debug("performing direct PUT with no revision ID");

		HttpURLConnection connection;

		String returnResponse = "";
		URL url = null;
		url= new URL(dbUrl + doc);
		logger.debug(url.toString());

		connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Content-Type", "application/json");

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(data);
		out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null) 
			returnResponse = returnResponse+inputLine;
		in.close();      

		logger.debug("server run success (doesn't mean PUT success)");
		return returnResponse;

	}catch(FileNotFoundException e)
	{
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	}

	}
	
	
	
	
	/**
	 * Put without checking the revision id. Usually it is used in creating a new document; if the doc already
	 * exixts, error will occur. 
	 * @param doc
	 * @param data
	 * @return
	 */

	public static String performDirectPUT(String dbUrl,String doc, String data)  
	{ 	try {

		logger.debug("performing direct PUT with no revision ID");

		HttpURLConnection connection;

		String returnResponse = "";
		URL url = null;
		url= new URL(dbUrl + doc);
		logger.debug(url.toString());

		connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Content-Type", "application/json");

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(data);
		out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null) 
			returnResponse = returnResponse+inputLine;
		in.close();      

		logger.debug("server run success (doesn't mean PUT success)");
		return returnResponse;

	}catch(FileNotFoundException e)
	{
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		return myExceptinHandler.getJsonError(e.toString());
	}

	}



	/**
	 * Perform general queries of a document given by its ID
	 * @param doc
	 * @param data
	 * @return the document information formed in JSON
	 * @throws Exception
	 */

	public static String performGet(String doc) 
	{ 
		try {
			logger.debug("-----performting GET--------");
			HttpURLConnection connection;

			String returnResponse = "";
			URL url;

			url = new URL(dbUrl + doc);

			logger.debug(url.toString());


			connection = (HttpURLConnection)url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(3000);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				returnResponse = returnResponse+inputLine;
			in.close(); 

			logger.debug("server run success (doesn't mean GET success)");

			return returnResponse;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
			//						String errorMsg="{\"error\":"+"\""+e.toString()+"\""+"}";return errorMsg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//						String errorMsg="{\"error\":"+"\""+e.toString()+"\""+"}";return errorMsg;

			//e.printStackTrace();
			return myExceptinHandler.getJsonError("Reading file "+doc+" : "+e.toString());


		}



	}
	/**
	 * Delete the doc but rev is requied; it may be revised later to handle the case that rev is not provided by the Android client side.
	 * @param doc
	 * @param rev
	 * @return
	 */

	public static String performDelete(String doc, String rev)  
	{
		try {
			logger.debug("performing DELETE----");

			String returnResponse = "";
			HttpURLConnection connection;
			String url=dbUrl+doc+"?rev="+rev+"&_deleted:true";
			logger.debug(url);

			connection = (HttpURLConnection)new URL(url).openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			connection.setRequestProperty("Content-Type", "application/json");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				returnResponse = returnResponse+inputLine;
			in.close();      

			logger.debug("server run success (doesn't mean DELETE success)");

			return returnResponse;
		} catch(FileNotFoundException e)
		{

			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return myExceptinHandler.getJsonError(e.toString());
		}


	}







}
