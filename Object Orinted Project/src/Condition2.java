/**
 * This Class represents the interface Condition for creating a dynamic condition for the filter.
 * The class has one method which returns boolean over a singleRecord
 * 
 *
 */
public interface Condition2 <T>{
	
	boolean test(T object);

}