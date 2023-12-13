package model.sensor_actuator_descriptors;

import java.util.Random;

public class BPMSensorDescriptor extends GenericDescriptor<Double>{

    public BPMSensorDescriptor(){
        super(new Random().nextDouble() * 150);
    }

    @Override
    public void refreshValue() {

    }
}
