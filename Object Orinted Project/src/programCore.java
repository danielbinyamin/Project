
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.lang.Math;

/**
 * This class is the main executable class.
 * from here we:
 * 1) pick our directory to work with.
 * 2) export out final CSV
 * 3) filter our final CSV to a KML 
 */
public class programCore {

	//members
	private File _wigleDir, _outputDir;
	private Records _records;

	public programCore(String wigleDir, String outputDir){
		_wigleDir = new File(wigleDir);
		_outputDir = new File(outputDir);
		_records = new Records();
		_records.CSV2Records(_wigleDir);
		_records.toCSV(_outputDir);
	}

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

	public String createFilteredFile(String fileName, Records records){
		File filteredRecord = new File(_outputDir + "\\" + fileName); 
		records.toKml(filteredRecord);
		return "Filtered file ready.\nPath to filtered file: " + _outputDir + "\nFiltered file ready: " + fileName;
	}

	public String filterByLocation(double lat, double lon, double radius){
		Point2D locationPick = new Point2D.Double(lat,lon);
		Condition locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
		Records filtByLoc = _records.filter(locationCondition);
		String fileName = "FilteredByLocation("+lat+" , "+lon+")"+"Radius_"+radius+".kml";
		String msgToShow = createFilteredFile(fileName, filtByLoc);
		return msgToShow;
	}
	
	public String filterByTime (String begDay, String begTime, String endDay, String endTime){
		Calendar beginDate = StringtoDate(begDay, begTime);
		Calendar endDate = StringtoDate(endDay, endTime);
		Condition timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;
		Records filtByTime = _records.filter(timeCondition);
		String fileName = "FilteredByDate"+ begDay + "@" + begTime + endDay + "@" + endTime +".kml";
		String msgToShow = createFilteredFile(fileName, filtByTime);
		return msgToShow;
	}
	
	public String filterByID (String id){
		//***sc.nextLine();
		Condition idCondition = currSingleRec->currSingleRec.get_id().equals(id);
		Records filtByID = _records.filter(idCondition);
		//***set here input path to save KML file.
		String fileName = "FilteredByID_"+id+".kml";
		String msgToShow = createFilteredFile(fileName, filtByID);//
		return msgToShow;
	}
	
	public String locateRouter (String mac){
		WeightedCenterPoint WCP = new WeightedCenterPoint(_records, mac);
		Point2D location = WCP.getLocation();
		double lat = location.getX();
		double lon = location.getY();
		double alt = WCP.getAlt();
		String msgToShow = "("+lat+","+lon+","+alt+")";
		return msgToShow;
	}
	
	public String locateUser (String pathToNewWiggleFile){
		Records tempRecords = new Records();
		tempRecords.CSV2Records(new File(pathToNewWiggleFile));
		ArrayList<SingleRecord> srList = _records.getSingleRecordsList();
		for (SingleRecord singleRecord : srList) {
			
		}
		WeightedCenterPoint WCP = new WeightedCenterPoint(tempRecords, "");
		Point2D location = WCP.getLocation();
		double lat = location.getX();
		double lon = location.getY();
		double alt = WCP.getAlt();
		String msgToShow = "("+lat+","+lon+","+alt+")";
		return msgToShow;
	}
}
