import java.awt.geom.Point2D;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * This class represents the implementation of the finding user location algorithm.
 * @author Tal
 */
public class findUserAlgo{
	//globals
	public static final int minDiff = 3;
	public static final int diffNoSignal = 100;
	public static final int noSignal = -120;
	public static final int norm = 10000;
	public static final double sigDiff = 0.4;
	public static final int power = 2;
	public static final int numOfDataLinesToCheck = 3;
	//members
	/**
	 * This private class represents the data needed for the algorithm for every wifi network.
	 */
	private class dataPerWifi{
		//members
		private int _signal, _difference;
		private double _weight;
		
		//constructors
		public dataPerWifi(int nodeSignal, int mainSignal){
			_signal = nodeSignal;
			if (_signal <= noSignal){
				_difference = diffNoSignal;
			}
			else{
				int realDiff = Math.abs(_signal - mainSignal); 
				_difference = Math.max(minDiff, realDiff);
			}
			_weight = norm / (Math.pow(_difference, sigDiff) * Math.pow(mainSignal, power));
		}
		
		//methods
		public double getWeight(){
			return _weight;
		}
	}
	
	/**
	 * This private class represents a single line in the table (like in Boaz's example).
	 */
	private class lineOfData implements Comparable<lineOfData>{
		private SingleRecord _singleRecord;
		private dataPerWifi _mac1, _mac2, _mac3;
		private double _pi, _wLat, _wLon, _wAlt;

		public lineOfData(SingleRecord singleRecord, dataPerWifi mac1, dataPerWifi mac2, dataPerWifi mac3){
			_singleRecord = singleRecord;
			_mac1 = mac1;
			_mac2 = mac2;
			_mac3 = mac3;
			_pi = _mac1.getWeight() * _mac2.getWeight() * _mac3.getWeight();
			_wLat = _pi * _singleRecord.get_location().getX();
			_wLon = _pi * _singleRecord.get_location().getY();
			_wAlt = _pi * _singleRecord.get_altitude();
		}

		public double getPi(){
			return _pi;
		}

		@Override
		public int compareTo(lineOfData other) {
			if (_pi >= other.getPi()){
				if (_pi > other.getPi()){
					return 1;
				}
				return 0;
			}
			return -1;
		}
	}
	private ArrayList<lineOfData> _linesOfData;
	double _wLatSum, _wLonSum, _wAltSum, _weight;

	//constructors
	public findUserAlgo(Records records, String mac1, int Signal1, String mac2, int Signal2, String mac3, int Signal3) throws Exception{
		_linesOfData = new ArrayList<>();
		for (SingleRecord singleRecord : records.getSingleRecordsList()) {
			boolean mac1IsFound, mac2IsFound, mac3IsFound;
			mac1IsFound = mac2IsFound = mac3IsFound = false;
			dataPerWifi firstMac, secondMac, thirdMac;
			firstMac = secondMac = thirdMac = null;
			ArrayList<Wifi> wifis = singleRecord.get_WifiList();
			for (int wifiIndex=0; wifiIndex < wifis.size(); wifiIndex++){
				if (!mac1IsFound){	//if mac1 wasn't found yet
					if (wifis.get(wifiIndex).get_MAC().equals(mac1)){
						mac1IsFound = true;
						firstMac = new dataPerWifi(wifis.get(wifiIndex).get_signal(), Signal1);
						if (wifiIndex+1 >= wifis.size()){
							break;
						}
						else{
							continue;
						}
					}
				}	
				if (!mac2IsFound){	//if mac2 wasn't found yet
					if (wifis.get(wifiIndex).get_MAC().equals(mac2)){
						mac2IsFound = true;
						secondMac = new dataPerWifi(wifis.get(wifiIndex).get_signal(), Signal2);
						if (wifiIndex+1 >= wifis.size()){
							break;
						}
						else{
							continue;
						}
					}
				}	
				if (!mac3IsFound){	//if mac3 wasn't found yet
					if (wifis.get(wifiIndex).get_MAC().equals(mac3)){
						mac3IsFound = true;
						thirdMac = new dataPerWifi(wifis.get(wifiIndex).get_signal(), Signal3);
						if (wifiIndex+1 >= wifis.size()){
							break;
						}
						else{
							continue;
						}
					}
				}
				if (mac1IsFound && mac2IsFound && mac3IsFound){
					break;
				}
			}
			if (mac1IsFound || mac2IsFound || mac3IsFound){
				if (!mac1IsFound){
					firstMac = new dataPerWifi(noSignal, Signal1);
				}
				if (!mac2IsFound){
					secondMac = new dataPerWifi(noSignal, Signal2);
				}
				if (!mac3IsFound){
					thirdMac = new dataPerWifi(noSignal, Signal3);
				}
				lineOfData tempLineOfData = new lineOfData(singleRecord, firstMac, secondMac, thirdMac);
				_linesOfData.add(tempLineOfData);
			}
		}
		if (_linesOfData.size() > 0){
			Collections.sort(_linesOfData);
			Collections.reverse(_linesOfData);
		}
		else{
			throw(new Exception("\nThis MAC is has never been recorded.\nIt is not possible to tell your location."));
		}
	}
	//methods
	public Point2D getLocation(){
		_wLatSum = _wLonSum = _wAltSum = _weight = 0;
		int finalNumOfDataLinesToCheck = Math.min(numOfDataLinesToCheck, _linesOfData.size());
		for (int lineOfData = 0; lineOfData < finalNumOfDataLinesToCheck; lineOfData++) {
			_wLatSum = _wLatSum + _linesOfData.get(lineOfData)._wLat;
			_wLonSum = _wLonSum + _linesOfData.get(lineOfData)._wLon;
			_wAltSum = _wAltSum + _linesOfData.get(lineOfData)._wAlt;
			_weight = _weight + _linesOfData.get(lineOfData)._pi;
		}
		System.out.println("lat="+_wLatSum / _weight+" lon="+_wLonSum / _weight);//***
		return (new Point2D.Double(_wLatSum / _weight, _wLonSum / _weight));
	}

	public double getAlt(){
		return _wAltSum / _weight;
	}
}

