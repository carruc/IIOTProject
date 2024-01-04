package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;

/** Generic alarm actuator model.
 * Value represents ON/OFF state.
 */
public class AlarmValueDescriptor extends GenericDescriptor<Boolean> {
    public static final String BPM_UNIT = "Boolean";
    public AlarmValueDescriptor(){
        super(false, BPM_UNIT);
    }
    public AlarmValueDescriptor(Boolean value) {
        super(value, BPM_UNIT);
    }

    @Override
    public String toString() {
        return "AlarmValueDescriptor{" + "value=" + getValue() + ", unit='" + getUnit() + '\'' + '}';
    }
}
