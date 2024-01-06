package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;

public class BodyTemperatureDescriptor extends GenericDescriptor<Double> {

    public static final Double MIN_BODY_TEMPERATURE = 30.0;
    public static final Double MAX_BODY_TEMPERATURE = 45.0;
    public static final Double MAX_BODY_TEMPERATURE_VARIATION = 0.3;

    public static final Double DEFAULT_BODY_TEMPERATURE = 36.0;

    public static final String BODY_TEMPERATURE_UNIT = "°C";


    public BodyTemperatureDescriptor(){
        super(DEFAULT_BODY_TEMPERATURE, BODY_TEMPERATURE_UNIT);
    }

    public BodyTemperatureDescriptor(Double bodyTemperature) {
        super(bodyTemperature, BODY_TEMPERATURE_UNIT);
    }

    public Double getBodyTemperature() {
        return getValue();
    }

    public void setBodyTemperature(Double bodyTemperature) {
        if(bodyTemperature < MIN_BODY_TEMPERATURE)
            setValue(MIN_BODY_TEMPERATURE);
        if(bodyTemperature > MAX_BODY_TEMPERATURE)
            setValue(MAX_BODY_TEMPERATURE);
        setValue(bodyTemperature);
    }

    @Override
    public String toString() {
        return "BodyTemperatureDescriptor{" + "value=" + getBodyTemperature() + ", unit='" + getUnit() + '\'' + '}';
    }
}
