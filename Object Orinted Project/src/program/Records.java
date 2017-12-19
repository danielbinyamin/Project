package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class Records {
	private ArrayList<SingleRecord> _records;

	//Constructors
	public Records(ArrayList<SingleRecord> records) {
		_records = new ArrayList<SingleRecord>(records);
	}

	public Records(){
		_records = new ArrayList<SingleRecord>();
	}

	public Records(String loadFrom){
		_records = loadRecordsFromFile(loadFrom);
	}

	/**
	 * This method turn a directory with WiggleWifi csv's to a Records object.
	 * @param WiggleWifi directory
	 */
	public ArrayList<SingleRecord> loadRecordsFromFile(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.out.println("Error reading loaded file 1.");
			return null;
		}
		String[] pathToSaveOutput = path.split("/");
		pathToSaveOutput[pathToSaveOutput.length-1] = "loadedRecords";
		String saveTo = String.join("", pathToSaveOutput);
		File savedFilePath = new File(saveTo);
		FileWriter writer;
		try {
			writer = new FileWriter(savedFilePath);
		} catch (IOException e1) {
			System.out.println("Error writing to file when loading file.");
			return null;
		}
		ArrayList<SingleRecord> records = new ArrayList<>();
		PrintWriter outs = new PrintWriter(writer);
		outs.print("Time,id,Lat,Lon,Alt,Num_Of_Networks");
		String line;
		try {
			line = br.readLine();
		} catch (IOException e) {
			System.out.println("Error reading loaded file. 2");
			return null;
		}
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
			double lat = Double.parseDouble(details[2]);
			double lon = Double.parseDouble(details[3]);
			double altitude = Double.parseDouble(details[4]);
			SingleRecord tempSR = new SingleRecord(id, wifiList, dateAndTime, lon, lat, altitude);
			records.add(tempSR);
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("Error reading loaded file. 3");
				return null;
			}
		}
		return records;
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

	public boolean isEmpty() {
		return _records.size()==0;
	}

}