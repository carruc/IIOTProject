package model.descriptors;

import java.awt.geom.Point2D;

/** Model for the Smart Drug Inventory's cartesian motor.
 * Value represents the location for the drug retrieval.
 */
public class CartesianMotorActuatorDescriptor extends GenericDescriptor<Point2D>{

    public CartesianMotorActuatorDescriptor(Point2D value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
