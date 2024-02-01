package model.descriptors.smartwatch;

import message.SmartWatchDisplayMessage;
import model.descriptors.GenericDescriptor;

/** Modeling class for the facility's operators' wristband display.
 * Value represents displayed message.
 */
public class SmartWatchDisplayDescriptor extends GenericDescriptor<String> {

    public SmartWatchDisplayDescriptor() {
        super("", "");
    }

    public SmartWatchDisplayDescriptor(String message){
        super(message, "");
    }

    public void setMessage(String message){
        this.setValue(message);
    }

    public String getMessage(){
        return this.getValue();
    }

    @Override
    public String toString() {
        return "SmartWatchDisplayDescriptor{" + "value=" + this.getValue() + '}';
    }
}
