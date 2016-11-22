package com.common;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.spatialanalytics.function.ConstantConfig;
import com.example.spatialanalytics.function.CouchDBWholeTable;

/**
 * Yu Sun 06/04/2015
 * This class updates the fuel price according to the users' contributed prices.
 * 
 * A straightforward way is to update the fuel price once it has a new contributed price.
 * This strategy is beneficial when there are not too many users and we need to boost the app.
 * 
 * Later, however, we can use more complex price update strategies.
 * For instance, only when it has a few (say 3) contributed prices all with the same value, we
 * update its price, which means we need to validate the contributed price by comparing it with
 * others.
 * 
 * @author Yu Sun
 *
 */
public class UpdateFuelPrice {
	
	private static final Logger logger = LogManager.getLogger(UpdateFuelPrice.class.getSimpleName());
	
	// change this if query other points than fuel stations
	private String table_name = ConstantConfig.FUEL_STATION_TABLE_NAME;
	// added by Yu Sun on 06/03/2015 for efficiently fetching the whole table
	private String doc_id = ConstantConfig.ALL_STATIONS_DOC_ID;
	// name to short name map
	private static HashMap<String, String> nameToShortNameMap;
	
	public UpdateFuelPrice(){
		
		// Made according to http://www.gps-data-team.info/poi/australia/petrol/
		nameToShortNameMap = new HashMap<String, String>();
		
		nameToShortNameMap.put("BP", "BP");
		nameToShortNameMap.put("Caltex", "CTX");
		nameToShortNameMap.put("Shell", "SHL");
		nameToShortNameMap.put("United", "UNT");
		nameToShortNameMap.put("7 Eleven", "7-11");
		nameToShortNameMap.put("Mobil", "MBL");
		nameToShortNameMap.put("Coles Express", "CLZ");
		nameToShortNameMap.put("Woolworths Petrol", "WLS");
		nameToShortNameMap.put("Roadhouses", "RDH");
		nameToShortNameMap.put("E-85 Fuel", "E85");
		
	}
	
	/**
	 * This function updates the fuel price for the given station with the given fuel type
	 * and price json array.
	 * The update is to merge the stored fuel-type-price json array with the given one. For example,
	 * the merge of [{"Unleaded":102.2}, {"E85":60.1}] and [{"Unleaded":104.2}, {"LPG":52.0}] is
	 * [{"Unleaded":104.2}, {"LPG":52.0}, {"E85":60.1}].
	 * 
	 * Note that this function is only used when the fuel station already exists.
	 * If the operations include inserting a new fuel station, the function below
	 * 'insertStationAndUpdatePrice' should be used.
	 * 
	 * @param station_id -- the id of the existing station
	 * @param fuelTypePrice -- the fuel-type-price json array to be merged
	 * @return
	 * 		true, if the update successes;
	 * 		false, otherwise.
	 */
	public boolean updatePrice( String station_id, JSONArray fuelTypePrice ){
		
		JSONArray all_stations = CouchDBWholeTable
				.getWholeTableStoredInSingleDoc(table_name, doc_id);
		if( all_stations == null || all_stations.length() == 0 )
			return false;
		
		for(int i = 0; i < all_stations.length(); i++ ){
			
			try{
				JSONObject station = all_stations.getJSONObject(i);
				String id = station.getString(ConstantConfig.FUEL_STATION_COLUMN_ID);
				if( id.equals( station_id ) ){
					
					JSONArray merge_result = mergeTypePriceJSONArray(
							station.getJSONArray( ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED ),
							fuelTypePrice
							);
					station.put(ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED,
							merge_result);
					
					// update the cached whole table
					return CouchDBWholeTable.updateWholeTableStoredInSingleDoc(
							table_name, doc_id, all_stations);
				}
			} catch (JSONException e) {
				logger.error( "Error parsing the " + i + "th json object", e);
				return false;
			}
		}

		return false;
	}

	/**
	 * This function inserts the given new station (a json object) into the database.
	 * If the given fuel-type-price json is not null, we also merge the fuel-type-price
	 * json array of the new station with the given one.
	 * 
	 * For example, the merge of [{"Unleaded":102.2}, {"E85":60.1}] and [{"Unleaded":104.2},
	 * {"LPG":52.0}] is [{"Unleaded":104.2}, {"LPG":52.0}, {"E85":60.1}].
	 * 
	 * @param new_station -- the new station in a json object, including fields: brand, latitude,
	 * 						longitude, fuel_provided, source.
	 * @param fuelTypePrice -- the fule-type-price json array to be merged
	 * @return
	 * 	 		true, if the update (insertion) successes;
	 * 			false, otherwise.
	 */
	public boolean insertStationAndUpdatePrice( JSONObject new_station, JSONArray fuelTypePrice ){
		
		//merge the fuel-type-price json array
		if( fuelTypePrice != null ){
			
			try {
				new_station.put(
						ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED,
						this.mergeTypePriceJSONArray(
							new_station.getJSONArray(ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED),
							fuelTypePrice
							)
						);
			} catch (JSONException e) {
				logger.error("Processing json input", e);
				e.printStackTrace();
			}
		}
			
		JSONArray all_stations = CouchDBWholeTable
				.getWholeTableStoredInSingleDoc(table_name, doc_id);
		try{
			//Assume there's no delete, assign the new station an ascending id
			String currentMaxId = all_stations.getJSONObject( all_stations.length() - 1 )
					.getString(ConstantConfig.FUEL_STATION_COLUMN_ID);
			int new_station_id = Integer.valueOf(currentMaxId) + 1;
			new_station.put(ConstantConfig.FUEL_STATION_COLUMN_ID, String.valueOf(new_station_id));
			
			//Get short name for the station
			String brand = new_station.getString(ConstantConfig.FUEL_STATION_COLUMN_BRAND);
			String short_name = brand;
			if( nameToShortNameMap.containsKey( brand ) ){
				short_name = nameToShortNameMap.get( brand );
			}
			new_station.put(ConstantConfig.FUEL_STATION_COLUMN_SHORT_NAME, short_name);
			
			//Add the new station into the JSONArray
			all_stations.put( new_station );
			
			return CouchDBWholeTable.updateWholeTableStoredInSingleDoc(
				table_name, doc_id, all_stations);
			
		} catch (JSONException e){
			logger.error("Error getting current largest station id", e);
			return false;
		}
	}
	
	/**
	 * This function merges the two given json arrays.
	 * @param old_array -- must be not null
	 * @param new_array -- must be not null
	 * @return the merge result of old_array and new_array 
	 */
	public JSONArray mergeTypePriceJSONArray( JSONArray old_array, JSONArray new_array ){
		
		if( old_array.length() == 0 )
			return new_array;
		if( new_array.length() == 0 )
			return old_array;
		
		JSONArray result = new JSONArray();
		
		// Map< fuel_name, array_index >
		HashMap<String, Integer> old_type_price_map = new HashMap<>();
		
		// insert the old array
		for(int i = 0; i < old_array.length(); i++){

			try{
				old_type_price_map.put(
						old_array.getJSONObject(i).getString(ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED_NAME),
						i // array index
						);
				result.put( old_array.getJSONObject(i) );
			} catch (JSONException e){
				logger.error( "Error parsing the " + i + "th json object", e);
			}
		}// end first for
		
		// insert the new array
		for(int i = 0; i < new_array.length(); i++){
			
			try{
				String fuel_name = new_array
					.getJSONObject(i).getString(ConstantConfig.FUEL_STATION_COLUMN_FUEL_PROVIDED_NAME);
				if( old_type_price_map.containsKey( fuel_name ) ){ // the fuel type is old, update price
					int index = old_type_price_map.get( fuel_name ); // get index
					result.put(index, new_array.getJSONObject(i)); // replace
				} else { // add the new fuel type into the result 
					result.put( new_array.getJSONObject(i) );
				}
			} catch (JSONException e){
				logger.error( "Error parsing the " + i + "th json object", e);
			}
		}// end second for
		
		return result;
	}//end function

}
