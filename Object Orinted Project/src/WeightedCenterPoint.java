import java.awt.geom.Point2D;
import java.util.ArrayList;

public class WeightedCenterPoint{

	private ArrayList<Double> _weight, _wLat, _wLon, _wAlt;
	double _wLatSum, _wLonSum, _wAltSum;
	
	public WeightedCenterPoint(Records records, String mac){
		fillData(records, mac);
		calcWeights();
	}
	
	public void fillData (Records records, String mac){
		_weight = new ArrayList<>();
		_wLat = new ArrayList<>();
		_wLon = new ArrayList<>();
		_wAlt = new ArrayList<>();
		ArrayList<SingleRecord> srList = records.getSingleRecordsList();
		for (SingleRecord singleRecord : srList) {
			ArrayList<Wifi> currWifis = singleRecord.get_WifiList();
			Point2D location = singleRecord.get_location();
			for (Wifi wifi : currWifis) {
				if (wifi.get_MAC().equals(mac)){
					double weight = 1 / Math.pow((wifi.get_signal()) , 2);
					double lat = location.getX();
					double lon = location.getY();
					double alt = singleRecord.get_altitude();
					_weight.add(weight);
					_wLat.add(weight*lat);
					_wLon.add(weight*lon);
					_wAlt.add(weight*alt);
				}
			}
		}
	}
	
	public void calcWeights(){
		double weightSum=0;
		_wLatSum = _wLonSum = _wAltSum = 0;
		int numOfLocations = _weight.size();
		for (int locationIndex = 0; locationIndex < numOfLocations; locationIndex++) {
			weightSum = weightSum + _weight.get(locationIndex);
			_wLatSum = _wLatSum +  _wLat.get(locationIndex);
			_wLonSum = _wLonSum+  _wLon.get(locationIndex);
			_wAltSum = _wAltSum +  _wAlt.get(locationIndex);
		}
		_wLatSum = _wLatSum / weightSum;
		_wLonSum = _wLonSum / weightSum;
		_wAltSum = _wAltSum / weightSum;
	}
	
	public Point2D getLocation(){
		return (new Point2D.Double(_wLatSum,_wLonSum));
	}
	
	public double getAlt(){
		return _wAltSum;
	}

	
}

