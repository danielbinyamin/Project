package program;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

import org.omg.stub.java.rmi._Remote_Stub;

import de.micromata.opengis.kml.v_2_2_0.atom.Link;

import java.lang.Math;

/**
 * NOTE: This class is not used in GUI.
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
	
	public programCore() {
		_wigleDir = new File("");
		_outputDir = new File("");
		_records = new Records();
		
	}

	//methods
	/**
	 * This function converts a string to date in the right format for KML files.
	 * @param date - a string represents the date to convert.
	 * @param time - a string represents the time to convert.
	 * @return Calendar object.
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
	 * This retrieves all different MAC's from given Records object and creates a csv file of MAC and
	 their estimated location using the locateRouterAlgo class
	 * @param main Records type
	 * @return String represents a message for the user with the path of the created csv.
	 */
	public String locateRouters (Records main, String output){
		ArrayList<SingleRecord> dataList = main.getSingleRecordsList();
		ArrayList<String> macList = new ArrayList<>();
		for (SingleRecord singleRecord : dataList) {
			for (Wifi network : singleRecord.get_WifiList()) {
				String mac = network.get_MAC();
				if(!macList.contains(mac))
					macList.add(mac);
			}
		}
		ArrayList<ArrayList<Object>> macAndLocationList = new ArrayList<>();
		for (String mac : macList) {

			locateRouterAlgo WCP = new locateRouterAlgo(main, mac);
			Point2D location = WCP.getLocation();
			double lat = location.getX();
			double lon = location.getY();
			double alt = WCP.getAlt();
			ArrayList<Object> specificMacDetails = new ArrayList<>();
			specificMacDetails.addAll(Arrays.asList(mac, lat,lon,alt));
			macAndLocationList.add(specificMacDetails);

		}
		String msgToShow = createMacLocationsFile(output, macAndLocationList);

		return msgToShow;
	}

	public Records get_records() {
		return _records;
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
	public Records locateUser (Records main, Records noGpsRecords) throws Exception{
		ArrayList<SingleRecord> emptyGps = noGpsRecords.getSingleRecordsList();
		ArrayList<SingleRecord> filledGps = new ArrayList<>();
		int index = 0;
		for (SingleRecord currentRecord : emptyGps) {
			
			ArrayList<Wifi> wifiList = currentRecord.get_WifiList();
			String mac1="",mac2="",mac3="";
			int signal1=0,signal2=0,signal3=0;
			String[] macArr = {mac1,mac2,mac3};
			int[] signalArr = {signal1,signal2,signal3};
			for (int i = 0; i < 3; i++) {
				try {
					macArr[i] = wifiList.get(i).get_MAC();
					signalArr[i]=wifiList.get(i).get_signal();
				} catch ( IndexOutOfBoundsException e ) {
					macArr[i] = "-1";
					signalArr[i] = -1;
				}
			}

			try{
				findUserAlgo userLocation = new findUserAlgo(main, macArr[0], signalArr[0], macArr[1], signalArr[1], macArr[2], signalArr[2]);
				Point2D location = userLocation.getLocation();
				double alt = userLocation.getAlt();
				emptyGps.get(index).set_location(location);
				emptyGps.get(index).set_altitude(alt);
				index++;
			}
			catch(Exception e){
				/*no need to throw exception as "userLocation" will throw */
				Point2D location = new Point2D.Double(-1, -1);
				emptyGps.get(index).set_location(location);
				emptyGps.get(index).set_altitude(-1);
				index++;
			}
		}//end for
		Records fullGps = new Records(emptyGps);
		return fullGps;

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

}
