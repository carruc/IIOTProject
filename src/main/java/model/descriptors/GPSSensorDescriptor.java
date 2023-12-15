package model.descriptors;

import model.point.PointXYZ;

/** Three-dimensional GPS location model. Uses proprietary class "PointXYZ".
 */
public class GPSSensorDescriptor extends GenericDescriptor<PointXYZ> {

    public GPSSensorDescriptor(){
        super(new PointXYZ(0, 0, 0));
    }
    public GPSSensorDescriptor(PointXYZ value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
