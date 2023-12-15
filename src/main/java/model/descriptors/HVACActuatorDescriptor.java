package model.descriptors;

/** Model for HVAC piloting. Value represents the delivered temperature.
 * Does not take into account hysteresis logic.
 */

public class HVACActuatorDescriptor extends GenericDescriptor<Double>{

    public HVACActuatorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
