package program;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is version 2 of the original class.
 * This class is the main executable class.
 * Each UI communicates with an instance of this class and gets the wanted output for any method.
 * @author daniel & Tal
 *
 */
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
	
	public ResultSet queryDB(Connection connection, String query){
		try{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
		}
		catch (Exception error){
			System.out.println("error quering the DB: "+error);
			return null;
		}
	}

	public Connection initConnectionToDB(String url, String username, String password){
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Connection addCombinedDB(Connection connection, String table) throws Exception {
		//get table from DB
		Statement stmt = connection.createStatement();
		String query = "select * from " + table + ";";
		ResultSet rs = queryDB(connection, query);
		Records combinedRecords = new Records();
		combinedRecords.loadRecordsFromDB(rs);
		_records.addRecords(combinedRecords);
		return connection;
	}
	
	public void reAddCombinedDB(Connection con, String table) throws Exception {
		Records combinedToAdd = new Records();
		combinedToAdd.reloadRecordsFromDB(con, table);
		_records.addRecords(combinedToAdd);
	}

	public void cleanRecordsData() {
		_records = new Records();
	}

	public String getLastModified(Connection connection, String table) throws ClassNotFoundException, SQLException {
		//read data from table
		String db = connection.getCatalog();
		String query = "SELECT UPDATE_TIME FROM information_schema.tables WHERE  TABLE_SCHEMA = '" + db + "' AND TABLE_NAME = '"+table+"'";
		ResultSet rs = queryDB(connection, query);
		rs.next();
		//return value
		return rs.getString(1);
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

	public boolean checkIfMacExistsInRecords(String mac) {
		return _records.doesMacExist(mac);
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
