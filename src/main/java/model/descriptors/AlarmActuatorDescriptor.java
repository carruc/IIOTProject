package model.descriptors;

import java.util.Random;

/** Generic alarm actuator model.
 * Value represents ON/OFF state.
 */
public class AlarmActuatorDescriptor extends GenericDescriptor<Boolean> {
    public AlarmActuatorDescriptor(){
        super(false);
    }
    public AlarmActuatorDescriptor(Boolean value) {
        super(value);
    }

    @Override
    public void refreshValue() {
    }
}
