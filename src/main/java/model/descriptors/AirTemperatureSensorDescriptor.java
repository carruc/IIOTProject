package model.descriptors;

public class AirTemperatureSensorDescriptor extends GenericTemperatureSensorDescriptor {

    private final Double MAX_TMP = 25.0;
    private final Double MIN_TMP = 18.0;

    public AirTemperatureSensorDescriptor() {
        super();
        this.temperatureSensorType = "iot:sensor:temperature:air";
    }

    public AirTemperatureSensorDescriptor(Double value) {
        super(value);
        this.temperatureSensorType = "iot:sensor:temperature:air";
    }

    @Override
    public void refreshValue() {
        this.setValue(MIN_TMP + this.getRandom().nextDouble() * (MAX_TMP - MIN_TMP));
    }
}
