package junits;

import static org.junit.Assert.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import org.junit.Test;
import program.*;

import program.*;
/**
 * This class is a Junit test class for the Records class
 * @author Tal
 *
 */
public class RecordsTest {

	@Test
	public void testConstructor() {
		Wifi w1 = new Wifi("_SSID1", "_MAC1", 1, -1);
		Wifi w2 = new Wifi("_SSID2", "_MAC2", 2, -2);
		Wifi w3 = new Wifi("_SSID3", "_MAC3", 3, -3);
		Wifi w4 = new Wifi("_SSID4", "_MAC4", 4, -4);
		Wifi w5 = new Wifi("_SSID5", "_MAC5", 5, -5);
		Wifi w6 = new Wifi("_SSID6", "_MAC6", 6, -6);
		Wifi w7 = new Wifi("_SSID7", "_MAC7", 7, -7);
		Wifi w8 = new Wifi("_SSID8", "_MAC8", 8, -8);
		Wifi w9 = new Wifi("_SSID9", "_MAC9", 9, -9);
		ArrayList<Wifi> wifiL1 = new ArrayList<>();
		ArrayList<Wifi> wifiL2 = new ArrayList<>();
		ArrayList<Wifi> wifiL3 = new ArrayList<>();
		wifiL1.add(w1);
		wifiL1.add(w2);
		wifiL1.add(w3);
		wifiL2.add(w4);
		wifiL2.add(w5);
		wifiL2.add(w6);
		wifiL3.add(w7);
		wifiL3.add(w8);
		wifiL3.add(w9);
		SingleRecord sr1 = new SingleRecord("id1",wifiL1, "01-01-01 01:01:01",1.0,1.0,1.0);
		SingleRecord sr2 = new SingleRecord("id2",wifiL2, "02-02-02 02:02:02",2.0,2.0,2.0);
		SingleRecord sr3 = new SingleRecord("id3",wifiL3, "03-03-03 03:03:03",3.0,3.0,3.0);
		ArrayList<SingleRecord> srL = new ArrayList<>();
		srL.add(sr1);
		srL.add(sr2);
		srL.add(sr3);
		Records r = new Records(srL);
		Wifi test = r.getSingleRecordsList().get(1).get_WifiList().get(1);
		assertTrue(test.compareTo(new Wifi("_SSID1", "_MAC1", 1, -1))==1);
	}

	@Test
	public void testFilter() {
		Condition c = s->s.get_id().equals("rightID");
		Wifi w1 = new Wifi("_SSID1", "_MAC1", 1, -1);
		Wifi w2 = new Wifi("_SSID2", "_MAC2", 2, -2);
		Wifi w3 = new Wifi("_SSID3", "_MAC3", 3, -3);
		ArrayList<Wifi> wifiL1 = new ArrayList<>();
		wifiL1.add(w1);
		wifiL1.add(w2);
		wifiL1.add(w3);
		SingleRecord sr1 = new SingleRecord("rightID",wifiL1, "01-01-01 01:01:01",1.0,1.0,1.0);
		assertTrue(c.test(sr1));
	}

	@Test
	public void testCSV2Records() {
		Records r = new Records();
		File file = new File("C:/wigle/testEx1/test");
		r.CSV2Records(file);
		ArrayList<SingleRecord> srL = r.getSingleRecordsList();
		String srlID = srL.get(0).get_id();
		String wifiSSID = srL.get(0).get_WifiList().get(0).get_SSID();
		assertTrue(srlID.equals("TESTid") && wifiSSID.equals("SSIDtest"));
	}

	@Test
	public void testToCSV() {
		Records r = new Records();
		File file = new File("C:/wigle/testEx1/test");//hardcoded path. need to fix
		r.CSV2Records(file);
		File file2 = new File("C:/wigle/testEx1/test/output.csv");//hardcoded path. need to fix
		r.toCSV(file2);
		try{
			BufferedReader br = new BufferedReader(new FileReader(file2));
			br.readLine();
			String line = br.readLine();
			assertTrue(line.split(",")[1].equals("TESTid"));
		}
		catch(Exception e){
			assertTrue(false);	//don't know how to assert on exception
		}
	}
	
	@Test
	public void testToKml() {
		Records r = new Records();
		File file = new File("C:/wigle/testEx1/test");//hardcoded path. need to fix
		r.CSV2Records(file);
		File file2 = new File("C:/wigle/testEx1/test/output.csv");//hardcoded path. need to fix
		r.toCSV(file2);
		File file3 = new File("C:/wigle/testEx1/test/outputKML.kml");//hardcoded path. need to fix
		r.toKml(file3);
		//here we need to check if KML is well-formed xml.
	}


}
