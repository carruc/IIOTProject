package resource;

import model.sensor_actuator_descriptors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class HealthcareSensorResource extends GenericResource<HealthcareSensorDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(HealthcareSensorResource.class);

    private static final String RESOURCE_TYPE = "iot:sensor:healthcare";

    private HealthcareSensorDescriptor healthcareSensor;

    private Timer timer = null;

    private static final long HEALTHCARE_DATA_UPDATE_PERIOD = 30000;
    private static final long HEALTHCARE_DATA_UPDATE_DELAY = 5000;

    public HealthcareSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public HealthcareSensorResource(String id, String type) {
        super(id, type);
        init();
    }

    private void init() {
        healthcareSensor = new HealthcareSensorDescriptor(new BPMSensorDescriptor(), new OxygenSensorDescriptor(), new WristTemperatureSensorDescriptor());
        timer = new Timer();
    }

    private void startPeriodicTask(){
        try{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    healthcareSensor.getBpmSensor().refreshValue();
                    healthcareSensor.getOxygenSensor().refreshValue();
                    healthcareSensor.getWristTemperatureSensor().refreshValue();
                    notifyUpdate(healthcareSensor);
                }
            }, HEALTHCARE_DATA_UPDATE_DELAY, HEALTHCARE_DATA_UPDATE_PERIOD);
        } catch(Exception e){

        }
    }

    public static void main(String[] args) {

    }
}
