package resource.hvac;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TemperatureSensorResource extends GenericResource<Double> {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureSensorResource.class);

    //bound per la generazione dei valori di temperatura
    private static final double MIN_TEMPERATURE_VALUE = 18.0;

    private static final double MAX_TEMPERATURE_VALUE = 30.0;

    private static final double MIN_TEMPERATURE_VARIATION = 0.1;

    private static final double MAX_TEMPERATURE_VARIATION = 1.0;

    private static final String LOG_DISPLAY_NAME = "TemperatureSensor";

    public static final long UPDATE_PERIOD = 5000;

    private static final long TASK_DELAY_TIME = 5000;

    private static final String RESOURCE_TYPE = "iot.sensor.temperature";

    private Double updatedValue;

    private Random random;

    private Timer updateTimer = null;

    public TemperatureSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    private void init(){

        try{

            this.random = new Random(System.currentTimeMillis());
            this.updatedValue = MIN_TEMPERATURE_VALUE + this.random.nextDouble()*(MAX_TEMPERATURE_VALUE - MIN_TEMPERATURE_VALUE);

            startPeriodicEventValueUpdateTask();

        }catch (Exception e){
            logger.error("Error initializing the IoT Resource ! Msg: {}", e.getLocalizedMessage());
        }

    }

    private void startPeriodicEventValueUpdateTask(){

        try{

            logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);

            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    double variation = (MIN_TEMPERATURE_VARIATION + MAX_TEMPERATURE_VARIATION*random.nextDouble()) * (random.nextDouble() > 0.5 ? 1.0 : -1.0);
                    updatedValue = updatedValue + variation;
                    notifyUpdate(updatedValue);
                }
            }, TASK_DELAY_TIME, UPDATE_PERIOD);

        }catch (Exception e){
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }

    }

    @Override
    public Double loadUpdatedValue() {
        return this.updatedValue;
    }

    public static void main(String[] args) {

        TemperatureSensorResource rawResource = new TemperatureSensorResource();
        logger.info("New {} Resource Created with Id: {} ! {} New Value: {}",
                rawResource.getType(),
                rawResource.getId(),
                LOG_DISPLAY_NAME,
                rawResource.loadUpdatedValue());

        rawResource.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(GenericResource<Double> resource, Double updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });

    }

}
