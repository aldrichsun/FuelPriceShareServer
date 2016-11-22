package com.spatialanalytics.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileCleaningTracker;
import org.json.JSONObject;

import com.spatialanalytics.config.ConstantConfig;
import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;
import com.spatialanalytics.model.NearbyStationAPI;
import com.spatialanalytics.model.ZHAOHEAPI;

/**
 * Servlet implementation class UploadServlet
 * 1) Receive the uploaded fuel price image, and process it to extract the fuel infomation
 * including price and type.
 * 2) Retrieve petro station the user currently located at
 * 
 * More comments added by Yu Sun on 02/04/2015
 * This servlet accepts the image taken by the user, hand the image to the text recognition
 * server (which is currently developed and maintained by He Zhao and Yuan Li), receive the
 * recognized text and send the text back to the client end for confirmation (and editing).
 * If we can get the user's current location, this servlet also retrieves the fuel stations
 * close to (within 10km but it should be 1KM!) the user's current location, and send these
 * stations (if any) back to the user to choose from (if there is only one such station, the
 * user doesn't need to choose).
 * After confirming or editing recognized text and choosing the right station, the final contributed
 * price is processed by the UploadReginedResultServlet.
 */
public class FuelPriceImageProcessServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * File upload set up & constraits
	 */
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 500000 * 1024;
	private int maxMemSize = 40 * 1024;
	private File file;
	private static String root;
	private String rootFolder=ConstantConfig.KEY_FUEL_IMAGE_FOLDER;//the folder store the uploaded fuel image. it has to be created in disk

	private List <String>fileNameList=new ArrayList<String>();
	private static  Logger logger= LogManager.getLogger(FuelPriceImageProcessServlet.class.getSimpleName());

	/**
	 * for error control
	 */
	private final  String TAG="FuelPriceImageProcessServlet";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FuelPriceImageProcessServlet() {
		super();
	}
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		root = config.getServletContext().getRealPath("/");
		filePath = root + rootFolder;
		createDir(new File(filePath));
		logger.debug("File Folder PATH : " +root);
	}
	
	/**
	 * create folder if the path doesn't exist
	 * @param file
	 */
	public void createDir(File file){
		if (!file.exists()) {
			if (file.mkdir()) {
				logger.debug("Directory is created!");
			} else {
				logger.debug("Failed to create directory!");
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//root = getServletContext().getRealPath("/");
		logger.debug("---FuelPriceImageProcessServlet---");

		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );
		if( !isMultipart ){
			out.println(myExceptinHandler.getJsonError("File not uploaded!"));
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(maxMemSize);
		factory.setRepository(new File(filePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax( maxFileSize );

		JSONObject jsonData=new JSONObject();//the data sent by user android client
		try
		{
			//Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);
			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while ( i.hasNext () ) 
			{
				FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () )	
				{
					//Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();

					if(fieldName.equals(ConstantConfig.KEY_FILE_UPLOAD_FILE_DATA))
						if(fileName.toLowerCase().endsWith("jpg") || fileName.toLowerCase().endsWith("png") ||fileName.toLowerCase().endsWith("jpeg"))
						{
							/**
							 * Get the file and store it locally 
							 */
							fileNameList.add(fileName);
							file = new File( filePath + fileName) ;	
							logger.debug(file.getAbsolutePath().toString());
							fi.write( file );
							logger.debug("write file success");
						}
				}else {
					/**
					 * Get string data usually encoded in JSON
					 */
					jsonData = new JSONObject(fi.getString());
					logger.debug(jsonData.toString());
				}
			}
			/**
			 * call API to get the current petro station. Please notice that,
			 * the result can be : null, only 1 petro station or 1+
			 */
			if (!jsonData.has(ConstantConfig.KEY_LATITUDE) 
					||!jsonData.has(ConstantConfig.KEY_LONGITUDE) 
					||!jsonData.has(ConstantConfig.KEY_CAN_GET_LOCATION) 
					||!jsonData.has(ConstantConfig.KEY_CONTRIBUTE_PRICE_TRANSACTION_ID) 
					|| !jsonData.has(ConstantConfig.KEY_UID))
			{
				out.println(myExceptinHandler.getJsonError("Latitue or Longitute or can_get_location or transactionID or userID be null!"));
				return;
			}
			
			double lat;//user current location : latitude
			double log;//user current location : longitude 
			boolean canGetLocation;
			
			logger.debug(myExceptinHandler.setTag(ConstantConfig.KEY_CONTRIBUTE_PRICE_PROCESS_STATUS, ConstantConfig.KEY_CONTRIBUTE_PRICE_STATUS_RETRIEVE_PETROL_STATION));

			lat=jsonData.getDouble(ConstantConfig.KEY_LATITUDE);
			log=jsonData.getDouble(ConstantConfig.KEY_LONGITUDE);
			canGetLocation = jsonData.getBoolean(ConstantConfig.KEY_CAN_GET_LOCATION);
			
			////////////////////// Get nearby stations ///////////////////////////////////////
			ArrayList<JSONObject> petroStations = NearbyStationAPI.getPetroStations(canGetLocation,lat, log);
			//////////////////////////////////////////////////////////////////////////////////
			
			/**
			 * call API to process the uploaded fuel image and get the information
			 * in JSON
			 */
			logger.debug(myExceptinHandler.setTag(ConstantConfig.KEY_CONTRIBUTE_PRICE_PROCESS_STATUS, ConstantConfig.KEY_CONTRIBUTE_PRICE_STATUS_PROCESS_FUEL_IMAGE));

			ArrayList<JSONObject> fuel = ZHAOHEAPI.getFuelObject();
			//removed by Han on 09/04/2015.
			//get all the fuel information from local to reduce data transition.
			//ArrayList<String> allFuelType = ZHAOHEAPI.getAllFuelTypes();
			/**
			 * obtain other basic information from the client
			 * Might generate the unique transaction id here and
			 * pass it back to the user
			 */
			String transactionID;//the contributing price transaction id
			String userID; //user unique id to identify user
			transactionID=jsonData.getString(ConstantConfig.KEY_CONTRIBUTE_PRICE_TRANSACTION_ID);
			userID=jsonData.getString(ConstantConfig.KEY_UID);
			/**
			 * encode all the information into one JSON to return it back to Android client
			 */
			JSONObject jsonReply = new JSONObject();		
			jsonReply.put(ConstantConfig.KEY_PETROL_STATION,petroStations);		
			jsonReply.put(ConstantConfig.KEY_SUCCESS, true);			
			jsonReply.put(ConstantConfig.KEY_FUEL, fuel);
			//jsonReply.put(ConstantConfig.KEY_ALL_FUEL_BRAND, allFuelType);
			
			out.print(jsonReply.toString());
			logger.debug("server run success (doesn't mean process fuel image success)");
		}
		catch(Exception ex) 
		{
			ex.printStackTrace();
			out.println(myExceptinHandler.getJsonError(ex.toString()));
			return;
		}
	}
}


