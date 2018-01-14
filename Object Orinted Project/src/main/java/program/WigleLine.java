package program;
/**
 * This class represents a a specific Wifi network scan as captured by the WiggleWifi app
 * @author Tal
 *
 */

public class WigleLine  implements Comparable<WigleLine> {

	private String _mac, _ssid, _auth, _time, _type, _id;
	private int _channel, _rssi;
	private double _lat, _lon, _alt, _meters;

	//Constructors
	public WigleLine(String mac,String ssid,String auth,String time, String channel, String rssi, String lat, String lon, String alt,String meters, String type, String id) {
		_mac=mac;
		_ssid=ssid;
		_auth=auth;
		_time=time;
		_type=type;
		_channel=Integer.parseInt(channel);
		_rssi=Integer.parseInt(rssi);
		_lat=Double.parseDouble(lat);
		_lon=Double.parseDouble(lon);
		_alt=Double.parseDouble(alt);
		_meters=Double.parseDouble(meters);
		_id=id;
	}

	public WigleLine(String line, String deviceID) {
		String[] data = line.split(",");
		_mac=data[0];
		_ssid=data[1];
		_auth=data[2];
		_time=data[3];
		_type=data[10];
		_channel=Integer.parseInt(data[4]);
		_rssi=Integer.parseInt(data[5]);
		_lat=Double.parseDouble(data[6]);
		_lon=Double.parseDouble(data[7]);
		_alt=Double.parseDouble(data[8]);
		_meters=Double.parseDouble(data[9]);
		_id=deviceID;
		
	}

	@Override
	public int compareTo(WigleLine otherLine) {
		if (this._rssi <= otherLine._rssi){
			if (this._rssi < otherLine._rssi)
				return 1;
			return 0;
		}
		return -1;
	}
	
	//Getters
	public String get_mac() {
		return _mac;
	}

	public String get_ssid() {
		return _ssid;
	}

	public String get_auth() {
		return _auth;
	}

	public String get_time() {
		return _time;
	}

	public String get_type() {
		return _type;
	}

	public String get_id() {
		return _id;
	}

	public int get_channel() {
		return _channel;
	}

	public int get_rssi() {
		return _rssi;
	}

	public double get_lat() {
		return _lat;
	}

	public double get_lon() {
		return _lon;
	}

	public double get_alt() {
		return _alt;
	}

	public double get_meters() {
		return _meters;
	}

	
}
