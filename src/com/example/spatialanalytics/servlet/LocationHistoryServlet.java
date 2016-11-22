package com.example.spatialanalytics.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.LocationHistory;

/**
 * TODO add comments
 * @author Yu Sun
 */
public class LocationHistoryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(LocationHistoryServlet.class.getSimpleName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LocationHistoryServlet() {
        super();
    }

	/**
	 * Yu Sun 03/03/2015:
	 * Given the user id, this function returns a json object in which the key is LOCATION_HISTORY_KEY and value is a json array containing
	 * the location history addresses (in the format of strings) whose maximum length is MAX_RETURN_NUMBER.
	 * It returns 
	 * 1) a json object in which the key is LOCATION_HISTORY_KEY and value is a json array containing
	 * the location history addresses (in the format of strings).
	 * 2) a json object in which the key is LOCATION_HISTORY_KEY and value is ERROR_VALUE when any error
	 * occurs. 
	 * The requested URL contains one parameter
	 * 1. u_id -- the user id
	 * A request example is: http://host/LocationHistoryServlet?user_id=%u_id 
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		logger.debug("The request URI is: " + request.toString());
		
		String u_id = request.getParameter(ConstantConfig.LOCATION_HISTORY_COLUMN_USER_ID);
		
		if( u_id == null ){
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		LocationHistory lh = new LocationHistory();
		
		response.getWriter().print( lh.getLocationHistory(u_id) );
		response.getWriter().flush();
	}

	/**
	 * Yu Sun 27/01/2015: Same as doPut
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		this.doPut(request, response);
	}

	/**
	 * Yu Sun 03/03/2015:
	 * Given the user id and the address to record, this function stores the address in the DB.
	 * Currently, if the store operation fails, we do nothing.
	 * @param u_id -- user id
	 * @param address -- user search address (each time we only record one address, as we need to
	 * maintain a proper order of these addresses, which is by default the function calling order. 
	 * 
	 * The requested URL contains three parameters
	 * 1. user_id -- the key of the row to be accessed
	 * 2. addr -- the address to be inserted
	 * 
	 * A request example is: http://host/LocationHistoryServlet?user_id=%u_id&addr=%addr_string
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("The request URI is: " + request.toString());
		
		String u_id = request.getParameter(ConstantConfig.LOCATION_HISTORY_COLUMN_USER_ID);
		String addr_string = request.getParameter(ConstantConfig.PARAM_ADDRESS); 
		
		if( u_id == null || addr_string == null ){
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		LocationHistory lh = new LocationHistory();
		lh.recordLocationHistory(u_id, addr_string);
		
		response.getWriter().print( "{\"ok\":true}" ); // Currently, we always return OK.
		response.getWriter().flush();
	}
}
