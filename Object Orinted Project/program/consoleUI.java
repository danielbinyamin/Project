package program;
import java.util.Scanner;

/**
 * This class is a Console User-Interface for the program.
 * It has a "programCore" instance as a data member.
 * The user communicates "programCore" via this UI.
 */
public class consoleUI {

	//data members
	public static Scanner _sc;
	private programCore _program;
	//globals
	public final static int exit = 0;
	public final static int filterByLocation = 1;
	public final static int filterByTime = 2;
	public final static int filterByID = 3;
	public final static int locateRouter = 4;
	public final static int locateUser = 5;

	//constructors
	public consoleUI(){
		init();
	}

	/**
	 * This method initiates the class by input values from user.
	 * @param wigleDir WifleWifi files directory.
	 * @param outputDir Path to save the output file.
	 */	
	public void init(){
		//create main CSV from wiggleWifi dir
		_sc = new Scanner(System.in);
		System.out.println("Enter Path of your wiggleWifi directory folder: ");
		String wigleDir = _sc.nextLine();
		System.out.println("Enter the Path to save the output file: ");
		String outputDir = _sc.nextLine();
		_program = new programCore(wigleDir, outputDir);
	}

	/**
	 * This function lets the user pick what function he would like to execute on his main CSV file.
	 * user can choose:
	 * filter by location, filter by time, filter by id, locate a router, or locate himself.
	 * @param _sc - input from user.
	 * @param msgToShow - output to view on GUI.
	 * @param choice - which of the above functions.
	 * @param fileName - name of the output file.
	 * @throws Exception if trying to locate user by unknown MAC.
	 */

	public void menu() throws Exception{
		boolean ON = true;
		int choice=-1;
		while (ON) {
			String msgToShow=new String();
			//Filter Picking
			System.out.println("Pick option. 0 to end program ");
			System.out.println("1: filter by location\n2: filter by time\n3: filter by ID\n4: locate router\n5: locate me");
			
			try{
				choice = _sc.nextInt();
			}
			catch(Exception e){
				System.out.println(e);
				break;
			}
			//user picks 1,2 or 3. this is saved in "choice"
			//make loop if enter 0(to create many filters
			switch (choice) {

			case (exit):
				ON = false;
			System.out.println("goodbye");
			break;

			case (filterByLocation):
				System.out.println("Enter Latitude:");
			double lat = _sc.nextDouble();
			System.out.println("Enter Longitude:");
			double longt = _sc.nextDouble();
			System.out.println("Enter radius:");
			double radius = _sc.nextDouble();
			msgToShow = _program.filterByLocation(lat, longt, radius);
			System.out.println(msgToShow);
			break;

			case filterByTime:
				System.out.println("Enter begging Date (YYYY-MM-DD):");
				String begDay = _sc.next();
				System.out.println("Enter begging Time(HH:MM:SS):");
				String begTime = _sc.next();
				System.out.println("Enter end Date (YYYY-MM-DD):");
				String endDay = _sc.next();
				System.out.println("Enter end Time(HH:MM:SS):");
				String endTime = _sc.next();
				msgToShow = _program.filterByTime(begDay, begTime, endDay, endTime);
				System.out.println(msgToShow);
				break;

			case filterByID:
				_sc.nextLine();
				System.out.println("Enter ID:");
				String id = _sc.nextLine();
				msgToShow = _program.filterByID(id);
				System.out.println(msgToShow);
				break;

			case locateRouter:
				System.out.println("Enter MAC");
				String mac = _sc.next();
				msgToShow = _program.locateRouter(mac);
				System.out.println(msgToShow);	//***maybe it is better to create an KML?
				break;

			case locateUser:
				System.out.println("Enter MAC");
				String mac1 = _sc.next();
				System.out.println("Enter its signal");
				int signal1 = _sc.nextInt();
				System.out.println("Enter MAC");
				String mac2 = _sc.next();
				System.out.println("Enter its signal");
				int signal2 = _sc.nextInt();
				System.out.println("Enter MAC");
				String mac3 = _sc.next();
				System.out.println("Enter its signal");
				int signal3 = _sc.nextInt();
				msgToShow = _program.locateUser(mac1, signal1, mac2, signal2, mac3, signal3);
				System.out.println(msgToShow);	//***maybe it is better to create an KML?
				
			default:
				System.out.println("Not valid input. Please Try Again.");
			}
		}
		_sc.close();
	}

	/**
	 * main class of the program.
	 */	
	public static void main(String[] args) throws Exception {
		consoleUI CUI = new consoleUI();
		CUI.menu();
	}
}
