package model.descriptors.wristband;

public class PersonHealthcareAndAlarmFlagDescriptor {
    private Boolean healthcareFlag;
    private Boolean alarmFlag;

    public PersonHealthcareAndAlarmFlagDescriptor(){
        healthcareFlag = false;
        alarmFlag = false;
    }

    public Boolean getHealthcareFlag() {
        return healthcareFlag;
    }

    public void setHealthcareFlag(Boolean healthcareFlag) {
        this.healthcareFlag = healthcareFlag;
    }

    public Boolean getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(Boolean alarmFlag) {
        this.alarmFlag = alarmFlag;
    }
}
