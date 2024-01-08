package resource.hvac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;

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


    public Boolean loadUpdatedValue() {
        return this.isActive;
    }

    public static void main(String[] args) {

        SwitchActuatorResource resource = new SwitchActuatorResource();
        logger.info("New {} Resource Created with Id: {} ! {} New Value: {}",
                resource.getType(),
                resource.getId(),
                LOG_DISPLAY_NAME,
                resource.loadUpdatedValue());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i=0; i<100; i++){
                        resource.setActive(!resource.loadUpdatedValue());
                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        resource.addDataListener(new ResourceDataListener<Boolean>() {
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
