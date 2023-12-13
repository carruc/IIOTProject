package resource;

import model.sensor_actuator_descriptors.AlarmActuatorDescriptor;
import model.sensor_actuator_descriptors.GPSSensorDescriptor;
import model.sensor_actuator_descriptors.HealthcareSensorDescriptor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.UUID;

public class GPSSensorResource extends GenericResource<GPSSensorDescriptor> {
    //private static final Logger logger = LoggerFactory.getLogger(GPSSensorResource.class);

    private static final String RESOURCE_TYPE = "iot:object:wristband";

    private HealthcareSensorDescriptor healthcareSensor;
    private GPSSensorDescriptor gpsSensor;
    private AlarmActuatorDescriptor alarmActuator;

    private Random random = new Random(System.currentTimeMillis());

    private Timer timer = null;

    private static final long GPS_DATA_UPDATE_PERIOD = 300000;

    public GPSSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public GPSSensorResource(String id, String type){
        super(id, type);
        init();
    }

    private void init(){
        gpsSensor = new GPSSensorDescriptor();
    }
}
