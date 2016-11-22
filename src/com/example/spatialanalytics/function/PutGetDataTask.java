package com.example.spatialanalytics.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 27/01/2015 Yu Sun: The private class that handles the CouchDB request
/**
 * Created by Yu Sun on 24/01/2015.
 * Use exclusively for CouchDB. <p>
 * Given a cURL request (PUT, GET or DELETE such as curl -X GET http://127.0.0.1:5984/_all_dbs),
 * this class connects the CouchDB server and returns the JSON string returned by CouchDB.
 * If the request fails (due to DB server errors), it returns null.
 * <p>
 * When using CouchDB, <p>
 * ---------------------------------------------------------------<p>
 * the GET request URL is like: <p>
 * 		http://host_name/table_name/row_id <p>
 * the PUT request URL is like: <p>
 * 		http://host_name/table_name/row_id -d JSON_string <p>
 * the DELETE request URL is like: <p>
 * 		http://host_name/table_name/row_id?rev=rev_id <p>
 * --------------------------------------------------------------- <p>
 * The given URL MUST follow such pattern.
 * 
 * A common usage example is as follows:
 * ===============================================================
	URL url = null;
	try {
		url = new URL(ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
	} catch (MalformedURLException e) {
		logger.error("Error malformed URL: " + ConstantConfig.HOST + "/" + table_name + "/" + doc_id);
		return null;
	}
	PutGetDataTask getAll = new PutGetDataTask(
			PutGetDataTask.REQUEST_GET,
			url
			);
	String res = getAll.getResult();
	if( res == null )
		return null;
		
	JSONObject jsonObj = new JSONObject(res);;
 * ================================================================
 */
public class PutGetDataTask {
	
	private static final Logger logger = LogManager.getLogger(PutGetDataTask.class.getSimpleName());

    private int requestType = 0;
    private URL myUrl = null;
    private String jsonStr = null; // Will contain the raw JSON response as a string.

    // Request type
    public static final int REQUEST_GET = 1000;
    public static final int REQUEST_PUT = 1001;
    public static final int REQUEST_DELETE = 1002;

    /**
     * Constructor of the class.
     * @param requestType Must be REQUEST_GET, REQUEST_PUT or REQUEST_DELETE
     */
    public PutGetDataTask(int requestType, URL url){
        this.requestType = requestType;
        this.myUrl = url;
    }
    
    /**
     * Constructor of the class.
     * @param requestType Must be REQUEST_GET, REQUEST_PUT or REQUEST_DELETE
     */
    public PutGetDataTask(int requestType, URL url, String jsonStr){
    	
    	// 29/01/2015 added by Yu Sun
    	if( requestType != 1001 )
    		logger.error("Attention: the request type for json string " + jsonStr.substring(0, 30) + "... is not PUT");
    	
    	this.requestType = requestType;
        this.myUrl = url;
        this.jsonStr = jsonStr;
    }

    public String getResult(){
        	    	
        return this.connectToDB();
    }
    
    /////////////////////////////////////////////
    private String connectToDB() {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        OutputStreamWriter writer = null;

        try{
            
            logger.info("The request URL is: " + myUrl.toString());

            // Create the request to CouchDB, and open the connection
            urlConnection = (HttpURLConnection) myUrl.openConnection();
            
            switch (this.requestType) {
                // "put"
                case REQUEST_PUT:
                {
                	// 26/01/2015 Yu Sun: PUT didn't work out, so I turned to the
                	// Light Couch to have a try. 27/01/2015 Yu Sun spent almost another day
                	// I make it work out. The only key is to use json.toString() and write.flush().
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    
                    logger.info("The JSON string is : " + this.jsonStr );
                    
                    OutputStream outputStream = urlConnection.getOutputStream();
                    writer = new OutputStreamWriter( outputStream );
                    // 27/01/2015 Yu Sun: This is very wrong!
                    //writer.write(URLEncoder.encode(this.jsonStr, "UTF-8"));
                    
                    logger.trace("Writing to the DB server...");
                    writer.write( this.jsonStr );

                    writer.flush();
                    outputStream.flush(); outputStream.close();

                    logger.debug("The PUT response code is: " + String.valueOf(urlConnection.getResponseCode()));
                    logger.debug("The PUT response message is: " + urlConnection.getResponseMessage());
                    break;
                }
                // "get"
                case REQUEST_GET:
                {
                    urlConnection.setRequestMethod("GET");
                    logger.trace("Connecting to the DB server...");
                    urlConnection.connect();
                                        
                    logger.debug("The GET response code is: " + String.valueOf(urlConnection.getResponseCode()));
                    logger.debug("The GET response message is: " + urlConnection.getResponseMessage());
                    
                    break;
                }
                // "delete"
                case REQUEST_DELETE:
                {
                    urlConnection.setRequestMethod("DELETE");
                    logger.trace("Connecting to the URL...");
                    urlConnection.connect();
                    
                    logger.debug("The DELETE response code is: " + String.valueOf(urlConnection.getResponseCode()));
                    logger.debug("The DELETE response message is: " + urlConnection.getResponseMessage());
                    
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown request type");
            }
            
            // Read the input stream into a String
            InputStream inputStream = null;
            try{
            	inputStream = urlConnection.getInputStream();
            }catch(Exception e){
            	inputStream = urlConnection.getErrorStream();
            }	            
            StringBuffer buffer = new StringBuffer();
            if ( inputStream == null ) {
                logger.debug("The obtained input stream is empty");
                // Nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            logger.trace("Reading from the response...");
            while ((line = reader.readLine()) != null){
                // Since it's JSON, adding a newline isn't necessary (it won't
                // affect parsing). But it does make debugging a lot easier if you
                // print out the complete buffer for debugging.
                buffer.append(line + "\n");
            }
            logger.debug("The response message length is: " + String.valueOf(buffer.length()));

            if (buffer.length() == 0) {
                // Stream was empty.
                return null;
            }
            jsonStr = buffer.toString();
            logger.debug("The response JSON string is: " + jsonStr);
            
            return jsonStr;

        } catch (IOException e) {
        	logger.error("ERROR: ", e.toString());
            // If the code didn't successfully get the data,
            // then return null.
            return null;
        } finally {

            if( urlConnection != null ){
                urlConnection.disconnect();
            }
            if( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                	logger.error("Error closing reading stream: ", e.toString());
                }
            }
            if( writer != null ) {
                try {
                    writer.close();
                } catch (IOException e) {
                	logger.error("Error closing writing stream: ", e.toString());
                }
            }
        }
        //return null;
    }
    /////////////////////////////////////////////
}