package model.descriptors;

import model.descriptors.wristband.AlarmValueDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class AlarmValueDescriptorTest {

    @Test
    void refreshValue() {
        AlarmValueDescriptor aad = new AlarmValueDescriptor();
        List<Boolean> randomList = new ArrayList<>();
        IntStream.range(0, 10).forEach( i -> {
            aad.setValue();
            randomList.add(aad.getValue());
        });
        System.out.println(randomList);
    }
}