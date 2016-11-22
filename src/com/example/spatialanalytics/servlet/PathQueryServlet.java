package com.example.spatialanalytics.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.PathQuery;

/**
 * Servlet implementation class PathQueryServlet
 */
//@WebServlet("/PathQueryServlet")
public class PathQueryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	private static final Logger logger = LogManager.getLogger(PathQueryServlet.class.getSimpleName());
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PathQueryServlet() {
        super();
    }

	/**
	 * Yu Sun 12/02/2015
	 * This method processes the Path Query, which given an origin location (lat/lng), a destination location
	 * (lat/lng) and a distance (double) returns the path from origin to destination (currently from
	 * Google Directions API) and the points (fuel stations) that have a distance to the path less than or equal
	 * to the range distance. <p>
	 * The distance from a specific point to the path is the minimum distance between the point and any point in
	 * the path, which hereafter is called 'path distance'. <p>
	 * 
	 * The required parameters are:
	 * 1. origin -- in the format of latitude,longitude
	 * 2. destination -- in the format of latitude,longitude
	 * 3. path_dist -- the maximum path distance in the format of a double number <p>
	 * 
	 * A common request example is:
	 * http://128.250.26.229:8080/FuelPriceSharingServer/PathQueryServlet?origin=-37.7963,144.9614&destination=-37.864,144.982&path_dist=1.0 <p>
	 * 
	 * The response is: <p>
	 * 	i) A Json object with 1) the name of "fuel_station" and value of a Json array contains a 
	 * 	   list of the required stations, and 2) the name of "direction" and value of the response
	 *     of google directions API call. <p>
	 *  ii) If there are errors in any field, the corresponding value would be "error". <p>
	 *  iii) If there are no query results, the corresponding value would be "empty".
	 *  One example result is {"fuel_station":"empty","direction":"error"}
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String origin = request.getParameter(ConstantConfig.PARAM_ORIGIN);
		String destination = request.getParameter(ConstantConfig.PARAM_DESTINATION);
		String path_dist = request.getParameter(ConstantConfig.PARAM_PATH_DISTANCE);
		
		if( origin == null || destination == null || path_dist == null || 
			origin.isEmpty() || destination.isEmpty() || path_dist.isEmpty() ){
			
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print( "Wrong parameters, please check the API doc" );
			response.getWriter().flush();
			return;
		}
		
		String[] tmp_o = origin.split(",");
		String[] tmp_d = destination.split(",");
		if( tmp_o.length != 2 || tmp_d.length != 2 ||
			isNotNumeric(tmp_o[0]) || isNotNumeric(tmp_o[1]) ||
			isNotNumeric(tmp_d[0]) || isNotNumeric(tmp_o[1]) || isNotNumeric(path_dist) ){
			
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print( "Wrong parameters, please check the API doc" );
			response.getWriter().flush();
			return;			
		}
		
		double lat_o = Double.valueOf(tmp_o[0]);
		double lng_o = Double.valueOf(tmp_o[1]);
		double lat_d = Double.valueOf(tmp_d[0]);
		double lng_d = Double.valueOf(tmp_d[1]);
		double p_dist = Double.valueOf(path_dist);
		
		PathQuery pq = new PathQuery(lat_o, lng_o, lat_d, lng_d, p_dist);
		
		logger.trace("Getting the query result...");
		response.getWriter().print( pq.getResult() );
		response.getWriter().flush();
		logger.trace("done!");
	}

	/**
	 * Do the same as doGet.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Check whether the given string represents a double number
	 * @param str -- string to check
	 * @return true if the string does NOT represent a double number, otherwise false
	 */
	private static boolean isNotNumeric(String str){
		
	  return !str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
