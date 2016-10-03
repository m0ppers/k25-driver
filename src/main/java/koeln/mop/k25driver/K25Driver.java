package koeln.mop.k25driver;

import koeln.mop.kew2000.state.Odometer;
import koeln.mop.kew2000.state.Speed;
import koeln.mop.canbusmatcher.CanMessage;
import koeln.mop.canbusmatcher.CanMessageRecipient;
import koeln.mop.canbusmatcher.ConsumeResult;

public class K25Driver implements CanMessageRecipient {
	private Speed speed;
	
	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

	public Odometer getOdometer() {
		return odometer;
	}

	public void setOdometer(Odometer odometer) {
		this.odometer = odometer;
	}

	private Odometer odometer;

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
		return result;
	}
	
	private ConsumeResult parseRearWheel(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		if (this.speed != null) {
			// mop: wtf
			this.speed.setSpeedValue((int) (((message.getData(4) & 0xff) * 256 + (message.getData(3) & 0xff)) * 0.06));
			result.handled = 0xffff0000;
		}
		return result;
	}
	
	private ConsumeResult parseOdometer(CanMessage message) {
		ConsumeResult result = new ConsumeResult();

		if (this.odometer != null) {
			int odometer = ((message.getData(4) & 0xff) << 16) | ((message.getData(3) & 0xff) << 8) | (message.getData(2) & 0xff);
			this.odometer.setOdometerValue(odometer);
			result.handled = 0xffffff00;
		}
		return result;
	}
}
