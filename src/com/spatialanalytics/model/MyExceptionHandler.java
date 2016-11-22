package com.spatialanalytics.model;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.spatialanalytics.config.ConstantConfig;


/**
 * Created by hanl4 on 19/02/2015.
 * Self defined exception handler
 */
public class MyExceptionHandler extends Exception {
	

	private String TAG;// generally means the host class/servlet
	private JSONObject msg=new JSONObject();//message
	private Logger logger;

	public MyExceptionHandler(String tag) {

		// TODO Auto-generated constructor stub
		this.TAG=tag;
		this.logger=LogManager.getLogger(TAG);
	}

	
	/**
	 * check the field of request parameter is empty or not
	 * @param request
	 * @param field
	 * @return
	 */
	public boolean isParameterEmpty(HttpServletRequest request,String field)
	{
		return (request.getParameter(field)==null || request.getParameter(field).equals(""));

	}
	
	/**
	 * check the field of request parameter is empty or not
	 * @param field
	 * @return
	 */
	public boolean isParameterEmpty(String field)
	{
		return (field==null || field.equals(""));

	}
	
	/**
	 * get the errror message if the required field of parameter if empty
	 * @param field
	 * @return
	 */

	public String getJsonRequestParseError(String field)
	{
		try {
			msg.put(ConstantConfig.KEY_ERROR, field+" can not be empty");
			msg.put("tag", TAG);
			logger.error(msg.toString());
			return msg.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":"+"\""+e.toString()+"\""+"}";
		}
		
	
	}





	/**
	 * get error message
	 * @param e
	 * @return
	 */

	public String getJsonError(String error)
	{
		
		try {
			msg.put(ConstantConfig.KEY_ERROR,error);
			msg.put("tag", TAG);
			logger.error(msg.toString());
			return msg.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":"+"\""+e.toString()+"\""+"}";
		}
		

	}

	/**
	 * get error message without a tag
	 * @param e
	 * @return
	 */
	public String getJsonErrorNoTag(String error)
	{
		
		
		try {
			msg.put(ConstantConfig.KEY_ERROR, error);
			msg.put("tag", TAG);
			logger.error(msg.toString());
			return msg.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":"+"\""+e.toString()+"\""+"}";
		}
		
		

	}

	/**
	 * generate ok message
	 * @return
	 */
	public String getJsonOK()
	{
		
		return "{\"ok\":true,\"tag\":\""+TAG+"\"}";
	}

	/**
	 * generate ok message without a tag
	 * @return
	 */
	public String getJsonOkNoTag()
	{


		return "{\"ok\":"+"\"true\""+"}";
	}
	
	
	/**
	 * customise the tags or status (timely report the status of current process)
	 * @return
	 */
	public String setTag(String tag, String msg)
	{
		JSONObject json=new JSONObject();
		try {
			json.put(ConstantConfig.KEY_SUCCESS, true);
			json.put(tag, msg);
				
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.toString());

		}
		return json.toString();
		
	}

	public String getTAG() {
		return TAG;
	}

	public void setTAG(String TAG) {
		this.TAG = TAG;
	}

}