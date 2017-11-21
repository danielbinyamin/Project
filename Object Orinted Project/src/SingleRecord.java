import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SingleRecord {
	private Point2D _location;
	private double _altitude;
	private Calendar _date;
	private ArrayList<Wifi> _WifiList;
	private String _id;
	
	//TODO: Fix date
	public SingleRecord(String id, ArrayList<Wifi> wifiList, String dateAndTime, double lon, double lat, double altitude) {
		this._id = new String(id);
		this._WifiList = new ArrayList<>(wifiList);
		Collections.sort(this._WifiList);
		this._location = new Point2D.Double(lat,lon);
		this._altitude = altitude;
		
		String[] mainarr = dateAndTime.split(" ");
		String[] date = mainarr[0].split("-");
		String[] time = mainarr[1].split(":");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[2]);
		int hour = Integer.parseInt(time[0]);
		int minutes = Integer.parseInt(time[1]);
		int sec = Integer.parseInt(time[2]);
		this._date = Calendar.getInstance();
		_date.set(year, month-1,day,hour,minutes,sec);
	}
	 
	

	
	
	//getters
	public Point2D get_location() {
		return _location;
	}

	public double get_altitude() {
		return _altitude;
	}

	public Calendar get_date() {
		return _date;
	}

	public ArrayList<Wifi> get_WifiList() {
		return _WifiList;
	}

	public String get_id() {
		return _id;
	}
	
	//toString method
	@Override
	public String toString() {
		return "SingleRecord [_location=" + _location + ", _altitude=" + _altitude + ", _date=" + _date.getTime() + ", _WifiList="
				+ _WifiList + ", _id=" + _id + "]";
	}
	
	


}
