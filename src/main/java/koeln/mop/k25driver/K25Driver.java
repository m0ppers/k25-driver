package koeln.mop.k25driver;

import koeln.mop.kew2000.state.Odometer;
import koeln.mop.kew2000.state.Speed;
import koeln.mop.canbusmatcher.CanMessage;
import koeln.mop.canbusmatcher.CanMessageRecipient;
import koeln.mop.canbusmatcher.ConsumeResult;

public class K25Driver implements CanMessageRecipient {
	private int frontSpeed;
	private int rearSpeed;
	private int odometer;
	private int rpm;
	
	public int getFrontSpeed() {
		return frontSpeed;
	}
	
	public int getRearSpeed() {
		return rearSpeed;
	}

	public int getOdometer() {
		return odometer;
	}
	
	public int getRPM() {
		return rpm;
	}

	@Override
	public ConsumeResult onCanMessage(CanMessage message) {
		K25Address address = K25Address.fromInt(message.getAddress());
		if (address == null) {
			return new ConsumeResult();
		}
		
		switch(address) {
			case THROTTLE:
				return parseThrottle(message);
			case REARWHEEL:
				return parseRearWheel(message);
			case ODOMETER:
				return parseOdometer(message);
			default:
				return new ConsumeResult();
		}
	}
	
	private ConsumeResult parseThrottle(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		System.out.println("HASS " + (message.getData(2) & 0xff));
		this.rpm = (int) (message.getData(3) & 0xff) * 256 + (message.getData(2) & 0xff);
		result.handled = 0xffff0000;
		return result;
	}
	
	private ConsumeResult parseRearWheel(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		// mop: wtf
		this.rearSpeed = (int) (((message.getData(3) & 0xff) * 256 + (message.getData(2) & 0xff)) * 0.06);
		result.handled = 0xffff0000;
		return result;
	}
	
	private ConsumeResult parseOdometer(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		int odometer = ((message.getData(4) & 0xff) << 16) | ((message.getData(3) & 0xff) << 8) | (message.getData(2) & 0xff);
		this.odometer = odometer;
		result.handled = 0xffffff00;
		return result;
	}
}
