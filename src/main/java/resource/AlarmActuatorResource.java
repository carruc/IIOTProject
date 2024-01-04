package resource;

import model.descriptors.AlarmActuatorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class AlarmActuatorResource extends GenericResource<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:actuator:alarm";

    private Boolean isActive = false;

    public AlarmActuatorResource(){
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
    }

    public AlarmActuatorResource(String id, String type){
        super(id, type);
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
