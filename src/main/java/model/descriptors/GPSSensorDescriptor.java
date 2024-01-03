package model.descriptors;

import model.point.PointXYZ;
import model.point.PointXYZUtils;

import java.util.Random;

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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("GPSSensorDescriptor{");
        stringBuilder.append(getValue());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
