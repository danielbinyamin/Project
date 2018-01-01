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
	private String toString;

	//Constructors
	
	public FilterForRecords(FilterForRecords other) {
		_f = new Filter(other.get_filters());
		toString = other.toString();
	}
	
	public FilterForRecords() {
		_f = new Filter();
		toString = "No filter selected";
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
			_f = new Filter(temp.get_filters());
			toString = temp.toString();
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
	}

	public void saveFilterToDisk(String path) throws IOException {
		try {
			FileOutputStream fileOut = new FileOutputStream(path+"\\"+this.toString()+".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error saving Filter\n");
			e.printStackTrace();
		}
	}

	public void cleanFilter() {
		_f = new Filter();
		toString = "No filter selected";
	}
	
	//Getters
	public Filter get_filters() {
		return _f;
	}

	public int getNumOfFilters() {
		return _f.get_filters().size();
	}
	
	@Override
	public String toString() {
		return toString;
	}
	

}
