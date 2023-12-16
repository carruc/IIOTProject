package model.descriptors;

import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static model.descriptors.CartesianMotorActuatorDescriptor.clampPoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartesianMotorActuatorDescriptorTest {

    @Test
    void refreshValue() {
        CartesianMotorActuatorDescriptor cm = new CartesianMotorActuatorDescriptor();

        List<Point2D.Double> destPoints = new ArrayList<Point2D.Double>(List.of(new Point2D.Double[]{new Point2D.Double(-1, 0), new Point2D.Double(0, -1), new Point2D.Double(1, 0), new Point2D.Double(0, 1), new Point2D.Double(10, 1), new Point2D.Double(1, 10), new Point2D.Double(11, 1), new Point2D.Double(1, 11),}));

        for (Point2D.Double destPoint : destPoints) {

            clampPoint(destPoint);
            Point2D.Double startingPoint = new Point2D.Double(cm.getValue().getX(), cm.getValue().getY());

            cm.to(destPoint);
            Point2D.Double increment = new Point2D.Double(
                    (destPoint.getX() - startingPoint.getX()) * cm.getPrecision(),
                    (destPoint.getY() - startingPoint.getY()) * cm.getPrecision());
            System.out.println("Testing for: TEST " + destPoint + ", " + increment + ", " + cm.getPrecision());
            System.out.println("Testing for: ACT  " + cm.getDestPoint() + ", " + cm.getIncrement() + ", " + cm.getPrecision());
            assertEquals(0.0f, cm.getDestPoint().distance(destPoint));
            assertEquals(0.0f, cm.getIncrement().distance(increment));

            System.out.println("START: " + startingPoint);
            System.out.println("DEST: " + destPoint);
            System.out.println("INCREMENT: " + increment);

            IntStream.range(0, 1001).forEach(i -> {
                cm.refreshValue();
                System.out.println(cm.getValue());
            });

            assertEquals(startingPoint.distance(destPoint), startingPoint.distance(cm.getValue()));
        }
    }

    @Test
    void to() {
        CartesianMotorActuatorDescriptor cm = new CartesianMotorActuatorDescriptor();
        Point2D.Double point = new Point2D.Double(10, 10);
        cm.to(point);
        assertEquals(0.0f, cm.getDestPoint().distance(point));
    }

    @Test
    void clamp() {
        List<Point2D.Double> list = new ArrayList<Point2D.Double>(List.of(new Point2D.Double[]{new Point2D.Double(-1, 0), new Point2D.Double(0, -1), new Point2D.Double(1, 0), new Point2D.Double(0, 1), new Point2D.Double(10, 1), new Point2D.Double(1, 10), new Point2D.Double(11, 1), new Point2D.Double(1, 11),}));
        list.forEach(p -> {
            clampPoint(p);
            assertTrue(p.getX() <= 10 && p.getX() >= 0 && p.getY() <= 10 && p.getY() >= 0);
        });
    }
}