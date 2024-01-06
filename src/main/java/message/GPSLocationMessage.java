package message;

import model.descriptors.wristband.GPSLocationDescriptor;
import model.point.PointXYZ;

public class GPSLocationMessage extends GenericMessage{
    private PointXYZ gpsLocation;

    public GPSLocationMessage(){

    }

    public GPSLocationMessage(PointXYZ gpsLocation){
        this.gpsLocation = gpsLocation;
    }

    public GPSLocationMessage(GPSLocationDescriptor gpsLocationDescriptor){
        this(gpsLocationDescriptor.getGPSLocation());
    }

    public PointXYZ getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(PointXYZ gpsLocation) {
        this.gpsLocation = gpsLocation;
    }
}
