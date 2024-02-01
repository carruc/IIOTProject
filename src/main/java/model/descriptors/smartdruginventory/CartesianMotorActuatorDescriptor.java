package model.descriptors.smartdruginventory;

import model.descriptors.GenericDescriptor;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * Model for the Smart Drug Inventory's cartesian motor.
 * Value represents the current location on the cartesian map in meters.
 * incrementSize and precision are a measures of the motor's sensitivity;
 */
public class CartesianMotorActuatorDescriptor extends GenericDescriptor<Double> {

    public static final Point2D.Double startingPoint = new Point2D.Double(0.0, 0.0);
    private static final double MAX_X = 10.0f;
    private static final double MAX_Y = 10.0f;
    private static final double MIN_X = 0.0f;
    private static final double MIN_Y = 0.0f;
    private final double precision;
    private Boolean idle;
    private Point2D.Double destPoint;
    private Point2D.Double increment;

    public CartesianMotorActuatorDescriptor() {
        super(new Point2D.Double(), "");
        this.destPoint = new Point2D.Double();
        this.increment = new Point2D.Double();
        this.precision = 0.001;
        this.idle = true;
    }

    public Point2D.Double getStartingPoint(){
        return startingPoint;
    }

    public CartesianMotorActuatorDescriptor(double precision) {
        super(new Point2D.Double(), "");
        this.destPoint = new Point2D.Double();
        this.increment = new Point2D.Double();
        this.precision = precision;
    }

    /*@Override
    public void refreshValue() {
        if (destPoint.distance(getValue()) > precision) {
            this.move();
        } else {
            this.setValue(destPoint);
            this.increment = new Point2D.Double();
        }
    }*/

    public Point2D.Double getCurrentPoint(){
        return this.getValue();
    }

    public Point2D.Double getDestPoint() {
        return destPoint;
    }

    public Point2D.Double getIncrement() {
        return increment;
    }

    public double getPrecision() {
        return precision;
    }

    public Boolean isIdle(){
        return this.idle;
    }

    public void to(Point2D.Double destPoint) {
        clampPoint(destPoint);
        this.destPoint = destPoint;
        this.increment = new Point2D.Double((destPoint.getX() - getValue().getX()) * precision, (destPoint.getY() - getValue().getY()) * precision);

        this.idle = false;

        //To implement with Thread
        while(destPoint.distance(getValue()) > precision) {
            this.step();
        }

        this.setValue(destPoint);
        this.increment = new Point2D.Double();

        this.idle = true;
    }

    private void step(){
        Point2D.Double point = getValue();
        point.setLocation(point.getX() + increment.getX(), point.getY() + increment.getY());
        this.setValue(point);
    }

    public static void clampPoint(Point2D point) {
        point.setLocation(Math.max(MIN_X, Math.min(MAX_X, point.getX())), Math.max(MIN_Y, Math.min(MAX_Y, point.getY())));
    }

}
