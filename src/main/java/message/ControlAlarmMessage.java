package message;

import model.descriptors.wristband.AlarmValueDescriptor;

public class ControlAlarmMessage extends GenericMessage{
    private Boolean alarmValue;

    public ControlAlarmMessage(){

    }

    public ControlAlarmMessage(Boolean alarmValue) {
        this.alarmValue = alarmValue;
    }

    public ControlAlarmMessage(AlarmValueDescriptor alarmValueDescriptor) {
        this.alarmValue = alarmValueDescriptor.getValue();
    }

    public Boolean getAlarmValue() {
        return alarmValue;
    }

    public void setAlarmValue(Boolean alarmValue) {
        this.alarmValue = alarmValue;
    }
}
