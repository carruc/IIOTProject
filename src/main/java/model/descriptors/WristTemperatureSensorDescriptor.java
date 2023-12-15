package model.descriptors;

import java.util.Random;

/** Class that models wrist temperature sensors.
 *  Value represents degrees-Celsius.
 */

public class WristTemperatureSensorDescriptor extends GenericDescriptor<Double> {
    private final double MAX_TEMPERATURE_VALUE = 42.0;

    private final double MIN_TEMPERATURE_VALUE = 35.0;

    private final double MAX_OFFSET = +3.0;

    private final double MIN_OFFSET = -3.0;

    public static final String TEMPERATURE_SENSOR_UNIT = "C";

    public static final String TEMPERATURE_SENSOR_TYPE = "iot:sensor:temperature";

    private final Random random = new Random(System.currentTimeMillis());

    public WristTemperatureSensorDescriptor() {
        super(0.0);
        setValue(MIN_TEMPERATURE_VALUE + this.random.nextDouble() * (MAX_TEMPERATURE_VALUE - MIN_TEMPERATURE_VALUE));
    }

    public WristTemperatureSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {
        setValue(getValue() + (MIN_OFFSET * random.nextDouble() + MAX_OFFSET * random.nextDouble()));
    }
}
