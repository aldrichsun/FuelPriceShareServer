package com.spatialanalytics.config;

/**
 * 
 * @author Han Li
 *
 */
public class ConstantConfig {
	/**
     * Fuel Settings
     */
    public static final String KEY_FUEL_PRICE = "price";
    public static final String KEY_FUEL_BRAND = "fuel";

    //Rectangle
    public static final String KEY_RECT_LEFT = "left";
    public static final String KEY_RECT_TOP = "top";
    public static final String KEY_RECT_RIGHT = "right";
    public static final String KEY_RECT_BOTTOM = "bottom";

    public static final String FLAG_IS_SELECTED="isSelected";

    public static final String KEY_FUEL="fuel";
    public static final String KEY_ALL_FUEL_BRAND="all_fuel_brand";



    /**
     * petrol stations
     */

    public final static String KEY_PETROL_STATION="fuel_station";
    public static final  String KEY_PETROL_STATION_ID="_id";
    public final static String KEY_PETROL_STATION_NAME="name";


    /**
     * Contribute price parameters
     */
    public static final String KEY_CONTRIBUTE_PRICE_TRANSACTION_ID="transaction_id";
    public static final String KEY_LONGITUDE="longitude";
    public static final String KEY_LATITUDE="latitude";
    public static final String KEY_CAN_GET_LOCATION="can_get_location";
    //////////// added by Yu Sun 06/04/2015 /////////////////////
    public static final String KEY_FUEL_PROVIDED = "fuel_provided";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_SOURCE = "source";
    /////////////////////////////////////////////////////////////
    
    public static final String KEY_CONTRIBUTE_PRICE_PROCESS_STATUS="status";
    public static final String KEY_CONTRIBUTE_PRICE_STATUS_RETRIEVE_PETROL_STATION="retrieve_petrol_station";
    public static final String KEY_CONTRIBUTE_PRICE_STATUS_PROCESS_FUEL_IMAGE="process_fuel_image";

    /**************************************************************************/
    /** Added by Yu Sun 02/04/2015: contribute price table column names */
    public static final String COLUMN_TRANSACTION_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FUEL_STATION_ID = "station_id";
    public static final String COLUMN_FUEL_TYPE_PRICE = "fuel";
    public static final String COLUMN_CONTRIBUTE_TIME = "time";
    public static final String COLUMN_CONTRIBUTE_LATITUDE = "contribute_latitude";
    public static final String COLUMN_CONTRIBUTE_LONGITUDE = "contribute_longitude";
    public static final String COLUMN_CREDIT_TO_USER = "gain_credit";
    /**************************************************************************/

    /**
     * File upload (image mainly)
     */
    public static final String KEY_FILE_UPLOAD_FILE_DATA="file";
    public  static final String KEY_FILE_UPLOAD_STRING_DATA="data";



    /**
     * JSON response codes
     */
    public static final String KEY_SUCCESS = "ok";
    public static final String KEY_ERROR = "error";


    /**
     * User information field
     */
    public static final String KEY_USER="user";

    public static final String KEY_ERROR_MSG = "error_msg";
    public static final String KEY_UID = "_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_Password = "password";    
    public static final String KEY_CREDIT = "credit";
    
    /**
     * folders to store uploaded images / files
     */
    public static final String KEY_FUEL_IMAGE_FOLDER="/fuel_image/";//Folder that stores uploaded fuel image. Need to be reset

    public static final String KEY_PROFILE_IMAGE_FOLDER="/user_profile_photo/";//Folder that stores uploaded profile image. Need to be reset

   /**
    * SUN YU API the timeout parameter for internect connectino when retrieving the nearby fuel stations
    */
    public static final int MAX_TIME_OUT = 60000;
    
    
    public static final double DEFAULT_RANGE_DIST = 0.5; //default range query distance for range query
}
