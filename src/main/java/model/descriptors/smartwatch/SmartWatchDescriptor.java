package model.descriptors.smartwatch;

public class SmartWatchDescriptor {

    private final SmartWatchDisplayDescriptor smartWatchDisplayDescriptor;
    private final SmartWatchDrugRequestDescriptor smartWatchDrugRequestDescriptor;
    private final SmartWatchAlarmAcknowledgeDescriptor smartWatchAlarmAcknowledgeDescriptor;

    private final String smartWatchID;

    public SmartWatchDescriptor(SmartWatchDisplayDescriptor smartWatchDisplayDescriptor, SmartWatchDrugRequestDescriptor smartWatchDrugRequestDescriptor, SmartWatchAlarmAcknowledgeDescriptor smartWatchAlarmAcknowledgeDescriptor, String smartWatchID) {
        this.smartWatchDisplayDescriptor = smartWatchDisplayDescriptor;
        this.smartWatchDrugRequestDescriptor = smartWatchDrugRequestDescriptor;
        this.smartWatchAlarmAcknowledgeDescriptor = smartWatchAlarmAcknowledgeDescriptor;
        this.smartWatchID = smartWatchID;
    }

    public void setAck(String ack){
        this.smartWatchAlarmAcknowledgeDescriptor.setValue(ack);
    }

    public String getAck(){
        return this.smartWatchAlarmAcknowledgeDescriptor.getValue();
    }
}
