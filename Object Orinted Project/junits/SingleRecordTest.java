package junits;
import static org.junit.Assert.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import org.junit.Test;
import program.*;

/**
 * This class is a Junit test class for the SingleRecords class
 * @author Daniel
 *
 */
public class SingleRecordTest {

	@Test
	public void testConstructor() {
		Wifi wifi1 = new Wifi("SSID1","MAC2",12,-67);
		Wifi wifi2 = new Wifi("SSID2","MAC2",15,-70);
		ArrayList<Wifi> list = new ArrayList<>();
		list.add(wifi1);
		list.add(wifi2);
		double lon = 23.3, lat = 56.6, alt = 706.6;
		String dateandtime = "03-11-2017 18:49:42";
		Calendar date = Calendar.getInstance();
		date.set(2017, 10,03,18,49,42);
		String id = "test";
		Point2D point = new Point2D.Double(lat,lon);
		SingleRecord a = new SingleRecord(id, list,dateandtime,lon,lat,alt);
		assertTrue(a.get_id().equals(id));
		assertTrue(a.get_WifiList().equals(list));
		assertTrue(a.get_location().equals(point));
		assertEquals(5, a.get_date().getTime().getDay());//the day when getting
		assertEquals(10, a.get_date().getTime().getMonth());//the month when getting
		assertEquals(117, a.get_date().getTime().getYear());//the year when getting
		assertEquals(18, a.get_date().getTime().getHours());//the ho when getting
		assertEquals(49, a.get_date().getTime().getMinutes());
		assertEquals(42, a.get_date().getTime().getSeconds());
	}

}
