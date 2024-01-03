package model.descriptors;

public class WristTemperatureSensorDescriptor extends GenericTemperatureSensorDescriptor {

    public WristTemperatureSensorDescriptor() {
        super();
        this.temperatureSensorType = "iot:sensor:temperature:wrist";
    }

    public WristTemperatureSensorDescriptor(Double value) {
        super(value);
        this.temperatureSensorType = "iot:sensor:temperature:wrist";
    }
}
