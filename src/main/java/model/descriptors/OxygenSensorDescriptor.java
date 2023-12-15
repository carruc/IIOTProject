package model.descriptors;

import java.util.Random;

/** Model for an O2 level sensor.
 * Value represents a percentage (0-100).
 */

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
