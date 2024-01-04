package resource;

import model.descriptors.wristband.AlarmValueDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlarmActuatorResource extends GenericResource<AlarmValueDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:actuator:alarm";

    private AlarmValueDescriptor alarmValueDescriptor;

    public AlarmActuatorResource(){
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
    }

    public AlarmActuatorResource(String id, String type){
        super(id, type);
    }

    public Boolean getValue() {
        return alarmValueDescriptor.getValue();
    }

    public void setValue(Boolean value) {
        alarmValueDescriptor.setValue(value);
    }
}
