package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.servlet.PathQueryServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Yu Sun on 12/02/2015 to test RangeQueryServlet by using Mockito mocking framework.
 * Technically speaking, such test is not unit test, since the test class is not fully isolated
 * and the executing sequence of the test methods affects the test result.
 * Anyway, for simplicity and convenience, we use such kind of test to make sure the correctness
 * and robust of the code. 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PathQueryServletTest extends Mockito{
	
	//location: Uni Melbourne
	private double lat_s = -37.7963;
	private double lng_s = 144.9614;
	//location: St Kilda
	private double lat_e = -37.8640;
	private double lng_e = 144.9820;
	//maximum path distance
	private double r_dist = 1.0; //km
	
	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	StringWriter sw;
	PrintWriter pw;
	PathQueryServlet pqServlet;

	/**
	 * 12/02/2015 Yu Sun: to set up the test by instantiating the mocked request
	 * and response objects and defining common operations as the 'when' sentence.
	 * @throws IOException
	 */
	@Before
	public void setUp(){
		
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);
		
		when(mockRequest.getMethod()).thenReturn("GET");
		
		pqServlet = new PathQueryServlet();
	}
	
	/**
	 * Yu Sun 12/02/2015: Should pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testA_queryWithCoord() throws IOException, ServletException{
		
		when(mockRequest.getParameter(ConstantConfig.PARAM_ORIGIN)).thenReturn(lat_s+","+lng_s);
		when(mockRequest.getParameter(ConstantConfig.PARAM_DESTINATION)).thenReturn(lat_e+","+lng_e);
		when(mockRequest.getParameter(ConstantConfig.PARAM_PATH_DISTANCE)).thenReturn(String.valueOf(r_dist));
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		pqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);
	}
	
	/**
	 * Yu Sun 12/02/2015 Such operations are not supported yet.
	 * <p>
	 * Yu Sun 12/02/2015: Should pay attention to the order of the code.
	 * The StringWriter and PrintWriter must be instantiated in the \@TEST,
	 * but not in the \@Setup. The sentence
	 * 			when(mockResponse.getWriter()).thenReturn(pw);
	 * must be after the instantiation of StringWriter and PrintWriter.
	 * @throws IOException
	 * @throws ServletException
	 */
	//@Test -- 12/02/2015 Yu Sun: to be tested after the function is upgraded.
	public void testB_queryWithAddress() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_ADDRESS)).thenReturn("TO BE FILLED");
		when(mockRequest.getParameter(ConstantConfig.PARAM_PATH_DISTANCE)).thenReturn(String.valueOf(r_dist));
		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		try{
			pqServlet.service(mockRequest, mockResponse);
		//String res = sw.getBuffer().toString().trim();
		
		//assertTrue( res != null );
		//assertFalse( res.isEmpty() );
		//System.out.println(res);
		} catch (UnsupportedOperationException e){
			System.err.println(e.toString());
		}
	}
	
	/**
	 * Yu Sun 12/02/2015
	 * Test bad request by mocking incomplete query parameters
	 * This bad request is "lat lng", instead of "lat,lng"  
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testC_badRequest_no_comma() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_ORIGIN)).thenReturn(lat_s+" "+lng_s);
		when(mockRequest.getParameter(ConstantConfig.PARAM_DESTINATION)).thenReturn(lat_e+","+lng_e);
		when(mockRequest.getParameter(ConstantConfig.PARAM_PATH_DISTANCE)).thenReturn(String.valueOf(r_dist));		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		pqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);		
	}
	
	/**
	 * Yu Sun 12/02/2015
	 * Test bad request by mocking incomplete query parameters
	 * This bad request is that the lng is not numeric.  
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testD_badRequest_lng_not_numeric() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_ORIGIN)).thenReturn(lat_s+","+"hello");
		when(mockRequest.getParameter(ConstantConfig.PARAM_DESTINATION)).thenReturn(lat_e+","+lng_e);
		when(mockRequest.getParameter(ConstantConfig.PARAM_PATH_DISTANCE)).thenReturn(String.valueOf(r_dist));		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		pqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);		
	}
	
	/**
	 * Yu Sun 12/02/2015
	 * Test bad request by mocking incomplete query parameters
	 * This bad request is that the lng is not numeric.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testE_badRequest_lat_not_numeric() throws IOException, ServletException {

		when(mockRequest.getParameter(ConstantConfig.PARAM_ORIGIN)).thenReturn(lat_s+","+lng_s);
		when(mockRequest.getParameter(ConstantConfig.PARAM_DESTINATION)).thenReturn("24hi007"+","+lng_e);
		when(mockRequest.getParameter(ConstantConfig.PARAM_PATH_DISTANCE)).thenReturn(String.valueOf(r_dist));		
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		
		when(mockResponse.getWriter()).thenReturn(pw);
		
		pqServlet.service(mockRequest, mockResponse);
		String res = sw.getBuffer().toString().trim();
		
		assertTrue( res != null );
		assertFalse( res.isEmpty() );
		System.out.println(res);		
	}
}
