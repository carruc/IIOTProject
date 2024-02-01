package resource.smartdruginventory;

import model.descriptors.smartdruginventory.SmartDrugRequestDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.wristband.AlarmActuatorResource;

import java.util.UUID;

public class SmartDrugRequestResource extends GenericResource<SmartDrugRequestDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:actuator:smartdruginventory:drug";

    private final SmartDrugRequestDescriptor smartDrugRequestDescriptor;

    public SmartDrugRequestResource(SmartDrugRequestDescriptor smartDrugRequestDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.smartDrugRequestDescriptor = smartDrugRequestDescriptor;
    }

    @Override
    public SmartDrugRequestDescriptor loadUpdatedValue() {
        return null;
    }

    public String getValue(){
        return (String) this.smartDrugRequestDescriptor.getValue();
    }

    public void setValue(String drugID){
        this.smartDrugRequestDescriptor.setValue(drugID);
    }

}
