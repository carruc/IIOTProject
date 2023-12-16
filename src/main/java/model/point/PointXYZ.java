package model.point;

import java.util.Objects;

public class PointXYZ {

    private double latitude;
    private double longitude;
    private double elevation;

    public PointXYZ(double latitude, double longitude, double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public PointXYZ(PointXYZ pointXYZ){
        this.latitude = pointXYZ.getLatitude();
        this.longitude = pointXYZ.getLongitude();
        this.elevation = pointXYZ.getElevation();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PointXYZ pointXYZ = (PointXYZ) o;
        return Double.compare(pointXYZ.latitude, latitude) == 0 && Double.compare(pointXYZ.longitude, longitude) == 0 && Double.compare(pointXYZ.elevation, elevation) == 0;
    }

    @Override
    public String toString() {
        return String.format("[lat = %.3f, lon = %.3f, el =  %.3f]", latitude, longitude, elevation);
    }
}
