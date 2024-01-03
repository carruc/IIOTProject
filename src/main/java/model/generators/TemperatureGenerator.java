package model.generators;

import model.descriptors.GenericDescriptor;
import java.util.Random;

public class TemperatureGenerator extends GenericGenerator {
    private double maxTemperatureValue; //42, 35 e 42, 17
    private double minTemperatureValue;
    private final Random random = new Random(System.currentTimeMillis());

    public TemperatureGenerator(GenericDescriptor genericDescriptor) {
        super(genericDescriptor);
        this.maxTemperatureValue = maxTemperatureValue;
        this.minTemperatureValue = minTemperatureValue;
    }

    @Override
    public void refreshValue() {
        this.getGenericDescriptor().setValue(minTemperatureValue + this.random.nextDouble() * (maxTemperatureValue - minTemperatureValue));
    }
}
