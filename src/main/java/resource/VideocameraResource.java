package resource;

import model.descriptors.VideocameraDescriptor;
import model.descriptors.wristband.HealthcareDataDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class VideocameraResource extends GenericResource<VideocameraDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(VideocameraResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:videocamera";

    private VideocameraDescriptor videoCameraData;
    private Timer timer;
    private Random random;

    private static final long CAMERA_DATA_UPDATE_STARTING_DELAY = 5000;
    private static final long CAMERA_DATA_UPDATE_PERIOD = 5000;

    public VideocameraResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public VideocameraResource(String id, String type) {
        super(id, type);
        init();
    }

    private void init() {
        videoCameraData = new VideocameraDescriptor();
        timer = new Timer();
        random = new Random();
        startPeriodicTask();
    }

    private void startPeriodicTask() {
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    videoCameraData.setNumberOfPeople(random.nextInt(10)); //Numero casuale di persone fino a 10
                    notifyUpdate(videoCameraData);
                }
            }, CAMERA_DATA_UPDATE_STARTING_DELAY, CAMERA_DATA_UPDATE_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VideocameraDescriptor getVideoCameraData() {
        return videoCameraData;
    }

    public static void main(String[] args) {
        VideocameraResource videocameraResource = new VideocameraResource();

        videocameraResource.addDataListener(new ResourceDataListener<VideocameraDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<VideocameraDescriptor> resource, VideocameraDescriptor updatedValue) {
                if (resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New videocamera value: {}", resource.getId(), updatedValue);
                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                } else {
                    logger.error("Error");
                }
            }
        });
    }
}

