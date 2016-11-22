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

import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;

/**
 * Servlet implementation class LoginServlet
 * This servlet only in charge of log in 
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(LoginServlet.class.getSimpleName());

	/**
	 * for error control
	 */
	private final  String TAG="LoginServlet";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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

		/**
		 * Pass the password to the android client, and perform the validation at the client side.
		 * 
		 */
		PrintWriter out = response.getWriter();
		logger.debug("----Login----");
		
		String act = request.getParameter("Action");
		if(myExceptinHandler.isParameterEmpty(act))
		{
			out.println(myExceptinHandler.getJsonRequestParseError("Action"));
		
			return;
		}
		act=act.toString();	
		

		

		String reply=myExceptinHandler.getJsonOK();
		try {

			if (act.equals("Login"))
			{
				/**
				 * get the enclosed data information
				 */

				
				String username = request.getParameter("username");
				if(myExceptinHandler.isParameterEmpty(username))
				{
					out.println(myExceptinHandler.getJsonRequestParseError(username));
				
					return;
				}
				username=username.toString();
				
				//Get all the user information from user table
				reply=DBController.performGet(username);
				JSONObject json=new JSONObject(reply);
				if(!json.has("error"))
				{
					logger.debug("read user document success");
					/**
					 * remove the sensitive information from the records for safety but not 
					 * a big necessary
					 */
					json.remove("_id");
					reply=json.toString();
				}




			}else{
				reply=myExceptinHandler.getJsonError("Unknow Action");
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			reply=myExceptinHandler.getJsonError(e.toString());

		}
		out.println(reply);
		logger.debug("server success (doesn't mean login success)");






	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
