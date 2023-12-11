package model.point;

public class PointXYZUtils {

    /**
     * Calculate distance between two points in latitude, longitude and
     * elevation. Uses Haversine method as its base.
     *
     * @param p1 First PointXYZ object.
     * @param p2 Second PointXYZ object.
     * @returns Distance in Meters
     */
    public static double distanceXYZ(PointXYZ p1, PointXYZ p2){

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double lonDistance = Math.toRadians(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        double height = p1.getElevation() - p2.getElevation();

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return distance;
    }

    /**
     * Calculate distance between two points in latitude and longitude without
     * taking into account height difference. Uses Haversine method as its base.
     *
     * @param p1 First PointXYZ object.
     * @param p2 Second PointXYZ object.
     * @returns Distance in Meters
     */
    public static double distanceXY(PointXYZ p1, PointXYZ p2){

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double lonDistance = Math.toRadians(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        return distance;
    }
}
