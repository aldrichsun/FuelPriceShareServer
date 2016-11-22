package com.spatialanalytics.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.Constant;
import com.common.CreditManager;
import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;
import com.spatialanalytics.model.MyTime;

/**
 * Servlet implementation class RegisterServlet
 * for registering in our own system
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * for error control
	 */
	private final  String TAG="RegisterServlet";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);
	private Logger logger = LogManager.getLogger(RegisterServlet.class.getSimpleName());

	 


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter out = response.getWriter();
		String reply=myExceptinHandler.getJsonOK();
		logger.debug("----RegisterServlet----");
		try {

			String act = request.getParameter("Action").toString();
			

			if (act.equals("Register"))
			{
				/**
				 * Get the encoded information from request
				 */
				
			
				String phone = request.getParameter("phone");
				if(myExceptinHandler.isParameterEmpty(phone))
				{
					out.println(myExceptinHandler.getJsonRequestParseError(phone));
				
					return;
				}
				phone=phone.toString();
				
				
				String json = request.getParameter("json");
				if(myExceptinHandler.isParameterEmpty(json))
				{
					out.println(myExceptinHandler.getJsonRequestParseError(phone));
				
					return;
				}
				json=json.toString();
				
				
				
				JSONObject newUser=new JSONObject(json);
				//the time that user register
				newUser.put("created_at", MyTime.getToday());
				
				//////////////////// Added by Yu Sun 02/04/2015 //////////////////////
				/* give the newly registered user initial credit */
				newUser.put(Constant.COLUMN_USER_CREDIT, CreditManager.getInitialCredit());
				//////////////////////////////////////////////////////////////////////
				
				/**
				 * direct put user information into the system. if the document or the
				 * doc id has been taken, an error will return to reply.
				 */
				logger.debug("Receive all required parameters successfully! Now do CouchDB PUT..");

				reply=DBController.performDirectPUT(phone, newUser.toString());
				

			}else{
				reply=myExceptinHandler.getJsonError("Unknow Action");
			}



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reply=myExceptinHandler.getJsonError(e.toString());
		}

		out.println(reply);
		logger.debug("server run success (doesn't mean register success)");



	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
