package program;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class programCoreV2 {
	//members
	private Records _records;
	private Records _filteredRecords;
	
	//constructors
	public programCoreV2(Records records) {
		_records = new Records(records);
		_filteredRecords = new Records();
	}
	
	public programCoreV2() {
		_records = new Records();
		_filteredRecords = new Records();
	}

	//Methods
	/**
	 * This function converts a string to date in the right format for KML files.
	 * @param date - a string represents the date to convert.
	 * @param time - a string represents the time to convert.
	 * @return Calendar object.
	 */
	private static Calendar StringtoDate(String date, String time) {
		String[] datearr = date.split("-");
		String[] timearr = time.split(":");
		int year = Integer.parseInt(datearr[0]);
		int month = Integer.parseInt(datearr[1]);
		int day = Integer.parseInt(datearr[2]);
		int hour = Integer.parseInt(timearr[0]);
		int minutes = Integer.parseInt(timearr[1]);
		int sec = Integer.parseInt(timearr[2]);
		Calendar c = Calendar.getInstance();
		c.set(year, month-1,day,hour,minutes,sec);
		return c;
	}

	/**
	 * This function creates a new KML file.
	 * @param fileName - a string represents name of the file.
	 * @param records - Records object that has all the data for the KML file.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public void createFilteredFile(String pathToSave, Records records){
		File path = new File(pathToSave);
		records.toKml(path);
	}

	public void loadRecordsFromWiggleDir(String wiggleDir) {
		_records = new Records();
		File dir = new File(wiggleDir);
		_records.CSV2Records(dir);
	}
	
	public boolean isRecordsEmpty() {
		return _records.isEmpty();
	}
	
	/**
	 * This function filters SingleRecord objects by location.
	 * @param lat - latitude .
	 * @param lon - longitude .
	 * @param radius - radius to scan. 
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public void filterByLocation(double lat, double lon, double radius){
		Point2D locationPick = new Point2D.Double(lat,lon);
		Condition locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
		Records filterByLoc = _records.filter(locationCondition);
		String fileName = "FilteredByLocation("+lat+" , "+lon+")"+"Radius_"+radius+".kml";
		createFilteredFile(fileName, filterByLoc);
	}

	/**
	 * This function filters SingleRecord objects by time.
	 * @param begDay - beginning day to scan.
	 * @param begTime - beginning time to scan.
	 * @param endDay - end day to scan. 
	 * @param endTime - end time to scan.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public void filterByTime (String begDay, String begTime, String endDay, String endTime){
		Calendar beginDate = StringtoDate(begDay, begTime);
		Calendar endDate = StringtoDate(endDay, endTime);
		Condition timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;
		Records filterByTime = _records.filter(timeCondition);
		String fileName = "FilteredByDate"+ begDay + "@" + begTime + endDay + "@" + endTime +".kml";
		createFilteredFile(fileName, filterByTime);
	}

	/**
	 * This function filters SingleRecord objects by ID.
	 * @param id - ID to look for.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public void filterByID (String id){
		Condition idCondition = currSingleRec->currSingleRec.get_id().toLowerCase().equals(id.toLowerCase());
		Records filterByID = _records.filter(idCondition);
		String fileName = "FilteredByID_"+id+".kml";
		createFilteredFile(fileName, filterByID);
	}

	/**
	 * This retrieves all different MAC's from given Records object and creates a csv file of MAC and
	 their estimated location using the locateRouterAlgo class
	 * @param main Records type
	 * @return String represents a message for the user with the path of the created csv.
	 */
	public Map<String, Double> locateRouter(String mac){
		
		locateRouterAlgo a = new locateRouterAlgo(_records, mac);
		Map<String,Double> result = new HashMap<String,Double>();
		Double lat = new Double(a.getLocation().getX());
		Double lon = new Double(a.getLocation().getY());
		Double alt = new Double(a.getAlt());
		result.put("lat",lat);
		result.put("lon",lon);
		result.put("alt",alt);
		
		return result;

	}
	
	public void addCombinedCSV(String path) {
		Records combinedToAdd = new Records();
		combinedToAdd.loadRecordsFromFilev2(path);
		_records.addRecords(combinedToAdd);
	}

	public void cleanRecordsData() {
		_records = new Records();
	}
	
	public void createCSVfromRecords(String pathToSave) {
		File f = new File(pathToSave);
		_records.toCSV(f);
	}
	
	public void createKMLfromRecords(String pathToSave) {
		String fileName = "output.kml";
		pathToSave +="/"+fileName;
		File f = new File(pathToSave);
		_records.toKml(f);
	}
	
	public void filter(FilterForRecords filter) {
		_filteredRecords = _records.filterv2(filter.get_filters());
	}
	
	/**This function return number of scans in program
	 * 
	 * @return number of scans
	 */
	public int scanCount() {
		return _records.size();
	}
	
	/**This function return number of diffrent MAC's in the data structure
	 * 
	 * @return number of routers
	 */
	public int diffRouterCount() {
		return _records.numOfDiffRouter();
	}
	
	public Records get_records() {
		return _records;
	}
	
	public void setFilteredRecords(Records other) {
		_filteredRecords = new Records(other);
	}
	
	public void switchRecords() {
		Records temp = new Records(_records);
		_records = new Records(_filteredRecords);
		_filteredRecords = new Records(temp);
	}
	
	public void cleanFilteredRecords() {
		_filteredRecords = new Records();
	}


	/**
	 * This function calls other function to estimate the location of a user.
	 * @param mac1 - MAC address located by user.
	 * @param Signal1 - signal of the above MAC.
	 * @param mac2 - MAC address located by user.
	 * @param Signal2 - signal of the above MAC.
	 * @param mac3 - MAC address located by user.
	 * @param Signal3 - signal of the above MAC.
	 * @return String represents a message for the user with the estimated location.
	 * @throws Exception 
	 */
	public Map<String, Double> locateUser(String mac1, int Signal1, String mac2, int Signal2, String mac3, int Signal3) throws Exception {
		findUserAlgo a = new findUserAlgo(_records, mac1, Signal1, mac2, Signal2, mac3, Signal3);
		Map<String,Double> result = new HashMap<String,Double>();
		Double lat = new Double(a.getLocation().getX());
		Double lon = new Double(a.getLocation().getY());
		Double alt = new Double(a.getAlt());
		result.put("lat",lat);
		result.put("lon",lon);
		result.put("alt",alt);
		
		return result;

	}

	/**
	 * This function creates a CSV of MAC locations 
	 * @param fileName
	 * @param macAndLocationList
	 * @return string message of file path.
	 */
	public String createMacLocationsFile(String fileName, ArrayList<ArrayList<Object>> macAndLocationList) {
		try {
			//add headers to CSV file
			FileWriter writer = new FileWriter(fileName);//***\\output.csv was changed to /output.csv
			PrintWriter outs = new PrintWriter(writer);
			outs.println("MAC,Lat,Lon,Alt");

			for (ArrayList<Object> specifidMacDetails : macAndLocationList) {
				String mac = (String)specifidMacDetails.get(0);
				String lat = Double.toString((double)specifidMacDetails.get(1));
				String lon= Double.toString((double)specifidMacDetails.get(2));
				String alt = Double.toString((double)specifidMacDetails.get(3));
				outs.println(mac+","+lat+","+lon+","+alt);
			}	
			outs.close();
			writer.close();
		}
		catch (Exception e) {
			System.out.println("Error writing from MacLocationList to CSV. Exception:\n: "+e);
		}
		return "MAC locations CSV is ready at: "+fileName;
	}
}
