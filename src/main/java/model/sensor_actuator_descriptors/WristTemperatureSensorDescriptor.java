package model.sensor_actuator_descriptors;

import model.sensor_actuator_descriptors.GenericDescriptor;

import java.util.Random;

public class WristTemperatureSensorDescriptor extends GenericDescriptor<Double> {

    public WristTemperatureSensorDescriptor(){
        super(new Random().nextDouble() * 40);
    }
    public WristTemperatureSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
