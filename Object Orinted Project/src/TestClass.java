
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
/**
 * This class is the main executable class
 * TODO: Need to test filterByToKml!
 * 
 *
 */
public class TestClass {

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

	public static void filterByToKML(Scanner sc, Records records, String pathToSaveFile) {
		boolean ON = true;
		while (ON) {
			//Filter Picking
			System.out.println("\nPick filter. 0 to end program ");
			System.out.println("1: by location\n2: by time\n3: by ID");
			int key = sc.nextInt();
			//user picks 1,2 or 3. this is saved in "key"
			//make loop if enter 0(to create many filters
			
			String fileName = new String();
			File filteredRecord;
			
			switch (key) {
			case 0:
				ON = false;
				break;
			case 1:/*location*/
				System.out.println("Enter Latitude");
				double lat = sc.nextDouble();
				System.out.println("Enter Longitude");
				double longt = sc.nextDouble();
				Point2D locationPick = new Point2D.Double(lat,longt);
				System.out.println("Enter radius");
				double radius = sc.nextDouble();	//***
				Condition locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
				Records filtByLoc = records.filter(locationCondition);
				//***set here input path to save KML file.
				fileName = "FilteredBy_"+"Location_"+lat+"_"+longt+"_"+"Radius_"+radius+".kml";
				filteredRecord = new File(pathToSaveFile + "\\" + fileName); 
				filtByLoc.toKml(filteredRecord);
				System.out.println("Filtered file ready.");
				System.out.println("Path to filtered file: " + pathToSaveFile);
				System.out.println("Filtered file ready: " + fileName);
				break;
			case 2:/*time*/
				System.out.println("Enter begging Date (YYYY-MM-DD)");
				String begDay = sc.next();
				System.out.println("Enter begging Time(HH:MM:SS)");
				String begTime = sc.next();
				Calendar beginDate = StringtoDate(begDay, begTime);
				System.out.println("Enter end Date (YYYY-MM-DD)");
				String endDay = sc.next();
				System.out.println("Enter end Time(HH:MM:SS)");
				String endTime = sc.next();
				Calendar endDate = StringtoDate(endDay, endTime);
				//Condition timeCondition = currSingleRec->currSingleRec.get_date().after(beginDate) && currSingleRec.get_date().before(endDate);
				Condition timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;
				Records filtByTime = records.filter(timeCondition);
				//***set here input path to save KML file.
				fileName = "FilteredBy_"+"Date"+".kml";
				filteredRecord = new File(pathToSaveFile + "\\" + fileName); 
				filtByTime.toKml(filteredRecord);
				System.out.println("Filtered file ready.");
				System.out.println("Path to filtered file: " + pathToSaveFile);
				System.out.println("Filtered file ready: " + fileName);
				break;
			case 3: /*id*/
				sc.nextLine();
				System.out.println("Enter ID");
				String id = sc.nextLine();
				Condition idCondition = currSingleRec->currSingleRec.get_id().equals(id);
				Records filtByID = records.filter(idCondition);
				//***set here input path to save KML file.
				fileName = "FilteredBy_"+"ID_"+id+".kml";
				filteredRecord = new File(pathToSaveFile + "\\" + fileName); 
				filtByID.toKml(filteredRecord);
				System.out.println("Filtered file ready.");
				System.out.println("Path to filtered file: " + pathToSaveFile);
				System.out.println("Filtered file ready: " + fileName);
				break;
			default: break;
			}
		}
	}


	public static void main(String[] args) {
		//create main CSV from wiggleWifi dir
		Scanner sc = new Scanner(System.in);
		System.out.println("Path of wiggleWifi output folder: ");
		String reader = sc.nextLine();
		File wigleOutputFolder = new File(reader);
		System.out.println("Path to save output files");
		reader = sc.nextLine();
		String pathToSaveFile = reader;
		String csvOutputPath = pathToSaveFile + "\\output.csv";
		File csvOutputFile= new File(csvOutputPath);
		Records r = new Records();
		r.CSV2Records(wigleOutputFolder);
		r.toCSV(csvOutputFile);

		//function call for filter
		filterByToKML(sc, r, pathToSaveFile);
	}

}
