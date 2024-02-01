package resource.smartdruginventory;

import model.descriptors.smartdruginventory.SmartDrugSensorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.wristband.AlarmActuatorResource;

import java.util.UUID;

public class SmartDrugSensorResource extends GenericResource<SmartDrugSensorDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    private final SmartDrugSensorDescriptor smartDrugSensorDescriptor;

    public static final String RESOURCE_TYPE = "iot:sensor:smartdruginventory";

    public SmartDrugSensorResource(SmartDrugSensorDescriptor smartDrugSensorDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.smartDrugSensorDescriptor = smartDrugSensorDescriptor;

    }

    @Override
    public SmartDrugSensorDescriptor loadUpdatedValue() {
        return null;
    }

    public Boolean getValue() {
        return this.smartDrugSensorDescriptor.getValue();
    }
}
