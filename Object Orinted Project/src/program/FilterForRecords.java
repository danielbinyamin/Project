package program;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterForRecords implements  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -167545388393389747L;
	//members
	private Filter _f;
	private Records _records;
	private String toString;

	//Constructors
	
	public FilterForRecords(FilterForRecords other) {
		_f = new Filter(other.get_filters());
		_records = new Records(other.get_records());
		toString = other.toString();
	}
	
	public FilterForRecords() {
		_f = new Filter();
		_records = new Records();
		toString = "No_filters";
	}
	
	//construct from Records
	public FilterForRecords(Records records) {
		_records = new Records(records);
		_f = new Filter();
		toString = "No_filters";
	}

	//Construct from Deserialized external object in path
	public FilterForRecords(String path) {
		FilterForRecords temp = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			temp = (FilterForRecords) in.readObject();
			in.close();
			fileIn.close();
//			_filters = new ArrayList<Condition>(temp.get_filters());
			_f = new Filter(temp.get_filters());
			_records = new Records(temp.get_records());
			toString = temp.toString();
//			try {
//				_relations = new ArrayList<String>(temp.get_relations());
//			}
//			catch (NullPointerException e) {
//				_relations = new ArrayList<String>();
//			}
		} catch (IOException i) {
			System.out.println("Error loading Filter\n");
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Filter not found\n");
			c.printStackTrace();
			return;
		}
	}
	
	public void createDateFilter(Calendar beginDate, Calendar endDate, boolean not) {
		Condition timeCondition;
		//check if NOT
		if (not) {
			timeCondition = currSingleRec->!(currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0);
			toString = "FilteredByDate_(NOT)"+beginDate.getTime()+"-"+endDate.getTime();
		} else {
			timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;	
			toString = "FilteredByDate_"+beginDate.getTime()+"-"+endDate.getTime();
		}	
		
		_f = new Filter(timeCondition);	
		_records = _records.filterv2(_f);
		
	}

	public void createLocationFilter(double lat, double lon, double radius, boolean not) {
		Condition locationCondition;
		Point2D locationPick = new Point2D.Double(lat,lon);
		//check if NOT
		if (not) {
			locationCondition = currSingleRec->!(locationPick.distance(currSingleRec.get_location())<=radius);
			toString = "FilteredByLocation_(NOT)("+lat+" , "+lon+")"+"Radius_"+radius;
		} else {
			locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
			toString = "FilteredByLocation_("+lat+" , "+lon+")"+"Radius_"+radius;
		}
		
		_f = new Filter(locationCondition);	
		_records = _records.filterv2(_f);	
	}
	
	public void createIDFilter(String id, boolean not) {
		Condition idCondition;
		//check NOT
		if (not) {
			idCondition = currSingleRec->!(currSingleRec.get_id().toLowerCase().equals(id.toLowerCase()));
			toString = "FilteredByID_(NOT)"+id;
		} else {
			idCondition = currSingleRec->currSingleRec.get_id().toLowerCase().equals(id.toLowerCase());
			toString = "FilteredByID_"+id;
		}
		
		_f = new Filter(idCondition);	
		_records = _records.filterv2(_f);		
	}

	public void addDateFilter(Calendar beginDate, Calendar endDate, boolean not, String relation) {
		Condition timeCondition;
		//check if NOT
		if (not) {
			timeCondition = currSingleRec->!(currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0);
			toString+=" "+relation+" (NOT)Date_"+beginDate.getTime()+"-"+endDate.getTime();
		} else {
			timeCondition = currSingleRec->currSingleRec.get_date().compareTo(beginDate)>=0 && currSingleRec.get_date().compareTo(endDate)<=0;	
			toString+=" "+relation+" Date_"+beginDate.getTime()+"-"+endDate.getTime();
		}
		
		_f.addFilter(timeCondition, relation);
		_records = _records.filterv2(_f);
	}
	
	public void addLocationFilter(double lat, double lon, double radius, boolean not, String relation) {
		Condition locationCondition;
		Point2D locationPick = new Point2D.Double(lat,lon);
		//check if NOT
		if (not) {
			locationCondition = currSingleRec->!(locationPick.distance(currSingleRec.get_location())<=radius);
			toString+=" "+relation+" (NOT)Location_("+lat+" , "+lon+")"+"Radius_"+radius;

		} else {
			locationCondition = currSingleRec->locationPick.distance(currSingleRec.get_location())<=radius;
			toString+=" "+relation+" "+"Location_("+lat+" , "+lon+")"+"Radius_"+radius;
		}
		
		_f.addFilter(locationCondition, relation);
		_records = _records.filterv2(_f);
	}

	public void addIDFilter(String id, boolean not, String relation) {
		Condition idCondition;
		//check NOT
		if (not) {
			idCondition = currSingleRec->!(currSingleRec.get_id().toLowerCase().equals(id.toLowerCase()));
			toString+=" "+relation+" (NOT)ID_"+id;
		} else {
			idCondition = currSingleRec->currSingleRec.get_id().toLowerCase().equals(id.toLowerCase());
			toString+=" "+relation+" ID_"+id;
		}
		
		_f.addFilter(idCondition, relation);	
		_records = _records.filterv2(_f);	
	}

	public void saveFilterToDisk(String path) throws IOException {
		try {
			FileOutputStream fileOut = new FileOutputStream(path+this.toString()+".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error saving Filter\n");
			e.printStackTrace();
		}
	}

	//Getters
	public Filter get_filters() {
		return _f;
	}

	public Records get_records() {
		return _records;
	}

	public int getNumOfFilters() {
		return _f.get_filters().size();
	}
	
	@Override
	public String toString() {
		return toString;
	}
	

}
