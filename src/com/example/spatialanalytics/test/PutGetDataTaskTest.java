package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.PutGetDataTask;

/**
 * @author Yu Sun 28/01/2015
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PutGetDataTaskTest {
	
	private final String table_name = "my_test";
	private final String row_id = "server_test_6";
	
	/**
	 * Test method for {@link com.example.spatialanalytics.function.PutGetDataTask#getResult()}.
	 * @throws MalformedURLException 
	 * @throws JSONException 
	 */
	@Test
	public void testA_Put() throws MalformedURLException, JSONException {
		
		URL url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + row_id);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", "test");
		jsonObj.put("times", 1);
		
		PutGetDataTask task = new PutGetDataTask(
				PutGetDataTask.REQUEST_PUT,
				url,
				jsonObj.toString()
				);
		String result = task.getResult();
		JSONObject resJson = new JSONObject( result );
		
		assertEquals(true, resJson.getBoolean("ok"));
	}
	
	@Test
	public void testB_Get() throws MalformedURLException, JSONException {
		
		URL url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + row_id);
		PutGetDataTask task = new PutGetDataTask(
				PutGetDataTask.REQUEST_GET,
				url
				);
		String result = task.getResult();
		JSONObject resJson = new JSONObject( result );
		
		assertEquals("test", resJson.getString("name"));
		assertEquals(1, resJson.getInt("times"));
		assertEquals(row_id, resJson.getString("_id"));
	}
	
	@Test
	public void testC_Put_again() throws MalformedURLException, JSONException {
		
		URL url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + row_id);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", "test");
		jsonObj.put("times", 1);
		
		PutGetDataTask task = new PutGetDataTask(
				PutGetDataTask.REQUEST_PUT,
				url,
				jsonObj.toString()
				);
		String result = task.getResult();
		JSONObject resJson = new JSONObject( result );
		
		assertEquals("conflict", resJson.getString("error"));
	}
	
	@Test
	public void testD_Delete() throws MalformedURLException, JSONException {
	
		URL url_get = new URL(ConstantConfig.HOST + "/" + table_name + "/" + row_id);
		
		PutGetDataTask getData = new PutGetDataTask(
			PutGetDataTask.REQUEST_GET,
			url_get
		);
		String jsonStr = getData.getResult();
		JSONObject jsonObject = new JSONObject(jsonStr);
		 
		// If find the row entry, we get its _rev id and then delete the row.
		String rev = jsonObject.getString("_rev");
		URL url_delete = new URL(url_get.toString() + "?rev=" + rev);
			
		PutGetDataTask task = new PutGetDataTask(
				PutGetDataTask.REQUEST_DELETE,
				url_delete
				);
		String result = task.getResult();
		JSONObject resJson = new JSONObject( result );
		
		assertEquals(true, resJson.getBoolean("ok"));
		assertEquals(row_id, resJson.getString("id"));
	}
}
