package resource;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.stream.Collectors;

public class GPSSensorResource extends GenericResource<GPSLocationDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(GPSSensorResource.class);

    private static final String GPX_FILE_NAME = "tracks/demo.gpx";

    public static final String RESOURCE_TYPE = "iot:sensor:gps";

    private GPSLocationDescriptor gpsLocationDescriptor;

    private List<WayPoint> wayPointList = null;

    private ListIterator<WayPoint> wayPointListIterator;

    private Timer timer;

    private static final long GPS_LOCATION_UPDATE_STARTING_DELAY = 5000;
    private static final long GPS_LOCATION_UPDATE_PERIOD = 10000;

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
            this.wayPointList = GPX.read(GPX_FILE_NAME).tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .collect(Collectors.toList());
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

                        //logger.info("{} -> Lat:{}, Lng:{}",
                        //        RESOURCE_TYPE,
                        //        currentWayPoint.getLatitude(),
                        //        currentWayPoint.getLongitude());

                        gpsLocationDescriptor.setGPSLocation(new PointXYZ(currentWayPoint.getLatitude().doubleValue(),
                                currentWayPoint.getLongitude().doubleValue(),
                                (currentWayPoint.getElevation().isPresent() ? currentWayPoint.getElevation().get().doubleValue() : 0.0)));

                        notifyUpdate(gpsLocationDescriptor);

                    }
                    //At the end of the WayPoint List
                    else{
                        logger.info("Reversing WayPoint List ...");
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
