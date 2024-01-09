package model.descriptors.hvac;

import model.descriptors.GenericDescriptor;

public class TemperatureSensorDescriptor extends GenericDescriptor<Double> {

    public static final Double DEFAULT_TEMPERATURE_VALUE = 21.0;
    public static final String DEFAULT_TEMPERATURE_UNIT = "°C";

    public TemperatureSensorDescriptor() {
        super(DEFAULT_TEMPERATURE_VALUE, DEFAULT_TEMPERATURE_UNIT);
    }

    public Double getTemperature(){
        return getValue();
    }

    public void setTemperature(Double temperature){
        setValue(temperature);
    }
}
