package model;

import java.util.Random;

public class TemperatureSensorDescriptor extends GenericDescriptor<Double>{
    private Random random;

    private final double MAX_TEMPERATURE_VALUE = 30.0;

    private final double MIN_TEMPERATURE_VALUE = 20.0;

    private final double MAX_OFFSET = +3.0;

    private final double MIN_OFFSET = -3.0;

    public static final String RESOURCE_UNIT = "C";

    public static final String RESOURCE_TYPE = "iot:sensor:temperature";

    public TemperatureSensorDescriptor(){
        super();
    }

    public TemperatureSensorDescriptor(String type, String unit, Double value){

    }
}
