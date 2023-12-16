package model.descriptors;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class AlarmActuatorDescriptorTest {

    @Test
    void refreshValue() {
        AlarmActuatorDescriptor aad = new AlarmActuatorDescriptor();
        List<Boolean> randomList = new ArrayList<>();
        IntStream.range(0, 10).forEach( i -> {
            aad.refreshValue();
            randomList.add(aad.getValue());
        });
        System.out.println(randomList);
    }
}