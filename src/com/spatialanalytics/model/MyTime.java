package com.spatialanalytics.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyTime {


	private static long currenttimeMillis; 
	private static String today;



	public static long getCurrenttimeMillis(){
		currenttimeMillis=System.currentTimeMillis();
		return currenttimeMillis;
	}

	public static String getToday(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		today = sdf.format(Calendar.getInstance().getTime());

		return today;
	}
	
}
