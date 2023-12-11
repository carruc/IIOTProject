package resource;

import model.HealthcareSensorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.UUID;

//PER ORA il braccialetto contiene solo un sensore di salute
public class WristbandResource extends GenericResource<HealthcareSensorDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(WristbandResource.class);

    private static final String RESOURCE_TYPE = "iot:object:wristband";

    private HealthcareSensorDescriptor healthcareSensor;

    private Timer timer = null;

    private static final long UPDATE_PERIOD = 10000;

    public WristbandResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public WristbandResource(String id, String type){
        super(id, type);
        init();
    }

    private void init(){

    }
}
