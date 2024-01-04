package resource;

import model.descriptors.wristband.BPMDescriptor;
import model.descriptors.wristband.BodyTemperatureDescriptor;
import model.descriptors.wristband.HealthcareDataDescriptor;
import model.descriptors.wristband.OxygenDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class HealthcareSensorResource extends GenericResource<HealthcareDataDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(HealthcareSensorResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:healthcare";

    private HealthcareDataDescriptor healthcareData;

    private Timer timer;

    private static final long HEALTHCARE_DATA_UPDATE_STARTING_DELAY = 5000;
    private static final long HEALTHCARE_DATA_UPDATE_PERIOD = 5000;

    private Random random;

    public HealthcareSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public HealthcareSensorResource(String id, String type) {
        super(id, type);
        init();
    }

    private void init() {
        healthcareData = new HealthcareDataDescriptor(new BPMDescriptor(), new OxygenDescriptor(), new BodyTemperatureDescriptor());
        timer = new Timer();
        random = new Random();
        startPeriodicTask();
    }

    private void startPeriodicTask() {
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    healthcareData.setBPM(healthcareData.getBPM() + (((random.nextDouble() * 2) - 1) * BPMDescriptor.MAX_BPM_VARIATION));
                    healthcareData.setOxygen(healthcareData.getOxygen() + (((random.nextDouble() * 2) - 1) * OxygenDescriptor.MAX_OXYGEN_VARIATION));
                    healthcareData.setBodyTemperature(healthcareData.getBodyTemperature() + (((random.nextDouble() * 2) - 1) * BodyTemperatureDescriptor.MAX_BODY_TEMPERATURE_VARIATION));
                    notifyUpdate(healthcareData);
                }
            }, HEALTHCARE_DATA_UPDATE_STARTING_DELAY, HEALTHCARE_DATA_UPDATE_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HealthcareSensorResource healthcareSensorResource = new HealthcareSensorResource();

        healthcareSensorResource.addDataListener(new ResourceDataListener<HealthcareDataDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<HealthcareDataDescriptor> resource, HealthcareDataDescriptor updatedValue) {
                if (resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New healthcare sensor value: {}", resource.getId(), updatedValue);
                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                } else {
                    logger.error("Error");
                    //System.out.println("Error");
                }
            }
        });
    }
}
