import java.awt.geom.Point2D;
import java.io.File;
import java.util.Calendar;
import java.util.Scanner;


public class consoleUI {

	//globals
	public final static int exit = 0;
	public final static int filterByLocation = 1;
	public final static int filterByTime = 2;
	public final static int filterByID = 3;
	public final static int locateRouter = 4;
	public final static int locateUser = 5;
	public static Scanner sc;
	private programCore _program;
	

	public consoleUI(){
		init();
	}

	public void init(){
		//create main CSV from wiggleWifi dir
		sc = new Scanner(System.in);
		System.out.println("Enter Path of your wiggleWifi directory folder: ");
		String wigleDir = sc.nextLine();
		System.out.println("Enter the Path to save the output file: ");
		String outputDir = sc.nextLine();
		_program = new programCore(wigleDir, outputDir);
	}

	/**
	 * This function lets the user pick what function he would like to execute on his main CSV file.
	 * user can choose:
	 * filter by location, filter by time, filter by id, locate a router, or locate himself.
	 * @param sc - input from user.
	 * @param msgToShow - output to view on GUI.
	 * @param choice - which of the above functions.
	 * @param fileName - name of the output file.
	 */

	public void menu(){
		boolean ON = true;
		int choice=-1;
		while (ON) {
			String msgToShow=new String();
			//Filter Picking
			System.out.println("Pick option. 0 to end program ");
			System.out.println("1: filter by location\n2: filter by time\n3: filter by ID\n4: locate router\n5: locate me");
			
			try{
				choice = sc.nextInt();
			}
			catch(Exception e){
				System.out.println(e);
				break;
			}
			//user picks 1,2 or 3. this is saved in "key"
			//make loop if enter 0(to create many filters
			String fileName = new String();
			File filteredRecord;
			switch (choice) {

			case (exit):
				ON = false;
			System.out.println("goodbye");
			break;

			case (filterByLocation):
				System.out.println("Enter Latitude:");
			double lat = sc.nextDouble();
			System.out.println("Enter Longitude:");
			double longt = sc.nextDouble();
			System.out.println("Enter radius:");
			double radius = sc.nextDouble();
			msgToShow = _program.filterByLocation(lat, longt, radius);
			System.out.println(msgToShow);
			break;

			case filterByTime:
				System.out.println("Enter begging Date (YYYY-MM-DD):");
				String begDay = sc.next();
				System.out.println("Enter begging Time(HH:MM:SS):");
				String begTime = sc.next();
				System.out.println("Enter end Date (YYYY-MM-DD):");
				String endDay = sc.next();
				System.out.println("Enter end Time(HH:MM:SS):");
				String endTime = sc.next();
				msgToShow = _program.filterByTime(begDay, begTime, endDay, endTime);
				System.out.println(msgToShow);
				break;

			case filterByID:
				//***sc.nextLine();
				System.out.println("Enter ID:");
				String id = sc.nextLine();
				msgToShow = _program.filterByID(id);
				System.out.println(msgToShow);
				break;

			case locateRouter:
				System.out.println("Enter MAC");
				String mac = sc.next();
				msgToShow = _program.locateRouter(mac);
				System.out.println(msgToShow);	//***maybe it is better to create an KML?
				break;

			case locateUser:
				System.out.println("Create a new Wiggle-Wifi file and enter path of it.");
				String newWiggle = sc.nextLine();
				msgToShow = _program.locateUser(newWiggle);
				System.out.println(msgToShow);	//***maybe it is better to create an KML?
				
			default:
				System.out.println("Not valid input. Please Try Again.");
			}
		}
		sc.close();
	}

	public static void main(String[] args) {
		consoleUI CUI = new consoleUI();
		CUI.menu();
	}
}
