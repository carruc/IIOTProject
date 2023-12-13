package model.sensor_actuator_descriptors;

public class ThermostatSelectorSensorDescriptor extends GenericDescriptor<Boolean>{
    public ThermostatSelectorSensorDescriptor(Boolean value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
