package model.descriptors;

import java.util.Random;

/** Model for an O2 level sensor.
 * Value represents a percentage (0-100).
 */

public class OxygenSensorDescriptor extends GenericDescriptor<Double> {

    public OxygenSensorDescriptor(){
        super();
    }
    public OxygenSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {
        this.setValue(this.getRandom().nextDouble(0.700, 0.999));
    }
}
