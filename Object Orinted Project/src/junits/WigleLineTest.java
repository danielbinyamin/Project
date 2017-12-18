package junits;
import static org.junit.Assert.*;
import org.junit.Test;
import program.*;
/**
 * This class is a Junit test class for the WiggleLine class
 * @author Tal
 *
 */
public class WigleLineTest {

	@Test
	public void testConstructor() {
		String line = "7c:b7:33:2c:d8:e7,AlexLovesIra,[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS],05-11-17 23:21,6,-67,32.100515,35.205195,710.3,5.800000191,WIFI";
		WigleLine w1 = new WigleLine(line, "id1");
		assertTrue(w1.get_ssid().equals("AlexLovesIra"));
	}
	
	@Test
	public void testCompareTo() {
		String line1 = "7c:b7:33:2c:d8:e7,name1,[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS],05-11-17 23:21,6,-67,32.100515,35.205195,710.3,5.800000191,WIFI";
		String line2 = "7c:b7:33:2c:d8:e7,name2,[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS],05-11-17 23:21,6,-10,32.100515,35.205195,710.3,5.800000191,WIFI";
		WigleLine w1 = new WigleLine(line1, "id1");
		WigleLine w2 = new WigleLine(line2, "id2");
		w1.compareTo(w2);
		assertFalse(w1.compareTo(w2)==-1);
	}

}
