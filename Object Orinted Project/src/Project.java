import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class Project {

	public static void createCSV (File input, File output){
		try{
			File[] listOfFiles = input.listFiles();
			//add headers to CSV file
			FileWriter writer = new FileWriter(output);
			PrintWriter outs = new PrintWriter(writer);
			outs.print("Time,id,Lat,Lon,Alt,Num_Of_Networks");
			for (int i = 1; i < 11; i++) {
				outs.print(",SSID"+i+",MAC"+i+",Frequncy"+i+",Signal"+i);
			}
			outs.println();		//one line down in the CSV output file.
			for (File file : listOfFiles) {
				ArrayList<ArrayList<WigleLine>> PITarr = new ArrayList<>();	//PITarr = array of arrays. each inner array has scans from the same time.
				if (!file.isDirectory() && file.getName().contains("WigleWifi")) {	int time = 0;	//time = number of different "time" string. 
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				String[] headerOfInputCSV = line.split(",");
				String deviceID = headerOfInputCSV[4].substring(7);		//same device ID to all lines in a file.
				line = br.readLine();	//to read the first line of data.
				line = br.readLine();	
				while (line != null){	//for each line, convert it to WigleLine and add it to an array with the same time.
					WigleLine newLine = new WigleLine(line,deviceID);
					if (PITarr.size()==0 || PITarr.size()<time){
						ArrayList<WigleLine> arrayPerTime = new ArrayList<>();
						arrayPerTime.add(newLine);
						PITarr.add(time, arrayPerTime);
					}
					else{
						String timeOfLastAddedLine = PITarr.get(time).get(0).get_time(); 
						if (timeOfLastAddedLine.equals(newLine.get_time())){
							PITarr.get(time).add(newLine);
						}
						else{	//if (timeOfLastAddedLine < newLine.time())
							time++;
							ArrayList<WigleLine> arrayPerTime = new ArrayList<>();
							arrayPerTime.add(newLine);
							PITarr.add(time, arrayPerTime);
						}
					}
					line = br.readLine();	//continue to the next line
				}
				br.close();
				}	// all lines from the file are now sorted by "time" in "PITarr". now, for each cell in PITarr: 1)sort by signal. 2)add first 10 (or numOfLines<10) lines to the CSV file.
				//for each PIT: 1)sort it by signal. 2)add to CSV (Time, ID, Lat, Lon, Alt, #WiFi networks). 3)add all the networks by loop.
				for (int specificTime = 0; specificTime < PITarr.size(); specificTime++){
					ArrayList<WigleLine> currentSpecificTimeList = PITarr.get(specificTime);
					Collections.sort(currentSpecificTimeList);	//sort list of PIT
					WigleLine line = currentSpecificTimeList.get(0);
					outs.print(line.get_time()+","+line.get_id()+","+line.get_lat()+","+line.get_lon()+","+line.get_alt()+","+Math.min(10, currentSpecificTimeList.size())+",");
					int networkIndex = 0;
					for (WigleLine network : currentSpecificTimeList)
					{	//fill rest of line with up to 10 WIFIs
						outs.print(network.get_ssid()+","+network.get_mac()+","+network.get_channel()+","+network.get_rssi()+",");
						networkIndex++;
						if (networkIndex==10)
							break;
					}
					outs.println();
				}	
			}	//all files are now in CSV file.
			outs.close();
			writer.close();
		}
		catch(Exception ex){
			System.out.println("Error creating CSV file. Exception: \n"+ex);
		}
	}

	public static ArrayList<String[]> lineToWifiList(String[] line) {
		ArrayList<String[]> wifiList = new ArrayList<String[]>();
		int j=0;
		String[] currentWifi = new String[4];
		for (int i = 6; i < line.length; i++) {
			if (j==4) {
				j=0;
				wifiList.add(new String[]{currentWifi[0],currentWifi[1],currentWifi[2],currentWifi[3]});
			}
			currentWifi[j] = line[i];
			j++;
		}
		return wifiList;

	}

	public static String FilterBy(File csv, Condition c, String by) {
		String fileName = "Filtered_By_"+by+".kml";
		try {
			FileWriter writer = new FileWriter(fileName);
			PrintWriter outs = new PrintWriter(writer);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String StringLine = br.readLine();
			StringLine = br.readLine(); //drop line

			//adds 2 headers to kml file
			outs.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
			outs.println("<Document>");
			String[] line;
			//runs through all lines in main csv file
			while (StringLine!=null) {
				line = outputLineToArr(StringLine); //turn String line to a split String array
				if (true){//(c.test(line)) {  //checks if the condition applied is true on current line
					ArrayList<String[]> wifiList = lineToWifiList(line);//list of [4]arrays of wifi
					outs.println("<Placemark>\n<name>"+line[6]+"</name>");
					outs.println("<description>List of Wifi: ");
					for (String[] s : wifiList) { //writing Wifi list in description box
						outs.println("Wifi #"+wifiList.indexOf(s));
						outs.println(Arrays.toString(s));
					}
					outs.println("</description>\n<Point>");
					outs.println("<coordinates>"+line[3]+","+line[2]+"</coordinates>"); 
					outs.println("</Point>\n</Placemark>");					
				}
				StringLine = br.readLine();
			}
			outs.println("</Document>");
			outs.println("</kml>");
			br.close();
			outs.close();
			writer.close();

		}

		catch(IOException ex) {
			System.out.println("Failed to filter by"+by+" Exception: \n"+ex);
		}
		return fileName;
	}

	public static String[] outputLineToArr(String line) {
		String[] data = line.split(",");
		return data;
	}

	public static void filterData(Scanner sc, File outputFile){
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
			case 1:
				System.out.println("Enter Latitude");
				double lat = sc.nextDouble();
				System.out.println("Enter Longitude");
				double longt = sc.nextDouble();
				System.out.println("Enter radius");
				double radius = sc.nextDouble();
				Condition c;// = s->Math.hypot(lat-Double.parseDouble(s[2]),longt-Double.parseDouble(s[3]))<=radius;
				String outPutFiltered; // = FilterBy(outputFile,c,"Location_lat"+lat+"_longt"+longt+"_radius"+radius);
			//	System.out.println("Filtered file ready: "+outPutFiltered);
				break;
			case 2: //not working
				System.out.println("Enter beginng year");
				int begYear = sc.nextInt();
				System.out.println("Enter beginng month");
				int begMonth = sc.nextInt();
				System.out.println("Enter beginng day");
				int begDay = sc.nextInt();
				System.out.println("Enter beginng hour");
				int begHour = sc.nextInt();
				System.out.println("Enter beginng minute");
				int begMin = sc.nextInt();
				System.out.println("Enter beginng second");
				int begSec = sc.nextInt();
				Date begDate = new Date(begYear, begMonth, begDay, begHour, begMin, begSec);
				System.out.println("Enter end year");
				int endYear = sc.nextInt();
				System.out.println("Enter end month");
				int endMonth = sc.nextInt();
				System.out.println("Enter end day");
				int endDay = sc.nextInt();
				System.out.println("Enter end hour");
				int endHour = sc.nextInt();
				System.out.println("Enter end minute");
				int endMin = sc.nextInt();
				System.out.println("Enter end second");
				int endSec = sc.nextInt();
				Date endDate = new Date(endYear, endMonth, endDay, endHour, endMin, endSec);
				//			01-01-1970  2:00:00 AM
				//			05-11-2017  11:21:47 PM
				Date check;
//				Condition c2 = s->begDate.compareTo(
//						new Date(/*year:*/Integer.parseInt(s[0].split(" ")[0].split("-")[2]),
//								/*month:*/Integer.parseInt(s[0].split(" ")[0].split("-")[1]),
//								/*day:*/Integer.parseInt(s[0].split(" ")[0].split("-")[0]),
//								/*hour:*/		Integer.parseInt(s[0].split(" ")[1].split(":")[0]),
//								/*minutes:*/Integer.parseInt(s[0].split(" ")[1].split(":")[1]),
//								/*seconds:*/Integer.parseInt(s[0].split(" ")[1].split(":")[2]))) <= 0 //before or same date
//								&&
//								endDate.compareTo(
//										new Date(/*year:*/Integer.parseInt(s[0].split(" ")[0].split("-")[2]),
//												/*month:*/Integer.parseInt(s[0].split(" ")[0].split("-")[1]),
//												/*day:*/Integer.parseInt(s[0].split(" ")[0].split("-")[0]),
//												/*hour:*/		Integer.parseInt(s[0].split(" ")[1].split(":")[0]),
//												/*minutes:*/Integer.parseInt(s[0].split(" ")[1].split(":")[1]),
//												/*seconds:*/Integer.parseInt(s[0].split(" ")[1].split(":")[2]))) >= 0; //before or same date
//												String outPutFiltered2 = FilterBy(outputFile,c2,"Time_BeginingTime__EndTime");	//fix time show
//												System.out.println("Filtered file ready: "+outPutFiltered2);
												break;

			case 3:
				System.out.println("Enter ID");
				String id = sc.next();
//				Condition c3 = s->s[1].equals(id);
//				String outPutFiltered3 = FilterBy(outputFile,c3,"ID_"+id);
//				System.out.println("KML file is at "+outPutFiltered3);
				break;
			default: break;
			}			
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Path of wiggleWifi output folder: ");
		String reader = sc.nextLine();
		File wigleOutputFolder = new File(reader);
		System.out.println("Path to save output file");
		reader = sc.nextLine()+".csv";
		File csvOutputFile= new File(reader);
		createCSV(wigleOutputFolder,csvOutputFile);	
		filterData(sc, csvOutputFile);
		System.out.println("test_tal");
	}
}

