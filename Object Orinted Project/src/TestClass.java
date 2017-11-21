import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TestClass {

	public static void main(String[] args) {

		//test of new objects

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
				Condition c = s->s.get_id().equals("sagit");
				Records r2 = r.filter(c);
				r2.toKml(new File("test2.kml"));
		
//		Wifi w = new Wifi("ssid","mac",23,-37);
//		ArrayList<Wifi> l = new ArrayList<>();
//		l.add(w);
//		SingleRecord t = new SingleRecord("id", l,"2017-11-06 18:42:34", 23, 32, 750);
//		System.out.println(t.get_date().getTime());

	}

}
