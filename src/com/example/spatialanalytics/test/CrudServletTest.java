package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.servlet.CrudServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Yu Sun on 28/01/2015 to test CrudServlet by using Mockito mocking framework.
 * Technically speaking, such test is not unit test, since the test class is not fully isolated
 * and the executing sequence of the test methods affects the test result.
 * Anyway, for simplicity and convenience, we use such kind of test to make sure the correctness
 * and robust of the code. 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrudServletTest extends Mockito{
	
	private final String table_name = "my_test";
	private final String row_id = "server_test_6";

	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	StringWriter sw;
	PrintWriter pw;
	CrudServlet tCrudServlet;
	
	/**
	 * 29/01/2015 Yu Sun: to set up the test by instantiating the mocked request
	 * and response objects and defining common operations as the two 'when' sentences.
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException{
	
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);

		when(mockRequest.getParameter(ConstantConfig.PARAM_TABLE_NAME)).thenReturn(table_name);
		when(mockRequest.getParameter(ConstantConfig.PARAM_ROW_ID)).thenReturn(row_id);
		
		tCrudServlet = new CrudServlet();
	}
	
	/**
	 * Yu Sun 29/01/2015: Should pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testA_DoPut() throws ServletException, IOException, JSONException{
		
		when(mockRequest.getMethod()).thenReturn("PUT");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", "test_servlet");
		jsonObj.put("times", 100);
		
		String jsonStr = jsonObj.toString();
		
		when(mockRequest.getParameter(ConstantConfig.PARAM_JSON_STRING)).thenReturn(jsonStr);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		tCrudServlet.service(mockRequest, mockResponse);
		String result = sw.getBuffer().toString().trim();
		
		JSONObject resJson = new JSONObject( result );
		assertEquals(true, resJson.getBoolean("ok"));
		
		//System.out.println(result);
	}
	
	/**
	 * Yu Sun 29/01/2015: This test must be after the testDoPut test. 
	 * Should also pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testB_DoGet() throws ServletException, IOException, JSONException{
		
		when(mockRequest.getMethod()).thenReturn("GET");
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		tCrudServlet.service(mockRequest, mockResponse);
		String result = sw.getBuffer().toString().trim();
		
		JSONObject resJson = new JSONObject( result );
		assertEquals("test_servlet", resJson.getString("name"));
		assertEquals(100, resJson.getInt("times"));
		assertEquals(row_id, resJson.getString("_id"));
		//System.out.println(result);
	}

	/**
	 * Yu Sun 29/01/2015: This test must be after the testDoPut test. 
	 * Should also pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testC_DoDelete() throws ServletException, IOException, JSONException{
		
		when(mockRequest.getMethod()).thenReturn("DELETE");
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		tCrudServlet.service(mockRequest, mockResponse);
		String result = sw.getBuffer().toString().trim();
		
		JSONObject resJson = new JSONObject( result );
		assertEquals(true, resJson.getBoolean("ok"));
		assertEquals(row_id, resJson.getString("id"));
		//System.out.println(result);
	}

}
