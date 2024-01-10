package resource.hvac;

import org.server.GenericResource;
import org.server.ResourceDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class SwitchActuatorResource extends GenericResource<Boolean> {

    private static Logger logger = LoggerFactory.getLogger(SwitchActuatorResource.class);

    private static final String LOG_DISPLAY_NAME = "SwitchActuator";

    private static final String RESOURCE_TYPE = "iot.actuator.switch";

    private Boolean isActive;

    public SwitchActuatorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.isActive = true;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
        notifyUpdate(isActive);
    }

    @Override
    public Boolean loadUpdatedValue() {
        return this.isActive;
    }

    public static void main(String[] args) {

        SwitchActuatorResource rawResource = new SwitchActuatorResource();
        logger.info("New {} Resource Created with Id: {} ! {} New Value: {}",
                rawResource.getType(),
                rawResource.getId(),
                LOG_DISPLAY_NAME,
                rawResource.loadUpdatedValue());


        rawResource.addDataListener(new ResourceDataListener<Boolean>() {
            @Override
            public void onDataChanged(GenericResource<Boolean> resource, Boolean updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });

    }

}
