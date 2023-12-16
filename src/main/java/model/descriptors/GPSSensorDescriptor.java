package model.descriptors;

import model.point.PointXYZ;
import model.point.PointXYZUtils;

import java.util.Random;

/** Three-dimensional GPS location model. Uses proprietary class "PointXYZ".
 */
public class GPSSensorDescriptor extends GenericDescriptor<PointXYZ> {
    public static final double BOUND = 0.5;
    public static final double LAT_GAIN = 0.1;
    public static final double LONG_GAIN = 0.1;
    public static final double HEIGHT_GAIN = 1;

    private final Random random = new Random(System.currentTimeMillis());

    public GPSSensorDescriptor(){
        super(new PointXYZ(0, 0, 0));
        setValue(new PointXYZ(
                random.nextDouble(-BOUND, BOUND) * 90,
                random.nextDouble(-BOUND, BOUND) * 180,
                random.nextDouble() * 10)
        );
    }
    public GPSSensorDescriptor(PointXYZ value) {
        super(value);
    }

    @Override
    public void refreshValue() {
        PointXYZ point = getValue();
        point.setLatitude(point.getLatitude() + random.nextDouble(-BOUND, BOUND) * LAT_GAIN);
        point.setLongitude(point.getLongitude() + random.nextDouble(-BOUND, BOUND) * LONG_GAIN);
        point.setElevation(point.getElevation() + random.nextDouble(-BOUND, BOUND) * HEIGHT_GAIN);
        setValue(point);
    }
}
