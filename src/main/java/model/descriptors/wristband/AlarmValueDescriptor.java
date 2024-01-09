package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;

public class AlarmValueDescriptor extends GenericDescriptor<Boolean> {

    public static final String ALARM_VALUE_UNIT = "Boolean";
    public AlarmValueDescriptor(){
        super(false, ALARM_VALUE_UNIT);
    }
    public AlarmValueDescriptor(Boolean value) {
        super(value, ALARM_VALUE_UNIT);
    }

    @Override
    public String toString() {
        return "AlarmValueDescriptor{" + "value=" + getValue() + ", unit='" + getUnit() + '\'' + '}';
    }
}
