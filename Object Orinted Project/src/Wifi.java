
/**
 * This class represents a single Wifi scan.
 * it implements Comparable in a way that will sort by signal strength
 * @author Daniel
 *
 */
public class Wifi implements Comparable<Wifi> {

	private String _SSID;
	private String _MAC;
	private int _freq;
	private int _signal;
	
	//Constructor
	public Wifi(String _SSID, String _MAC, int _freq, int _signal) {
		this._SSID = new String(_SSID);
		this._MAC = new String(_MAC);
		this._freq = _freq;
		this._signal = _signal;
	}
	
	@Override
	public int compareTo(Wifi otherLine) {
		if (this._signal <= otherLine._signal){
			if (this._signal < otherLine._signal)
				return 1;
			return 0;
		}
		return -1;
	}

    //getters
	public String get_SSID() {
		return _SSID;
	}

	public String get_MAC() {
		return _MAC;
	}

	public int get_freq() {
		return _freq;
	}

	public int get_signal() {
		return _signal;
	}

	public boolean equals(Wifi obj) {
		return obj.get_freq()==_freq&&obj.get_MAC().equals(_MAC)&&obj.get_signal()==_signal&&obj.get_SSID().equals(_SSID);
	}

	//toString method
	@Override
	public String toString() {
		return "SSID=" + _SSID + ", MAC=" + _MAC + ", freq=" + _freq + ", signal=" + _signal;
	}


}
