package model;

import model.point.PointXYZ;

public class GPSSensorDescriptor extends GenericDescriptor<PointXYZ> {
    public GPSSensorDescriptor(PointXYZ value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
