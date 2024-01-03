package model.generators;

import model.descriptors.GenericDescriptor;
import model.point.PointXYZ;

public class GPSSensorGenerator extends GenericGenerator {

    public static final double BOUND = 0.5;
    public static final double LAT_GAIN = 0.1;
    public static final double LONG_GAIN = 0.1;
    public static final double HEIGHT_GAIN = 1;

    public GPSSensorGenerator(GenericDescriptor genericDescriptor) {
        super(genericDescriptor);
    }
    @Override
    public void refreshValue() {
        PointXYZ point = (PointXYZ) this.getGenericDescriptor().getValue();
        point.setLatitude(point.getLatitude() + this.getRandom().nextDouble(-BOUND, BOUND) * LAT_GAIN);
        point.setLongitude(point.getLongitude() + this.getRandom().nextDouble(-BOUND, BOUND) * LONG_GAIN);
        point.setElevation(point.getElevation() + this.getRandom().nextDouble(-BOUND, BOUND) * HEIGHT_GAIN);
        this.getGenericDescriptor().setValue(point);
    }
}
