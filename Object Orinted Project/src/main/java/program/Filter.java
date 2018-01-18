package program;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a general filter. It has filters which are interfaces of condition and relations between them.
 * This class is Serializable which gives it the option to serialize it to the disk(save it) 
  	and load a Deserialized Object from the disk(construct the object from an external one)
 * 
 * 
 * @author Daniel
 *
 */
public class Filter implements Serializable {

	//members
	private ArrayList<Condition> _filters;
	private ArrayList<String> _relations;

	//constructors
	public Filter(Condition c) {
		_filters = new ArrayList<>();
		_filters.add(c);
		_relations = new ArrayList<String>();
	}

	public Filter(Filter other) {
		_filters = new ArrayList<Condition>(other.get_filters());
		_relations = new ArrayList<String>(other.get_relations());
	}

	public Filter() {
		_filters = new ArrayList<Condition>();
		_relations = new ArrayList<String>();
	}
	/**
	 * This constructor allows user to construct the object from an external Deserialized object
	 * @param path of Deserialized object
	 */
	public Filter(String path) {
		Filter temp = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			temp = (Filter) in.readObject();
			in.close();
			fileIn.close();
			_filters = new ArrayList<Condition>(temp.get_filters());
			try {
				_relations = new ArrayList<String>(temp.get_relations());
			}
			catch (NullPointerException e) {
				_relations = new ArrayList<String>();
			}
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

	/**
	 * A method to add a filter and the string relation to it
	 * @param c
	 * @param relation
	 */
	public void addFilter(Condition c, String relation) {
		_filters.add(c);
		_relations.add(relation);
	}

	/**
	 * This method allows user to Serialize and save the Filter object to the disk at "path"
	 * @param path
	 * @throws IOException
	 */
	public void saveFilter(String path) throws IOException {
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

	/**
	 * This method runs a filter over a specific scan taken
	 * @param r
	 * @return pass/fail
	 */
	public boolean checkFilterOverSingleRecord(SingleRecord r) {
		if(!this.multipleFilters())//only one filter with no relation
			return _filters.get(0).test(r);
		if(_relations.get(0).equals("&&")) //2 filters with && relation
			return _filters.get(0).test(r) && _filters.get(1).test(r);
		return _filters.get(0).test(r) || _filters.get(1).test(r);//2 filters with || relation
			
	}
	
	public boolean multipleFilters() {
		return _relations.size()!=0;
	}
	
	//Getters
	public ArrayList<Condition> get_filters() {
		return _filters;
	}

	public ArrayList<String> get_relations() {
		return _relations;
	}




}
