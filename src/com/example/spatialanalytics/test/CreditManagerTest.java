package com.example.spatialanalytics.test;

import static org.junit.Assert.*;
import org.junit.Test;

import com.common.CreditManager;

public class CreditManagerTest {
	
	private String user_id = "asun0715@gmail.com";
	private String invalid_id = "asun0715@gmail.com111";
	
	@Test
	public void test_credit(){
		
		boolean alwaysSufficient = CreditManager.hasSufficientCredit(user_id);
		assertTrue( alwaysSufficient );
		
		for(int i = 0; i < 10; i++){ //at most ten times deduction
			CreditManager.deductCredit(user_id);
		}
		
		assertFalse( CreditManager.hasSufficientCredit(user_id) );
	}
	
	@Test
	public void test_invalid_user(){
		
		boolean invalid = CreditManager.hasSufficientCredit(invalid_id);
		assertFalse( invalid );
	}

}
