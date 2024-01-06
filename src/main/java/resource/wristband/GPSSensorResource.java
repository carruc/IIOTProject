package resource.wristband;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.GenericResource;
import resource.ResourceDataListener;

import java.util.*;

public class GPSSensorResource extends GenericResource<GPSLocationDescriptor> {
    private static final Logger logger = LogManager.getLogger();

    private static final String GPX_FILE_NAME = "tracks/simulated_resident_path.gpx";

    public static final String RESOURCE_TYPE = "iot:sensor:gps";

    private GPSLocationDescriptor gpsLocationDescriptor;

    private List<WayPoint> wayPointList = null;

    private ListIterator<WayPoint> wayPointListIterator;

    private Timer timer;

    private static final long GPS_LOCATION_UPDATE_STARTING_DELAY = 5000;
    private static final long GPS_LOCATION_UPDATE_PERIOD = 2000;

    public GPSSensorResource() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    public GPSSensorResource(String id, String type){
        super(id, type);
        init();
    }

    private void init(){
        try {
            gpsLocationDescriptor = new GPSLocationDescriptor();
            this.wayPointList = GPX.read(GPX_FILE_NAME).getWayPoints();
            this.wayPointListIterator = this.wayPointList.listIterator();
            timer = new Timer();
            startPeriodicTask();
        } catch(Exception e){
            logger.error("ERROR");
        }

    }

    public void startPeriodicTask(){
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(wayPointListIterator.hasNext()){
                        WayPoint currentWayPoint = wayPointListIterator.next();


                        gpsLocationDescriptor.setGPSLocation(new PointXYZ(currentWayPoint.getLatitude().doubleValue(),
                                currentWayPoint.getLongitude().doubleValue(),
                                (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0)));

                        notifyUpdate(gpsLocationDescriptor);

                    }
                    //At the end of the WayPoint List
                    else{
                        Collections.reverse(wayPointList);
                        wayPointListIterator = wayPointList.listIterator();
                        logger.info("Iterating backward on the GPS Waypoint List ...");
                    }
                }
            }, GPS_LOCATION_UPDATE_STARTING_DELAY, GPS_LOCATION_UPDATE_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GPSLocationDescriptor getGpsLocationDescriptor() {
        return gpsLocationDescriptor;
    }

    public static void main(String[] args) {
        GPSSensorResource gpsSensorResource = new GPSSensorResource();
        gpsSensorResource.addDataListener(new ResourceDataListener<GPSLocationDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<GPSLocationDescriptor> resource, GPSLocationDescriptor updatedValue) {
                if (resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New gps sensor value: {}", resource.getId(), updatedValue);
                    System.out.println("Device " + resource.getId() + " Value: " + updatedValue);
                } else {
                    logger.error("Error");
                    //System.out.println("Error");
                }
            }
        });
    }
}
