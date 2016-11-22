package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.servlet.LocationHistoryServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocationHistoryServletTest extends Mockito{

	private String u_id = "test_u_id";
	private String addr_0 = "New St Kilda, VIC";
	
	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	StringWriter sw;
	PrintWriter pw;
	LocationHistoryServlet lhServlet;
	
	/**
	 * Test set up
	 */
	@Before
	public void setUp(){
		
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);
		
		lhServlet = new LocationHistoryServlet();
	}
	
	/**
	 * Test get, even there's no such record, the response shall not be empty
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testA_Get() throws IOException, ServletException{
		
		when(mockRequest.getParameter(ConstantConfig.LOCATION_HISTORY_COLUMN_USER_ID)).thenReturn(u_id);
		when(mockRequest.getMethod()).thenReturn("GET");
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
				
		when(mockResponse.getWriter()).thenReturn(pw);
		
		lhServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);
	}
	
	/**
	 * Test put by storing addr_0
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testB_Put() throws IOException, ServletException{
		
		when(mockRequest.getParameter(ConstantConfig.LOCATION_HISTORY_COLUMN_USER_ID)).thenReturn(u_id);
		when(mockRequest.getParameter(ConstantConfig.PARAM_ADDRESS)).thenReturn(addr_0);
		when(mockRequest.getMethod()).thenReturn("PUT");
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
				
		when(mockResponse.getWriter()).thenReturn(pw);
		
		lhServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);
	}
}
