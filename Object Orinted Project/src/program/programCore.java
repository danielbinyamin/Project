package program;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

import org.omg.stub.java.rmi._Remote_Stub;

import de.micromata.opengis.kml.v_2_2_0.atom.Link;

import java.lang.Math;

/**
 * This class is the main executable class.
 * Each UI communicates with an instance of this class and gets the wanted output for any method.
 */
public class programCore {

	//members
	private File _wigleDir, _outputDir;
	private Records _records;

	//constructors
	public programCore(String wigleDir, String outputDir){
		_wigleDir = new File(wigleDir);
		_records = new Records();
		_records.CSV2Records(_wigleDir);

		_outputDir = new File(outputDir);
		_records.toCSV(_outputDir);
	}

	//methods
	/**
	 * This function converts a string to date in the right format for KML files.
	 * @param date - a string represents the date to convert.
	 * @param time - a string represents the time to convert.
	 * @return Claendar object.
	 */
	public static Calendar StringtoDate(String date, String time) {
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
	 * This function loads a new Records object from given path, or the existing Records if given path is null.
	 * After loading Records object, it passes on all macs in Records and estimating their location by the first algorithm. 
	 * @param loadFrom - a string represents name of the file.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public ArrayList<String> findAllMACsLocation(String loadFrom) {
		
		if (loadFrom != null) {	//if a new Records need to be loaded from file
			Records loadedRecords = new Records(loadFrom);
			_records = loadedRecords;
		}
		ArrayList<String> allMacs = new ArrayList<>();
		for (SingleRecord SR : _records.getSingleRecordsList()) {
			for (Wifi wifi : SR.get_WifiList()) {
				String MAC = wifi.get_MAC();
				if (!allMacs.contains(MAC)) {
					allMacs.add(MAC);
				}
			}
		}

		ArrayList<String> macsEstimatedLocation = new ArrayList<>();
		for (String MAC : allMacs) {
			String estimansLocation = locateRouter(MAC);
			String fullLine = MAC+" - "+estimansLocation;
			macsEstimatedLocation.add(fullLine);
		}		
		return macsEstimatedLocation;
	}



	/**
	 * This function creates a new KML file.
	 * @param fileName - a string represents name of the file.
	 * @param records - Records object that has all the data for the KML file.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public String createFilteredFile(String fileName, Records records){
		File filteredRecord = new File(_outputDir + "/" + fileName); 
		records.toKml(filteredRecord);
		return "Filtered file ready.\nPath to filtered file: " + _outputDir + "\nFiltered file ready: " + fileName;
	}

	/**
	 * This function filters SingleRecord objects by location.
	 * @param lat - latitude .
	 * @param lon - longitude .
	 * @param radius - radius to scan. 
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public String filterByLocation(double lat, double lon, double radius){
		Point2D locationPick = new Point2D.Double(lat,lon);
		Condition locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
		Records filtByLoc = _records.filter(locationCondition);
		String fileName = "FilteredByLocation("+lat+" , "+lon+")"+"Radius_"+radius+".kml";
		String msgToShow = createFilteredFile(fileName, filtByLoc);
		return msgToShow;
	}

	/**
	 * This function filters SingleRecord objects by time.
	 * @param begDay - beginning day to scan.
	 * @param begTime - beginning time to scan.
	 * @param endDay - end day to scan. 
	 * @param endTime - end time to scan.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public String filterByTime (String begDay, String begTime, String endDay, String endTime){
		Calendar beginDate = StringtoDate(begDay, begTime);
		Calendar endDate = StringtoDate(endDay, endTime);
		Condition timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;
		Records filtByTime = _records.filter(timeCondition);
		String fileName = "FilteredByDate"+ begDay + "@" + begTime + endDay + "@" + endTime +".kml";
		String msgToShow = createFilteredFile(fileName, filtByTime);
		return msgToShow;
	}

	/**
	 * This function filters SingleRecord objects by ID.
	 * @param id - ID to look for.
	 * @return String represents a message for the user with the name and location of the file.
	 */
	public String filterByID (String id){
		Condition idCondition = currSingleRec->currSingleRec.get_id().toLowerCase().equals(id.toLowerCase());
		Records filtByID = _records.filter(idCondition);
		String fileName = "FilteredByID_"+id+".kml";
		String msgToShow = createFilteredFile(fileName, filtByID);
		return msgToShow;
	}

	/**
	 * This function calls other function to estimate the location of a router.
	 * @param mac - MAC address to locate.
	 * @return String represents a message for the user with the estimated location.
	 */
	public String locateRouter (String mac){
		locateRouterAlgo WCP = new locateRouterAlgo(_records, mac);
		Point2D location = WCP.getLocation();
		double lat = location.getX();
		double lon = location.getY();
		double alt = WCP.getAlt();
		String msgToShow = "("+lat+","+lon+","+alt+")";
		return msgToShow;
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
	 */
	public String locateUser (String mac1, int Signal1, String mac2, int Signal2, String mac3, int Signal3) throws Exception{
		try{
			findUserAlgo userLocation = new findUserAlgo(_records, mac1, Signal1, mac2, Signal2, mac3, Signal3);
			Point2D location = userLocation.getLocation();
			double lat = location.getX();
			double lon = location.getY();
			double alt = userLocation.getAlt();
			String msgToShow = "("+lat+","+lon+","+alt+")";
			return msgToShow;
		}
		catch(Exception e){
			/*no need to throw exception as "userLocation" will throw */
			return null;
		}
	}
}
