package com.example.spatialanalytics.servlet;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.PutGetDataTask;

/**
 * Servlet implementation class CrudServlet
 * 
 * Created by Yu Sun on 27/01/2015: Response to the 
 * requests to create, retrieve, update and delete data
 * in the underlying database.
 */
// Commented by Yu Sun on 29/01/2015: This description and 
// the servlet-mapping in web.xml, only one can be kept.
// Otherwise there will be multi-mapping error when start the server.
//@WebServlet(description = "Response to the requests to create, retrieve, update and delete data in the underlying database.", urlPatterns = { "/CrudServlet" })
public class CrudServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(CrudServlet.class.getSimpleName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CrudServlet() {
        super();
    }

	/**
	 * Yu Sun 27/01/2015: The requested URL contains two parameters
//	 * 1. table_name -- the name of the table to be accessed
	 * 2. row_id -- the key of the row to be accessed
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		logger.debug("The request URI is: " + request.toString());
		
		String table_name = request.getParameter(ConstantConfig.PARAM_TABLE_NAME);
		String row_id = request.getParameter(ConstantConfig.PARAM_ROW_ID);
		
		if( table_name == null || row_id == null ){
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		// 27/01/2015 Yu Sun: Since both parameters are given, we request the DB server
		// to read the intended data
		StringBuffer url = new StringBuffer(ConstantConfig.HOST);
		url.append("/" + table_name + "/" + row_id);
		URL myUrl = new URL(url.toString());
		PutGetDataTask putGetDataTask = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				myUrl
				);
		response.getWriter().print( putGetDataTask.getResult() );
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
	 * Yu Sun 27/01/2015: The requested URL contains three parameters
	 * 1. table_name -- the name of the table to be accessed
	 * 2. row_id -- the key of the row to be accessed
	 * 3. json_string -- the JSON object (string) to be inserted. Note that
	 * 					the string should contain the '_id' field
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("The request URI is: " + request.toString());
		
		String table_name = request.getParameter(ConstantConfig.PARAM_TABLE_NAME);
		String row_id = request.getParameter(ConstantConfig.PARAM_ROW_ID);
		String json_string = request.getParameter(ConstantConfig.PARAM_JSON_STRING); 
		
		if( table_name == null || row_id == null || json_string == null ){
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		// 27/01/2015 Yu Sun: Since both parameters are given, we request the DB server
		// to store the intended data
		StringBuffer url = new StringBuffer(ConstantConfig.HOST);
		url.append("/" + table_name + "/" + row_id);
		URL myUrl = new URL(url.toString());
		PutGetDataTask putGetDataTask = new PutGetDataTask(
				PutGetDataTask.REQUEST_PUT,
				myUrl,
				json_string
				);
		response.getWriter().print( putGetDataTask.getResult() );
		response.getWriter().flush();
	}
	
	/**
	 * Yu Sun 27/01/2015: The requested URL contains two parameters
	 * 1. table_name -- the name of the table to be accessed
	 * 2. row_id -- the key of the row to be accessed
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("The request URI is: " + request.toString());
		
		String table_name = request.getParameter(ConstantConfig.PARAM_TABLE_NAME);
		String row_id = request.getParameter(ConstantConfig.PARAM_ROW_ID);
		
		if( table_name == null || row_id == null ){
			logger.error("Bad request from: " + request.getQueryString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		// 27/01/2015 Yu Sun: Since both parameters are given, we request the DB server
		// to delete the intended data
		StringBuffer url = new StringBuffer(ConstantConfig.HOST);
		url.append("/" + table_name + "/" + row_id);
		
		// Yu Sun: we first try to get the row in the table
		URL myUrl_get = new URL(url.toString());
		PutGetDataTask getData = new PutGetDataTask(
			PutGetDataTask.REQUEST_GET,
			myUrl_get
		);
		String jsonStr = getData.getResult();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonStr);
			// If find the row entry, we get its _rev id and then delete the row.
			if( !jsonObject.optString("_rev").isEmpty() ){
				
				String rev = jsonObject.getString("_rev");
				url.append("?rev=" + rev);
				URL myUrl_delete = new URL(url.toString());
				
				PutGetDataTask putGetDataTask = new PutGetDataTask(
						PutGetDataTask.REQUEST_DELETE,
						myUrl_delete
						);
				response.getWriter().print( putGetDataTask.getResult() );
			}
			else{// Otherwise, we just return {"ok":true}
				response.getWriter().print("{\"ok\":true}");
			}
			response.getWriter().flush();
		} catch (JSONException e) {
			logger.error("ERROR JSON parsing: " + e.toString());
		}
		return;
	}
}