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

    public GPSSensorDescriptor(){
        super(new PointXYZ(0, 0, 0));
    }
    public GPSSensorDescriptor(PointXYZ value) {
        super(value);
    }

    @Override
    public void refreshValue() {
        PointXYZ point = (PointXYZ) this.getValue();
        point.setLatitude(point.getLatitude() + this.getRandom().nextDouble(-BOUND, BOUND) * LAT_GAIN);
        point.setLongitude(point.getLongitude() + this.getRandom().nextDouble(-BOUND, BOUND) * LONG_GAIN);
        point.setElevation(point.getElevation() + this.getRandom().nextDouble(-BOUND, BOUND) * HEIGHT_GAIN);
        this.setValue(point);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("GPSSensorDescriptor{");
        stringBuilder.append(getValue());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
