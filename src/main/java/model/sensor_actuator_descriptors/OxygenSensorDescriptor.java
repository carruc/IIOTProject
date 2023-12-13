package model.sensor_actuator_descriptors;

import model.sensor_actuator_descriptors.GenericDescriptor;

import java.util.Random;

public class OxygenSensorDescriptor extends GenericDescriptor<Double> {

    public OxygenSensorDescriptor(){
        super(new Random().nextDouble() * 100);
    }
    public OxygenSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
