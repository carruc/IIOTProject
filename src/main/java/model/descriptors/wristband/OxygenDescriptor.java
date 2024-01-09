package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;

public class OxygenDescriptor extends GenericDescriptor<Double> {
    public static final Double MIN_OXYGEN = 60.0;
    public static final Double MAX_OXYGEN = 100.0;
    public static final Double MAX_OXYGEN_VARIATION = 0.5;

    public static final Double DEFAULT_OXYGEN = 95.0;

    public static final String OXYGEN_UNIT = "%";

    public OxygenDescriptor(){
        super(DEFAULT_OXYGEN, OXYGEN_UNIT);
    }

    public OxygenDescriptor(Double oxygen){
        super(oxygen, OXYGEN_UNIT);
    }

    public Double getOxygen() {
        return getValue();
    }

    public void setOxygen(Double oxygen) {
        if(oxygen < MIN_OXYGEN)
            setValue(MIN_OXYGEN);
        if(oxygen > MAX_OXYGEN)
            setValue(MAX_OXYGEN);
        setValue(oxygen);
    }

    @Override
    public String toString() {
        return "OxygenDescriptor{" + "value=" + getOxygen() + ", unit='" + getUnit() + '\'' + '}';
    }
}
