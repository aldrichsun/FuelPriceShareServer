package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.servlet.RangeQueryServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Yu Sun on 30/01/2015 to test RangeQueryServlet by using Mockito mocking framework.
 * Technically speaking, such test is not unit test, since the test class is not fully isolated
 * and the executing sequence of the test methods affects the test result.
 * Anyway, for simplicity and convenience, we use such kind of test to make sure the correctness
 * and robust of the code. 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RangeQueryServletTest extends Mockito{

	private double lat = -37.7963;
	private double lng = 144.9614;
	private String addr = "University of Melbourne";
	private double r_dist = 2; // km
	
	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	StringWriter sw;
	PrintWriter pw;
	RangeQueryServlet rqServlet;
	
	/**
	 * 30/01/2015 Yu Sun: to set up the test by instantiating the mocked request
	 * and response objects and defining common operations as the 'when' sentence.
	 * @throws IOException
	 */
	@Before
	public void setUp(){
		
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);
		
		when(mockRequest.getMethod()).thenReturn("GET");
		
		rqServlet = new RangeQueryServlet();
	}
	
	/**
	 * Yu Sun 30/01/2015: Should pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void test_queryWithCoord() throws IOException, ServletException{
		
		when(mockRequest.getParameter(ConstantConfig.PARAM_LATITUDE)).thenReturn(String.valueOf(lat));
		when(mockRequest.getParameter(ConstantConfig.PARAM_LONGITUDE)).thenReturn(String.valueOf(lng));
		when(mockRequest.getParameter(ConstantConfig.PARAM_RANGE_DISTANCE)).thenReturn(String.valueOf(r_dist));
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		rqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);
	}
	
	/**
	 * Yu Sun 30/01/2015 Such operations are not supported yet.
	 * <p>
	 * Yu Sun 30/01/2015: Should pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void test_queryWithAddress() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_ADDRESS)).thenReturn(addr);
		when(mockRequest.getParameter(ConstantConfig.PARAM_RANGE_DISTANCE)).thenReturn(String.valueOf(r_dist));
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		try{
		rqServlet.service(mockRequest, mockResponse);
		//String res = sw.getBuffer().toString().trim();
		
		//assertTrue( res != null );
		//assertFalse( res.isEmpty() );
		//System.out.println(res);
		} catch (UnsupportedOperationException e){
			System.err.println(e.toString());
		}
	}
	
	/**
	 * Yu Sun 30/01/2015
	 * Test bad request by mocking incomplete query parameters 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void test_badRequest() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_LONGITUDE)).thenReturn(String.valueOf(lng));
		when(mockRequest.getParameter(ConstantConfig.PARAM_RANGE_DISTANCE)).thenReturn(String.valueOf(r_dist));
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		rqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);		
	}
}
