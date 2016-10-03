package koeln.mop.k25driver;

public enum K25Address {
	THROTTLE(0x10c),
	FRONTWHEEL(0x294),
	REARWHEEL(0x2a8),
	ODOMETER(0x3f8);
	
    private final int value;

    K25Address(final int newValue) {
        value = newValue;
    }

    public int getValue() {
    	return value;
    }
    
    public static K25Address fromInt(int value) {
    	// mop: wow ... that seems really expensive :S
        for (K25Address type : K25Address.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}	
