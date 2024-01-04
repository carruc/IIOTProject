package resource;

import model.descriptors.wristband.GPSLocationDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.UUID;

public class GPSSensorResource extends GenericResource<GPSLocationDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(GPSSensorResource.class);

    private static final String GPX_FILE_NAME = "tracks/demo.gpx";

    public static final String RESOURCE_TYPE = "iot:object:wristband";

    private GPSLocationDescriptor gpsSensor;

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
        gpsSensor = new GPSLocationDescriptor();
    }
}
