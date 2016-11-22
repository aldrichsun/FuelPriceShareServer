package com.example.spatialanalytics.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A general Http connection class, specifically given a URL, this class submits the GET request
 * represented by the URL (regardless of which server and and what request), and then read and
 * return the server response as a string back to the calling code.
 * Created by Yu Sun on 6/02/2015.
 *
 * Function: given a URL, this class submits the request represented by the URL, return the
 * server response as a string.
 * Input: url -- a request url (regardless of which server and and what GET request)
 * Output: A string represent the response of the server.
 * If any error occurs, it returns null.
 *
 * Note that Whether the string is null or empty is exactly as the server's response.
 * To correctly submit the request and process the response, please refer to the request
 * API's documentation. This class will do nothing to check the completeness of the request
 * but to open the http connection and return the server's response.
 */
public class APIRequest {

    private static final Logger logger = LogManager.getLogger(APIRequest.class.getSimpleName());

    public APIRequest(){

    }

    /**
     * This function submits the request represented by url, return the server response.
     * @param url -- a request url (regardless of which server and and what GET request).
     * @return A string represent the response of the server.
     * 		   If any error occurs, it returns null.
     */
    public String getResponse( URL url ){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resStr = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            logger.trace("Connecting to the URL...");
            urlConnection.connect();

            logger.debug("The response code is: " + String.valueOf(urlConnection.getResponseCode()));
            logger.debug("The response message is: " + urlConnection.getResponseMessage());

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if ( inputStream == null ) {
                logger.error("The obtained input stream is empty");
                // Nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            logger.trace("Reading from the URL...");
            while ((line = reader.readLine()) != null){
                // Since it's JSON, adding a newline isn't necessary (it won't
                // affect parsing). But it does make debugging a lot easier if you
                // print out the complete buffer for debugging.
                buffer.append(line + "\n");
            }

            logger.debug("The returned content length is: " + String.valueOf(buffer.length()));

            if (buffer.length() == 0) {
                // Stream was empty.
                return null;
            }
            resStr = buffer.toString();
            logger.debug("The returned string is: " + resStr);

        } catch (IOException e) {

            logger.error("Error reading from the server", e);
            // If the code didn't successfully get the data,
            // then return null.
            return null;

        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                	logger.error("Error closing reading stream: ", e);
                }
            }
        }
        return resStr;
    }
}

