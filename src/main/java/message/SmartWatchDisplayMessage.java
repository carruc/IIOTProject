package message;

import model.descriptors.smartwatch.SmartWatchDisplayDescriptor;

public class SmartWatchDisplayMessage extends GenericMessage {
    private String message;

    public SmartWatchDisplayMessage() {
        super();
    }

    public SmartWatchDisplayMessage(String message) {
        super();
        this.message = message;
    }

    public SmartWatchDisplayMessage(SmartWatchDisplayDescriptor smartWatchDisplayDescriptor) {
        this.message = smartWatchDisplayDescriptor.getMessage();
    }

    public String getDisplayValue() {
        return message;
    }

    public void setDisplayValue(String message) {
        this.message = message;
    }
}
