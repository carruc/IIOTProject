package model.sensor_actuator_descriptors;

public class ThermostatTemperatureSensorDescriptor extends GenericDescriptor<Double>{

    public ThermostatTemperatureSensorDescriptor(Double value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
