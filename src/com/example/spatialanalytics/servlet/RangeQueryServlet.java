package com.example.spatialanalytics.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.spatialanalytics.function.*;

/**
 * Yu Sun 30/01/2015: To provide the range query service.
 * Servlet implementation class RangeQueryServlet
 * 
 * Update history:
 * Yu Sun 01/04/2015: Check the remaining credit of the user, only if the
 * remaining credit is larger than or equal to the query credit deduction,
 * we return the query result and deduct query credit from the user's total
 * credit.
 * 
 */
//@WebServlet("/RangeQueryServlet")
public class RangeQueryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(RangeQueryServlet.class.getSimpleName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RangeQueryServlet() {
        super();
    }

	/**
	 * Yu Sun 30/01/2015 <p>
	 * The request parameter is 
	 * 		a quadruple of <latitude, longitude, range distance, user id>, 
	 * 		e.g., http://host:8080/RangeQueryServlet?lat=-37.842716&lng=144.883618&r_dist=3&user_id=222
	 * 
	 * Yu Sun 06/02/2015
	 * The returned result is a string representing a list of points (fuel stations) in the format
	 * of {"%table_name (fuel_station)":["list of points (fuel stations)"]}
	 * 
	 * Yu Sun 01/04/2015
	 * Add another query parameter of user id to check the validness of the query
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//non-optional parameter
		String range_dist = request.getParameter(ConstantConfig.PARAM_RANGE_DISTANCE);
		String user_id = request.getParameter(ConstantConfig.PARAM_CREDIT_USER_ID);
		String lat = request.getParameter(ConstantConfig.PARAM_LATITUDE);
		String lng = request.getParameter(ConstantConfig.PARAM_LONGITUDE);
		
		RangeQuery rq = null;
		if( (range_dist != null && !range_dist.isEmpty()) 
				&& (lat != null && !lat.isEmpty()) 
				&& (lng != null && !lng.isEmpty())
		){
			//query with the GPS coordinates
			rq = new RangeQuery(
					user_id,
					Double.valueOf(lat),			//latitude
					Double.valueOf(lng),			//longitude
					Double.valueOf(range_dist)			//range distance
					);
		}
		else{
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print( "Wrong parameters, please check the API doc" );
			response.getWriter().flush();
			return;
		}
		
		logger.trace("Getting the query result...");
		response.getWriter().print( rq.getResult() );
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

}