package resource;

import model.descriptors.VideoCameraDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class VideoCameraResource extends GenericResource<VideoCameraDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(VideoCameraResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:video_camera";

    private VideoCameraDescriptor videoCameraData;
    private Timer timer;
    private Random random;

    private static final long CAMERA_DATA_UPDATE_STARTING_DELAY = 5000;
    private static final long CAMERA_DATA_UPDATE_PERIOD = 5000;

    public VideoCameraResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public VideoCameraResource(String id, String type) {
        super(id, type);
        init();
    }

    private void init() {
        videoCameraData = new VideoCameraDescriptor();
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

    public VideoCameraDescriptor getVideoCameraData() {
        return videoCameraData;
    }
}

