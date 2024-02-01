package message;

import message.GenericMessage;

public class SmartWatchAlarmAcknowledgeMessage extends GenericMessage {

    private String acknowledged;
    public SmartWatchAlarmAcknowledgeMessage(String acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(String acknowledged) {
        this.acknowledged = acknowledged;
    }
}
