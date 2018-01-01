package program;
/**
 * This Class represents the interface Condition for creating a dynamic condition for the filter.
 * The class has one method which returns boolean over a singleRecord

 */

import java.io.Serializable;

public interface Condition extends Serializable {
	/**
	 * This function tests whether the SingleRecord object satisfying a condition.
	 * @param s - SingleRecord object.
	 * @return boolean value.
	 */
	boolean test(SingleRecord s);

}
