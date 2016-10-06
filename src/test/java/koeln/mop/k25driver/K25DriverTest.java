package koeln.mop.k25driver;

import static org.junit.Assert.*;
import org.junit.Before;

import org.junit.Test;

import koeln.mop.canbusmatcher.CanMessage;
import koeln.mop.canbusmatcher.ConsumeResult;

public class K25DriverTest {
	private K25Driver driver;
	
	@Before public void setUp() {
		driver = new K25Driver();
	}
	
	@Test
	public void testUnknownAddress() {
		CanMessage message = new CanMessage();
		message.setAddress(0xffffff);
		ConsumeResult result = driver.onCanMessage(message);
		assertEquals(0, result.handled);
	}

	@Test
	public void testSpeed() {
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.REARWHEEL.getValue());
		message.setData(2, (byte) 0xff);
		driver.onCanMessage(message);
		assertEquals(15, driver.getRearSpeed());
	}
	
	@Test
	public void testOdometer() {
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.ODOMETER.getValue());
		message.setData(1, (byte) 0xc1);
		message.setData(2, (byte) 0xaa);
		driver.onCanMessage(message);
		assertEquals(43713, driver.getOdometer());
	}
	
	@Test
	public void testRpm() {
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.THROTTLE.getValue());
		message.setData(2, (byte) 0xd7);
		message.setData(3, (byte) 0x6);
		driver.onCanMessage(message);
		assertEquals(1751, driver.getRPM());
	}
	
	@Test
	public void testEngine() {
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.ENGINE.getValue());
		message.setData(2, (byte) 0x67);
		message.setData(5, (byte) 0x4);
		message.setData(7, (byte) 0x61);
		driver.onCanMessage(message);
		assertEquals(24.75, driver.getAirTemperature(), 0.0001);
		assertEquals(53.25, driver.getEngineTemperature(), 0.0001);
		assertEquals(2, driver.getGear());
	}
	
	@Test
	public void testZfe() {
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.ZFE.getValue());
		message.setData(3, (byte) 0x98);
		driver.onCanMessage(message);
		assertEquals(0.5960, driver.getFuelLevel(), 0.0001);
	}
}
