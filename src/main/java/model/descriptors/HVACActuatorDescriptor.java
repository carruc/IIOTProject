package model.descriptors;

/** Model for HVAC piloting. Value represents the delivered temperature.
 * Does not take into account hysteresis logic.
 */

public class HVACActuatorDescriptor extends GenericDescriptor<Boolean>{

    public HVACActuatorDescriptor(Boolean value) {
        super(value, "");
    }

    /*@Override
    public void refreshValue() {

    }*/
}
