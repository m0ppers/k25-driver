package koeln.mop.k25driver;

import static org.junit.Assert.*;
import org.junit.Before;

import org.junit.Test;

import koeln.mop.canbusmatcher.CanMessage;
import koeln.mop.canbusmatcher.ConsumeResult;
import koeln.mop.kew2000.state.Odometer;
import koeln.mop.kew2000.state.Speed;

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
		driver.setSpeed(new Speed() {
			public void setSpeedValue(int speed) {
				assertEquals(15, speed);
			}
		});
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.REARWHEEL.getValue());
		message.setData(3, (byte) 0xff);
		driver.onCanMessage(message);
	}
	
	@Test
	public void testOdometer() {
		driver.setOdometer(new Odometer() {
			public void setOdometerValue(int odometer) {
				assertEquals(43713, odometer);
			}
		});
		CanMessage message = new CanMessage();
		message.setAddress(K25Address.ODOMETER.getValue());
		message.setData(2, (byte) 0xc1);
		message.setData(3, (byte) 0xaa);
		driver.onCanMessage(message);
	}
}
