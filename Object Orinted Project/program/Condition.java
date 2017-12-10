package program;
/**
 * This Class represents the interface Condition for creating a dynamic condition for the filter.
 * The class has one method which returns boolean over a singleRecord

 */
public interface Condition {
	/**
	 * This function tests whether the SingleRecord object satisfying a condition.
	 * @param s - SingleRecord object.
	 * @return boolean value.
	 */
	boolean test(SingleRecord s);

}
