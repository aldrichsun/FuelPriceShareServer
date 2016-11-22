package com.example.spatialanalytics.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.example.spatialanalytics.function.*;

/**
 * To test the RWLocalFile class
 * @author yus1
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RWLocalFileTest {
	
	private String inPath = "./data/test.in";
	private String outPath = "./data/test.out";
	private RWLocalFile trw = null;
	private ArrayList<String> res = null;
	
	@Before
	public void setUp(){
		trw = new RWLocalFile();
	}
	
	/**
	 * TEST read: readToStringArray
	 */
	@Test
	public void testA_read(){
		
		res = trw.readToStringArray(inPath);
		
		assertTrue(res != null);
		assertFalse(res.isEmpty());
	}
	
	/**
	 * TEST write: writeToLocalFile
	 */
	@Test
	public void testB_write(){
		
		res = new ArrayList<String>();
		
		res.add("dummy line");
		res.add("test write file");
		trw.writeToLocalFile(outPath, res, false);
		
		ArrayList<String> rback = trw.readToStringArray(outPath);
		
		assertTrue(rback != null);
		assertFalse(rback.isEmpty());
		assertEquals("test write file", rback.get((rback.size()-1)));
	}

}
