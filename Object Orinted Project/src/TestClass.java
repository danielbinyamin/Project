import java.awt.geom.Point2D;
import java.io.File;
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

	public static void filterByToKML(Scanner sc, Records r) {
		boolean ON = true;
		while (ON) {
			//Filter Picking
			System.out.println("\nPick filter. 0 to end program ");
			System.out.println("1: by location\n2: by time\n3: by ID");
			int key = sc.nextInt();
			//user picks 1,2 or 3. this is saved in "key"
			//make loop if enter 0(to create many filters
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
				double radius = sc.nextDouble();
				Condition c1 = s->locationPick.distance(s.get_location())<=radius;
				Records Fbyloc = r.filter(c1);
				Fbyloc.toKml(new File("FilteredBy_"+"Location_"+lat+"_"+longt+"_"+"Radius_"+radius+".kml"));
				System.out.println("Filtered file ready: FilteredBy_"+"Location_"+lat+"_"+longt+"_"+"Radius_"+radius+".kml");
				break;
			case 2:/*time*/
				System.out.println("Enter begging Date (YY-MM-DD)");
				String date = sc.next();
				System.out.println("Enter begging Time(HH:MM:SS)");
				String time = sc.next();
				Calendar beginDate = StringtoDate(date, time);
				System.out.println("Enter end Date (YY-MM-DD)");
				String date2 = sc.next();
				System.out.println("Enter end Time(HH:MM:SS)");
				String time2 = sc.next();
				Calendar endDate = StringtoDate(date2, time2);
				Condition c2 = s->s.get_date().after(beginDate) && s.get_date().before(endDate);
				Records FByTime = r.filter(c2);
				FByTime.toKml(new File("FilteredBy_"+"Date"+".kml"));
				System.out.println("FilteredBy_"+"Date"+".kml");

				break;
			case 3: /*id*/
				System.out.println("Enter ID");
				String id = sc.next();
				Condition c3 = s->s.get_id().equals(id);
				Records FById = r.filter(c3);
				FById.toKml(new File("FilteredBy_"+"ID_"+id+".kml"));
				System.out.println("Filtered file ready: FilteredBy_"+"ID_"+id+".kml");
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
		System.out.println("Path to save output file");
		reader = sc.nextLine()+".csv";
		File csvOutputFile= new File(reader);
		Records r = new Records();
		r.CSV2Records(wigleOutputFolder);
		r.toCSV(csvOutputFile);

		//function call for filter
		filterByToKML(sc, r);



	}

}
