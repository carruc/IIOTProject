package model.descriptors;

import model.point.PointXYZ;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class GPSSensorDescriptorTest {

    @Test
    void refreshValue() {
        GPSSensorDescriptor gps = new GPSSensorDescriptor();
        List<PointXYZ> randomList = new ArrayList<PointXYZ>();
        IntStream.range(0, 10).forEach(i -> {
            gps.refreshValue();
            randomList.add(new PointXYZ(gps.getValue()));
        });
        randomList.forEach(s -> System.out.println(s));
    }
}