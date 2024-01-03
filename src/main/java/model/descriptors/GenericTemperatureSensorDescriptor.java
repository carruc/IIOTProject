package model.descriptors;

/**
 * Model for thermostat-related temperature sensors.
 * Value represents the temperature in degrees-Celsius.
 */
public abstract class GenericTemperatureSensorDescriptor extends GenericDescriptor<Double> {
    private static final String TEMPERATURE_SENSOR_UNIT = "C";
    String temperatureSensorType;

    public GenericTemperatureSensorDescriptor() {
        super();
    }

    public GenericTemperatureSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("TemperatureSensorDescriptor{ type:");
        string.append(temperatureSensorType);
        string.append(", value: ");
        string.append(getValue());
        string.append(" °");
        string.append(TEMPERATURE_SENSOR_UNIT);
        string.append("}");
        return string.toString();
    }
}
