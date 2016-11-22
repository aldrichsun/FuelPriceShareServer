package com.spatialanalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;

/**
 * Servlet implementation class CouchDBHandlerServlet
 */
public class CouchDBHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String KEY_COUCHDB_DOC_ID="_id"; 
	private final String KEY_COUCHDB_DOC_DATA="data";
	private final String KEY_COUCHDB_DOC_ACTION="action";
	private final String KEY_COUCHDB_DOC_REV="rev";



	/**
	 * for error and logger
	 */
	private final  String TAG="DBController";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);
	//private Logger logger = LogManager.getLogger(MyExceptionHandler.class.getSimpleName());

	private Logger logger = LogManager.getLogger(CouchDBHandlerServlet.class.getSimpleName());

	private final boolean isPrint=true;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CouchDBHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub



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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String action=request.getParameter(KEY_COUCHDB_DOC_ACTION);
		String reply=myExceptinHandler.getJsonOkNoTag();

		logger.debug("---CouchDBHandlerServlet----");


		if(action==null)
		{
			out.println(myExceptinHandler.getJsonError("action is null"));

			return;
		}

		if(request.getParameter(KEY_COUCHDB_DOC_ID)==null)
		{

			out.println(myExceptinHandler.getJsonError("_id is null"));

			return;

		}

		//logger.debug(TAG,action);

		if(action.equals("PUT"))
		{

			if(request.getParameter(KEY_COUCHDB_DOC_DATA)==null)
			{
				out.println(myExceptinHandler.getJsonError("data is null"));

				return;
			}
			out.println(DBController.performPUT(request.getParameter(KEY_COUCHDB_DOC_ID).toString(), request.getParameter(KEY_COUCHDB_DOC_DATA).toString()));

		}
		else if(action.equals( "GET"))
		{
			out.println(DBController.performGet(request.getParameter(KEY_COUCHDB_DOC_ID).toString()));
		}
		else if(action.equals("DELETE"))
		{
			if(request.getParameter(KEY_COUCHDB_DOC_REV)==null)
			{
				out.println(myExceptinHandler.getJsonError("rev is null"));

				return;
			}
			out.println(DBController.performDelete(request.getParameter(KEY_COUCHDB_DOC_ID).toString(), request.getParameter(KEY_COUCHDB_DOC_REV).toString()));

		}
		else{
			out.println(myExceptinHandler.getJsonError("no such action is null"));

			return;

		}

	}





}
