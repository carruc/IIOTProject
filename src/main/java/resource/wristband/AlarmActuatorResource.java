package resource.wristband;

import model.descriptors.wristband.AlarmValueDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.GenericResource;

import java.util.UUID;

public class AlarmActuatorResource extends GenericResource<AlarmValueDescriptor> {
    private static final Logger logger = LogManager.getLogger();

    public static final String RESOURCE_TYPE = "iot:actuator:alarm";

    private AlarmValueDescriptor alarmValueDescriptor;

    public AlarmActuatorResource(){
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        alarmValueDescriptor = new AlarmValueDescriptor();
    }

    public AlarmActuatorResource(String id, String type){
        super(id, type);
        alarmValueDescriptor = new AlarmValueDescriptor();
    }

    public Boolean getValue() {
        return alarmValueDescriptor.getValue();
    }

    public void setValue(Boolean value) {
        alarmValueDescriptor.setValue(value);
    }
}
