package resource.smartwatch;

import model.descriptors.smartwatch.SmartWatchAlarmAcknowledgeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.wristband.AlarmActuatorResource;

import java.util.Random;
import java.util.Timer;
import java.util.UUID;

public class SmartWatchAlarmAcknowledgeResource extends GenericResource<SmartWatchAlarmAcknowledgeDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:smartwatch:ack";

    private static final long SMARTWATCH_MESSAGE_UPDATE_STARTING_DELAY = 5000;
    private static final long SMARTWATCH_MESSAGE_UPDATE_PERIOD = 20000;

    private Timer timer;
    private Random random;

    public final SmartWatchAlarmAcknowledgeDescriptor smartWatchAlarmAcknowledgeDescriptor;

    public SmartWatchAlarmAcknowledgeResource(SmartWatchAlarmAcknowledgeDescriptor smartWatchAlarmAcknowledgeDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.smartWatchAlarmAcknowledgeDescriptor = smartWatchAlarmAcknowledgeDescriptor;
    }

    private void init() {
        timer = new Timer();
        random = new Random();
    }

    @Override
    public SmartWatchAlarmAcknowledgeDescriptor loadUpdatedValue() {
        return null;
    }

    public void setValue(String acknowledged) {
        this.smartWatchAlarmAcknowledgeDescriptor.setValue(acknowledged);
    }

    public String getValue() {
        return this.smartWatchAlarmAcknowledgeDescriptor.getValue();
    }

    public static void main(String[] args) {

        SmartWatchAlarmAcknowledgeDescriptor alarmDescriptor = new SmartWatchAlarmAcknowledgeDescriptor("");
        SmartWatchAlarmAcknowledgeResource alarmAcknowledge = new SmartWatchAlarmAcknowledgeResource(alarmDescriptor);

        alarmAcknowledge.addDataListener(new ResourceDataListener<SmartWatchAlarmAcknowledgeDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<SmartWatchAlarmAcknowledgeDescriptor> resource, SmartWatchAlarmAcknowledgeDescriptor updatedValue) {
                if (resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New alarm acknowledge: {}", resource.getId(), updatedValue);
                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                } else {
                    logger.error("Alarm acknowledge error");
                }
            }
        });
    }
}
