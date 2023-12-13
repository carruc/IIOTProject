package resource;

import model.sensor_actuator_descriptors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class HealthcareSensorResource extends GenericResource<HealthcareSensorDescriptor> {
    //private static final Logger logger = LoggerFactory.getLogger(HealthcareSensorResource.class);

    private static final String RESOURCE_TYPE = "iot:sensor:healthcare";

    private HealthcareSensorDescriptor healthcareSensor;

    private Timer timer;

    private static final long HEALTHCARE_DATA_UPDATE_DELAY = 500;
    private static final long HEALTHCARE_DATA_UPDATE_PERIOD = 1000;

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
        startPeriodicTask();
    }

    private void startPeriodicTask(){
        try{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println();
                    healthcareSensor.getBpmSensor().refreshValue();
                    healthcareSensor.getOxygenSensor().refreshValue();
                    healthcareSensor.getWristTemperatureSensor().refreshValue();
                    notifyUpdate(healthcareSensor);
                }
            }, HEALTHCARE_DATA_UPDATE_DELAY, HEALTHCARE_DATA_UPDATE_PERIOD);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HealthcareSensorResource healthcareSensorResource = new HealthcareSensorResource();

        healthcareSensorResource.addDataListener(new ResourceDataListener<HealthcareSensorDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<HealthcareSensorDescriptor> resource,
                                      HealthcareSensorDescriptor updatedValue) {
                if(resource != null && updatedValue != null){
                    //logger.info("Device: {} -> New healthcare sensor value: {}", resource.getId(), updatedValue);
                    System.out.println("Device" + resource.getId() + "Value: " + updatedValue);
                } else{
                    //logger.error("Error");
                    System.out.println("Error");
                }
            }
        });
    }
}
