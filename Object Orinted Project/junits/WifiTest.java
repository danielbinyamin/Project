package junits;
import static org.junit.Assert.*;
import org.junit.Test;
import program.*;
/**
 * This class is a Junit test class for the Wifi class
 * @author Daniel
 *
 */
public class WifiTest {

	@Test
	//tests constructor
	public void testWifi() {
		Wifi one = new Wifi("SSID1","MAC1",11,-12);
		assertEquals("SSID1", one.get_SSID());
		assertEquals("MAC1", one.get_MAC());
		assertEquals(11, one.get_freq());
		assertEquals(-12, one.get_signal());
	}

	@Test
	//tests equals method
	public void testEquals() {
		Wifi a = new Wifi("SSID1","MAC1",11,-12);
		Wifi b = new Wifi("SSID1","MAC1",11,-12);
		assertTrue(a.equals(b));
		Wifi c = new Wifi("SSID","MAC1",11,-12);
		assertFalse(a.equals(c));
		Wifi d = new Wifi("SSID1","MAC",11,-12);
		assertFalse(a.equals(d));
		Wifi e = new Wifi("SSID1","MAC1",1,-12);
		assertFalse(a.equals(e));
		Wifi f = new Wifi("SSID1","MAC1",11,-13);
		assertFalse(a.equals(f));
	}

	public void testCompareTo() {
		Wifi a = new Wifi("SSID1","MAC1",11,-12);
		Wifi b = new Wifi("SSID1","MAC1",11,-12);
		assertEquals(0,a.compareTo(b));
		a = new Wifi("SSID1","MAC1",11,-12);
		b = new Wifi("SSID1","MAC1",11,-13);
		assertEquals(1,a.compareTo(b));
		a = new Wifi("SSID1","MAC1",11,-12);
		b = new Wifi("SSID1","MAC1",11,-11);
		assertEquals(-1,a.compareTo(b));

	}


}
