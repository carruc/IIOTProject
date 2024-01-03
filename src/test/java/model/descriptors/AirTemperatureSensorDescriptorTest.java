package model.descriptors;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class AirTemperatureSensorDescriptorTest {

    @Test
    void refreshValue() {
        GenericTemperatureSensorDescriptor ats = new WristTemperatureSensorDescriptor();
        List<Double> randomList = new ArrayList<Double>();
        IntStream.range(0, 100).forEach(i -> {
            ats.refreshValue();
            randomList.add(ats.getValue());
        });
        System.out.println(randomList);
    }
}