package koeln.mop.k25driver;

import koeln.mop.canbusmatcher.CanMessage;
import koeln.mop.canbusmatcher.CanDriver;
import koeln.mop.canbusmatcher.ConsumeResult;

public class K25Driver implements CanDriver {
	private int frontSpeed;
	private int rearSpeed;
	private int odometer;
	private int rpm;
	private int gear;
	private double engineTemperature;
	private double airTemperature;
	private double fuelLevel;
	
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
	
	public int getGear() {
		return gear;
	}
	
	public double getAirTemperature() {
		return airTemperature;
	}

	public double getEngineTemperature() {
		return engineTemperature;
	}
	
	public double getFuelLevel() {
		return fuelLevel;
	}
	
	@Override
	public long[] getAddresses() {
		long[] addresses = {
			K25Address.ENGINE.getValue(),
			K25Address.THROTTLE.getValue(),
			K25Address.REARWHEEL.getValue(),
			K25Address.ODOMETER.getValue(),
			K25Address.ZFE.getValue()
		};
		return addresses;
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
			case ENGINE:
				return parseEngine(message);
			case ZFE:
				return parseZfe(message);
			default:
				return new ConsumeResult();
		}
	}
	
	private ConsumeResult parseThrottle(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		this.rpm = (int) ((message.getData(3) & 0xff) * 256 + (message.getData(2) & 0xff)) / 4;
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
		int odometer = ((message.getData(3) & 0xff) << 16) | ((message.getData(2) & 0xff) << 8) | (message.getData(1) & 0xff);
		this.odometer = odometer;
		result.handled = 0xffffff00;
		return result;
	}
	
	private ConsumeResult parseZfe(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		fuelLevel = (message.getData(3) & 0xff) / 255f;
		result.handled = 0xff000000L;
		return result;
	}
	
	private ConsumeResult parseEngine(CanMessage message) {
		ConsumeResult result = new ConsumeResult();
		int gearInfo = message.getData(5) & 0xf;
		
		result.handled = 0xf0000000000L;
		switch(gearInfo) {
			case 1:
				gear = 1;
				break;
			case 2:
				gear = 0;
				break;
			case 4:
				gear = 2;
				break;
			case 7:
				gear = 3;
				break;
			case 8:
				gear = 4;
				break;
			case 0xb:
				gear = 5;
				break;
			case 0xd:
				gear = 6;
				break;
			case 0xf:
				gear = -1;
				break;
		}
		
		engineTemperature = (message.getData(2) & 0xff) * 0.75 - 24;
		airTemperature = (message.getData(7) & 0xff) * 0.75 - 48;
		
		result.handled |= 0xff00000000ff0000L;
		return result;
	}
}
