package resource.wristband;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;

import java.util.*;

public class GPSSensorResource extends GenericResource<GPSLocationDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(GPSSensorResource.class);

    private static final String GPX_FILE_NAME = "tracks/simulated_resident_path.gpx";

    public static final String RESOURCE_TYPE = "iot:sensor:gps";

    private GPSLocationDescriptor gpsLocationDescriptor;

    private List<WayPoint> wayPointList = null;
    private List<WayPoint> reversedWayPointList = null;
    private ListIterator<WayPoint> wayPointListIterator;
    private boolean reversed;

    private Timer timer;

    private static final long GPS_LOCATION_UPDATE_STARTING_DELAY = 5000;
    private static final long GPS_LOCATION_UPDATE_PERIOD = 300;

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
            wayPointList = GPX.read(GPX_FILE_NAME).getWayPoints();
            reversedWayPointList = new ArrayList<>(wayPointList);
            Collections.reverse(reversedWayPointList);
            wayPointListIterator = wayPointList.listIterator();
            reversed = false;

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
                    else{
                        if(!reversed){
                            wayPointListIterator = reversedWayPointList.listIterator();
                            reversed = true;
                        }
                        else{
                            wayPointListIterator = wayPointList.listIterator();
                            reversed = false;
                        }
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
                }
            }
        });
    }
}
