package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;
import model.point.PointXYZ;

/** Three-dimensional GPS location model. Uses proprietary class "PointXYZ".
 */
public class GPSLocationDescriptor extends GenericDescriptor<PointXYZ> {

    public static final PointXYZ DEFAULT_GPS_LOCATION = new PointXYZ(0, 0, 0);

    public static final String GPS_LOCATION_UNIT = "Coordinate";

    public GPSLocationDescriptor(){
        super(DEFAULT_GPS_LOCATION, GPS_LOCATION_UNIT);
    }
    public GPSLocationDescriptor(PointXYZ location) {
        super(location, GPS_LOCATION_UNIT);
    }

    public PointXYZ getGPSLocation(){
        return getValue();
    }

    public void setGPSLocation(PointXYZ location){
        setValue(location);
    }

    @Override
    public String toString() {
        return "GPSLocationDescriptor{" + "value=" + getGPSLocation() + ", unit='" + getUnit() + '\'' + '}';
    }
}
