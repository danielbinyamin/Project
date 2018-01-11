package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


/**
 * Main class of the project. Represents a set of records scanned. 
 * Each record in the list represents a specific time & location.
 * @author Daniel & Tal
 *
 */
public class Records  {

	private ArrayList<SingleRecord> _records;

	//Constructors
	public Records(ArrayList<SingleRecord> records) {
		_records = new ArrayList<SingleRecord>(records);
	}

	public Records(){
		_records = new ArrayList<SingleRecord>();
	}

	public Records(Records records) {
		_records = new ArrayList<>(records.getSingleRecordsList());
	}


	//NOT USED IN GUI
	/**
	 * This method loads a combined CSV into the records data structure
	 * NOTE!! not used in GUI
	 * @param WiggleWifi directory
	 */
	public void loadRecordsFromFile(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			br.readLine();
			line = br.readLine();
			while (line != null) {
				String[] details = line.split(",");
				ArrayList<Wifi> wifiList = new ArrayList<>();
				int numOfNetworks = Integer.parseInt(details[5]);
				for (int wifiIndex = 1; wifiIndex <= numOfNetworks; wifiIndex++) {				
					int index = 6+(wifiIndex-1)*4;
					if (!(details[index]==null && details[index+1]==null)) {
						String ssid;
						if (details[index]==null) ssid = new String("nullSSID");
						else ssid = details[index];
						index++;
						String mac;
						if (details[index]==null) mac = new String("nullMAC");
						else mac = details[index];
						index++;
						int freq = Integer.parseInt(details[index++]);
						int signal = Integer.parseInt(details[index]);
						Wifi tempWifi = new Wifi(ssid, mac, freq, signal);
						wifiList.add(tempWifi);
					}
				}
				String dateAndTime = details[0];//convert to date.***
				String id = details[1];
				double lat,lon,altitude;
				if(details[2].equals("?")){  lat =-1;}
				else { lat =Double.parseDouble(details[2]);} 
				if(details[3].equals("?")){ lon =-1;}
				else { lon = Double.parseDouble(details[3]);}
				if(details[4].equals("?")){ altitude =-1;}
				else {altitude = Double.parseDouble(details[4]);}
				SingleRecord tempSR = new SingleRecord(id, wifiList, dateAndTime, lon, lat, altitude);
				_records.add(tempSR);
				try {
					line = br.readLine();
				} catch (IOException e) {
					System.out.println("Error reading loaded file. 3");
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error loading data from combined csv. "+e);
		}
	}

	/**
	 * New version of old method. This method loads Records from a given combined csv
	 * @param path
	 */
	public void loadRecordsFromFilev2(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			br.readLine();
			line = br.readLine();
			while (line != null) {
				String[] details = line.split(",");
				//parsing string into calendar
				String dateString = details[0]; 
				Calendar date = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
				date.setTime(sdf.parse(dateString));

				String id = details[1];
				double lat = Double.parseDouble(details[2]);
				double lon = Double.parseDouble(details[3]);
				double alt = Double.parseDouble(details[4]);

				ArrayList<Wifi> wifiList = new ArrayList<>();
				String ssid,mac;
				int freq,signal;
				for (int i = 6; i < details.length; i=i+4) {
					ssid = details[i];
					mac = details[i+1];
					freq = Integer.parseInt(details[i+2]);
					signal = Integer.parseInt(details[i+3]);
					Wifi currentWifi = new Wifi(ssid,mac,freq,signal);
					wifiList.add(currentWifi);
				}
				SingleRecord currentRecord = new SingleRecord(id, wifiList, date, lon, lat, alt);
				_records.add(currentRecord);
				try { line = br.readLine(); }
				catch (IOException e) { System.out.println("Error reading line while loading combined csv\n"+e);			
				}
			}
			br.close();

		}
		catch (Exception e) {
			System.out.println("Error loading combined CSV\n"+e);
		}
	}

	/**
	 * This method loads Records from a given combined Database
	 * @param url
	 * @param username
	 * @param password
	 * @throws Exception 
	 */
	public Connection loadRecordsFromDB(String url, String table, String username, String password) throws Exception {
		//init. connection to server
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		}
		catch (SQLException error) {
			throw new IllegalStateException("Cannot connect the database!", error);
		}
		//read data from table
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM" +  table);
		//move from line to line
		try {
			while (rs.next()) {
				//date
				String dateString = rs.getString("date"); 
				Calendar date = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
				date.setTime(sdf.parse(dateString));
				//id
				String id = rs.getString("id");
				//location
				double lat = rs.getDouble("lat");
				double lon = rs.getDouble("lon");
				double alt = rs.getDouble("alt");
				//wifi-list
				ArrayList<Wifi> wifiList = new ArrayList<>();
				String ssid, mac;
				int freq, signal;
				for (int wifiIndex = 1; wifiIndex <= 10; wifiIndex = wifiIndex + 4) {
					ssid = rs.getString("ssid" + wifiIndex);
					mac = rs.getString("MAC" + wifiIndex);
					if (ssid.equals("") && mac.equals("")) {
						break;	//means that if MAC and SSID values are empty, there are no more wifi networks.
					}
					freq = rs.getInt("freq" + wifiIndex);
					signal = rs.getInt("signal" + wifiIndex);
					Wifi currentWifi = new Wifi(ssid,mac,freq,signal);
					wifiList.add(currentWifi);
				}
				//create singleRecord and add it to list
				SingleRecord currentRecord = new SingleRecord(id, wifiList, date, lon, lat, alt);
				_records.add(currentRecord);
			}
			return connection;
		}
		catch (Exception e) {
			throw new Exception("Error reading from the database!");
		}
	}

	/**
	 * This method loads Records from a given combined Database
	 * @param url
	 * @param username
	 * @param password
	 * @throws Exception 
	 */
	public void reloadRecordsFromDB(Connection connection, String table) throws Exception{
		//read data from table
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM" +  table);
		//move from line to line
		try {
			while (rs.next()) {
				//date
				String dateString = rs.getString("date"); 
				Calendar date = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
				date.setTime(sdf.parse(dateString));
				//id
				String id = rs.getString("id");
				//location
				double lat = rs.getDouble("lat");
				double lon = rs.getDouble("lon");
				double alt = rs.getDouble("alt");
				//wifi-list
				ArrayList<Wifi> wifiList = new ArrayList<>();
				String ssid, mac;
				int freq, signal;
				for (int wifiIndex = 1; wifiIndex <= 10; wifiIndex = wifiIndex + 4) {
					ssid = rs.getString("ssid" + wifiIndex);
					mac = rs.getString("MAC" + wifiIndex);
					if (ssid.equals("") && mac.equals("")) {
						break;	//means that if MAC and SSID values are empty, there are no more wifi networks.
					}
					freq = rs.getInt("freq" + wifiIndex);
					signal = rs.getInt("signal" + wifiIndex);
					Wifi currentWifi = new Wifi(ssid,mac,freq,signal);
					wifiList.add(currentWifi);
				}
				//create singleRecord and add it to list
				SingleRecord currentRecord = new SingleRecord(id, wifiList, date, lon, lat, alt);
				_records.add(currentRecord);
			}
		}
		catch (Exception e) {
			throw new Exception("Error reloading from the database!");
		}
	}

/**
 * This function adds external Records to current records. It will then sort the combined Records by date
 * @param records
 */
public void addRecords(Records records) {
	for (SingleRecord singleRecord : records.getSingleRecordsList()) {
		_records.add(singleRecord);
	}
	Collections.sort(_records);
}

/**
 * This method turn a directory with WiggleWifi csv's to a Records object.
 * @param WiggleWifi directory
 */	
public void CSV2Records(File wigleOutputFolder) {
	try {
		File[] listOfFiles = wigleOutputFolder.listFiles();
		for (File file : listOfFiles) {
			ArrayList<ArrayList<WigleLine>> PITarr = new ArrayList<>();	//PITarr = array of arrays. each inner array has scans from the same time.
			if (!file.isDirectory() && file.getName().contains("WigleWifi")){ 	//if you find a valid WiglleWIFI csv file...
				int time = 0;	//time = number of different "time" string. 
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				String[] headerOfInputCSV = line.split(",");
				String deviceID = headerOfInputCSV[4].substring(7);		//same device ID to all lines in a file.
				line = br.readLine();	//to read the first line of data.
				line = br.readLine();
				while (line != null){	//for each line, convert it to WigleLine and add it to an array with the same time.
					if (!line.split(",")[3].contains("70")){
						WigleLine newLine = new WigleLine(line,deviceID);
						if (PITarr.size()==0 || PITarr.size()<time){	//if it's the first line in file, or this line was written after the last line
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
					}
					line = br.readLine();	//continue to the next line
				}
				br.close();
			}// all lines from the file are now sorted by "time" in "PITarr". now, for each cell in PITarr


			/* Goes over PITarr and add each araylist<WiggleLine> as a SingleRecord 
			 * At the end of the main loop, adds the SingleRecord created to arg 'records'
			 */
			for (ArrayList<WigleLine> arrayList : PITarr) {
				ArrayList<Wifi> Wifilist = new ArrayList<Wifi>();
				for (WigleLine WigleLine : arrayList) {
					String ssid = WigleLine.get_ssid(); 
					String mac = WigleLine.get_mac();
					int freq = WigleLine.get_channel();
					int signal = WigleLine.get_rssi();
					Wifi singleWifi = new Wifi(ssid,mac,freq,signal);
					Wifilist.add(singleWifi);
				}
				double lat = arrayList.get(0).get_lat();
				double lon = arrayList.get(0).get_lon();
				double alt = arrayList.get(0).get_alt();
				String date = arrayList.get(0).get_time();
				String id =  arrayList.get(0).get_id();
				try{
					SingleRecord currentRec = new SingleRecord(id, Wifilist,date,lon,lat,alt);
					_records.add(currentRec);
				}
				catch (Exception e){System.out.println("EXCEPTION: "+e);}
			}
		}
	}
	catch (Exception e) {
		System.out.println("Error at CSV2Records. Exception:\n"+e);
	}
}

/**
 * This method creates a CSV file from Records object. It exports a Records object to a CSV file.
 * @param output directory to which the file will be created to.
 * @author Daniel & Tal
 */
public void toCSV(File output) {
	try {
		//add headers to CSV file
		FileWriter writer = new FileWriter(output + "/output.csv");//***\\output.csv was changed to /output.csv
		PrintWriter outs = new PrintWriter(writer);
		outs.print("Time,id,Lat,Lon,Alt,Num_Of_Networks");
		for (int i = 1; i < 11; i++) {
			outs.print(",SSID"+i+",MAC"+i+",Frequncy"+i+",Signal"+i);
		}
		//end headers

		outs.println();	//one line down in the CSV output file.
		for (SingleRecord singleRecord : _records) {
			Date time = singleRecord.get_date().getTime();
			String id = singleRecord.get_id();
			double lan = singleRecord.get_location().getX();
			double lot = singleRecord.get_location().getY();
			double alt = singleRecord.get_altitude();
			int numOfNetworks = Math.min(10, singleRecord.get_WifiList().size());
			outs.print(time+","+id+","+lan+","+lot+","+alt+","+numOfNetworks+",");
			int networkIndex = 0;
			for (Wifi network : singleRecord.get_WifiList()) {	//fill rest of line with up to 10 WIFIs
				outs.print(network.get_SSID()+","+network.get_MAC()+","+network.get_freq()+","+network.get_signal()+",");
				networkIndex++;
				if (networkIndex==10)
					break;
			}
			outs.println();
		}//all files are now in CSV file.
		outs.close();
		writer.close();
	}
	catch (Exception e) {
		System.out.println("Error writing from Records to CSV. Exception:\n: "+e);
	}
}
/**
 * This method creates a KML file from Records object. It exports a Records object to a KML file.
 * @param output directory to which the file will be created to.
 * @author Daniel & Tal
 */
public void toKml(File output){
	// The all encapsulating kml element.
	Kml kml = new Kml();
	Document document = kml.createAndSetDocument();
	int wifiCounter=1;
	for (SingleRecord singleRecord : _records) {
		double lat = singleRecord.get_location().getX();
		double lon = singleRecord.get_location().getY();
		Date dateType = singleRecord.get_date().getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String srDate = dateFormat.format(dateType)+"T"+timeFormat.format(dateType)+"Z";
		String description="";
		for (Wifi wifi : singleRecord.get_WifiList()) {
			description+="Wifi #"+wifiCounter+"\n"+wifi.toString()+"\n";
		}
		String placemarkName = singleRecord.get_id();
		// Create <Placemark> and set values.	
		Placemark placeMark = document.createAndAddPlacemark();
		placeMark.setName(placemarkName);
		placeMark.setVisibility(true);
		placeMark.setOpen(false);
		placeMark.setDescription(description);
		placeMark.createAndSetTimeStamp().setWhen(srDate);
		placeMark.createAndSetPoint().addToCoordinates(lon,lat);
		document.addToFeature(placeMark);
		kml.setFeature(document);	// <-- placemark is registered at kml ownership.
		wifiCounter++;
	}
	try { kml.marshal(output); }			
	catch (Exception e) {
		System.out.println("Error at kml marshal. Exception: \n"+e);
	}
}

public ArrayList<SingleRecord> getSingleRecordsList(){
	return _records;
}

//old version. NOT USED IN GUI
/**
 * This method returns a filtered Records object by the c Condition
 * @param c (Condition).
 * @return Filtered Records object.
 * @author Daniel & Tal
 */
public Records filter(Condition c) {
	ArrayList<SingleRecord> filterd = new ArrayList<SingleRecord>();
	for (SingleRecord singleRecord : _records) {
		if(c.test(singleRecord))
			filterd.add(singleRecord);	
	}
	return new Records(filterd);
}

/**
 * This is the newer filter method using the Filter class
 * @param f
 * @return
 */
public Records filterv2(Filter f) {
	ArrayList<SingleRecord> filterd = new ArrayList<SingleRecord>();
	for (SingleRecord singleRecord : _records) {
		if(f.checkFilterOverSingleRecord(singleRecord))
			filterd.add(singleRecord);
	}
	return new Records(filterd);
}

public boolean isEmpty() {
	return _records.size()==0;
}

/**
 * This function returns all the different MAC's in the Records as a list.
 * @return
 */
public ArrayList<String> getListOfDiffRouters() {
	ArrayList<String> macList = new ArrayList<>();
	String mac;
	for (SingleRecord currentRecord : _records) {
		for (Wifi currentWifi : currentRecord.get_WifiList()) {
			mac = currentWifi.get_MAC();
			if(!macList.contains(mac))
				macList.add(mac);
		}
	}
	return macList;
}

/**
 * This method checks if MAC exists in data base
 * @param mac
 * @return
 */
public boolean doesMacExist(String mac) {
	for (SingleRecord currentRecord : _records) 
		for (Wifi currentWifi : currentRecord.get_WifiList())
			if(mac.equals(currentWifi.get_MAC()))
				return true;
	return false;
}

/**
 * This method return number of diffrent routers
 * @return
 */
public int numOfDiffRouter() {
	return this.getListOfDiffRouters().size();
}

public int size() {
	return _records.size();
}

}