package model.sensor_actuator_descriptors;

import java.awt.geom.Point2D;

public class CartesianMotorActuatorDescriptor extends GenericDescriptor<Point2D>{

    public CartesianMotorActuatorDescriptor(Point2D value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
