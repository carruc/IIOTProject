package model;

import java.util.Random;

public class TemperatureSensorDescriptor extends GenericDescriptor<Double>{

    private final double MAX_TEMPERATURE_VALUE = 30.0;

    private final double MIN_TEMPERATURE_VALUE = 20.0;

    private final double MAX_OFFSET = +3.0;

    private final double MIN_OFFSET = -3.0;

    public static final String TEMPERATURE_SENSOR_UNIT = "C";

    public static final String TEMPERATURE_SENSOR_TYPE = "iot:sensor:temperature";

    private Random random = new Random(System.currentTimeMillis());

    public TemperatureSensorDescriptor(){
        super();
        setValue(MIN_TEMPERATURE_VALUE + this.random.nextDouble() * (MAX_TEMPERATURE_VALUE - MIN_TEMPERATURE_VALUE));
    }

    public TemperatureSensorDescriptor(Double value){
        super(value);
        setValue(MIN_TEMPERATURE_VALUE + this.random.nextDouble() * (MAX_TEMPERATURE_VALUE - MIN_TEMPERATURE_VALUE));
    }

    @Override
    public void refreshValue() {
        setValue(getValue() + (MIN_OFFSET + random.nextDouble() * (MAX_OFFSET - MIN_OFFSET)));
    }
}
