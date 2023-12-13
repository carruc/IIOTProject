package model.sensor_actuator_descriptors;

import java.util.Random;

/** Class that models wrist temperature sensors.
 *  Value represents degrees-Celsius.
 */

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
