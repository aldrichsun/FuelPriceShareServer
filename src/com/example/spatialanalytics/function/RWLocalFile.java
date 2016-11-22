package com.example.spatialanalytics.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Read and Write local (text) files on the server.
 * This is a very simple and basic class.
 * @author Yu Sun
 */
public class RWLocalFile {
	
	private static final Logger logger = LogManager.getLogger(RWLocalFile.class.getSimpleName());
	
	public RWLocalFile(){};
	
	/**
	 * Read the local text file into a SINGLE string.
	 * @param filePath -- the file to read in
	 * @return
	 * i) all the lines in the file in a single string;
	 * ii) null if any error occurs.
	 */
	public String readToSingleString(String filePath){
		
		StringBuffer res = new StringBuffer();
		
		File file = new File(filePath);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			logger.error("Error file " + filePath + " doesn't exist", e.toString());
			return null;
		}
		BufferedReader br = new BufferedReader(fr);
		String s = null;
		try {
			while((s = br.readLine())!=null){
				res.append(s + '\n');
			}
		} catch (IOException e) {
			logger.error("Error reading File " + filePath, e.toString());
			return null;
		}
		finally{
			if( br != null ){
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Error closing buffered reading stream: ", e.toString());					
				}
			}
			if( fr != null ){
				try {
					fr.close();
				} catch (IOException e) {
					logger.error("Error closing reading stream: ", e.toString());
				}
			}
		}
		
		return res.toString(); 
	}
	
	/**
	 * Read the local text file into a String array.
	 * Each line corresponds to one entry in the array.
	 * @param filePath -- the file to read in
	 * @return
	 * i) all the lines in the file in an array list;
	 * ii) the lines have read in before the error, if any error occurs
	 */
	public ArrayList<String> readToStringArray(String filePath){
		
		ArrayList<String> res = new ArrayList<String>();
		
		File file = new File(filePath);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			logger.error("Error file " + filePath + " doesn't exist", e.toString());
			return null;
		}
		BufferedReader br = new BufferedReader(fr);
		String s = null;
		try {
			while((s = br.readLine())!=null){
				res.add(s);
			}
		} catch (IOException e) {
			logger.error("Error reading File " + filePath, e.toString());
		}
		finally{
			if( br != null ){
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Error closing buffered reading stream: ", e.toString());					
				}
			}
			if( fr != null ){
				try {
					fr.close();
				} catch (IOException e) {
					logger.error("Error closing reading stream: ", e.toString());
				}
			}
		}
		
		//String[] result = (String[]) res.toArray();
		return res; 
	}
	
	/**
	 * Write the provided content to the assigned local path (if access permits)
	 * @param filePath -- local file path
	 * @param content -- content to write
	 * @param append -- true: append to the end of the file, false: override the existing file
	 */
	public void writeToLocalFile(String filePath, ArrayList<String> content, boolean append){
		
		File file = this.createFile(filePath);
		FileWriter fw = null;
		try {
			if( append )
				fw = new FileWriter(file, true);
			else
				fw = new FileWriter(file);
		} catch (IOException e) {
			logger.error("Error writing File " + filePath, e);
		}
		BufferedWriter bw = new BufferedWriter(fw);
		Iterator<String> c_it = content.iterator();
		int line_num = 0;
		try {
			while( c_it.hasNext() ){
				line_num++;
				bw.write( c_it.next() + "\n" );
			}
			bw.flush();
		} catch (IOException e) {
			logger.error("Error writing line " + line_num + " in file " + file.getPath(), e.toString());
		}finally{
			if( bw != null ){
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("Error closing buffered writing stream: " + e.toString());
				}
			}
			if( fw != null ){
				try {
					fw.close();
				} catch (IOException e) {
					logger.error("Error closing writing stream: " + e.toString());
				}
			}
		}
		return;
	}
	
	/**
	 * If file does not exist, create it;
	 * or if the file already exists, bind to it and return.
	 * @date 2011-12-20 12:19:43
	 * @author Yu Sun
	 * @param path
	 * @return
	 */
	private File createFile(String path) {  

		File file = new File(path);
		File parent = new File(file.getAbsolutePath().substring(0, 
				file.getAbsolutePath().lastIndexOf(File.separator)));
		if (!parent.exists()){
			this.createFile(parent.getPath());
			parent.mkdirs();
		}
		return file;
		
	}

}
