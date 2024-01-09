package message;

import model.descriptors.wristband.GPSLocationDescriptor;
import model.point.PointXYZ;

public class GPSLocationMessage extends GenericMessage{
    private PointXYZ gpsLocation;

    public GPSLocationMessage(){
        super();
    }

    public GPSLocationMessage(PointXYZ gpsLocation){
        super();
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
