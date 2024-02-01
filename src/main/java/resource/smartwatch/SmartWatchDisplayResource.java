package resource.smartwatch;

import model.descriptors.smartwatch.SmartWatchDisplayDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.wristband.AlarmActuatorResource;

import java.util.UUID;

public class SmartWatchDisplayResource extends GenericResource<SmartWatchDisplayDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:actuator:smartwatch";

    private final SmartWatchDisplayDescriptor smartWatchDisplayDescriptor;

    public SmartWatchDisplayResource(SmartWatchDisplayDescriptor smartWatchDisplayDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.smartWatchDisplayDescriptor = smartWatchDisplayDescriptor;
    }

    @Override
    public SmartWatchDisplayDescriptor loadUpdatedValue() {
        return null;
    }

    public String getDisplayMessage(){
        return this.smartWatchDisplayDescriptor.getMessage();
    }

    public void setDisplayMessage(String message){
        this.smartWatchDisplayDescriptor.setMessage(message);
    }

    public void setValue(String value) {
        this.smartWatchDisplayDescriptor.setMessage(value);
    }

    public String getValue(){
        return this.smartWatchDisplayDescriptor.getValue();
    }
}
