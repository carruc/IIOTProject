package model.descriptors;

public class AirTemperatureSensorDescriptor extends GenericTemperatureSensorDescriptor {

    public AirTemperatureSensorDescriptor() {
        super();
        this.temperatureSensorType = "iot:sensor:temperature:air";
    }

    public AirTemperatureSensorDescriptor(Double value) {
        super(value);
        this.temperatureSensorType = "iot:sensor:temperature:air";
    }
}
