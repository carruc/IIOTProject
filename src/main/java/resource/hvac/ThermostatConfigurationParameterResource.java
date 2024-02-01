package resource.hvac;



import model.descriptors.thermostat.ThermostatConfigurationDescriptor;
import resource.GenericResource;

import java.util.UUID;

public class ThermostatConfigurationParameterResource extends GenericResource<ThermostatConfigurationDescriptor> {

    private ThermostatConfigurationDescriptor thermostatConfigurationDescriptor;

    private static final String RESOURCE_TYPE = "iot.configuration.thermostat";

    public ThermostatConfigurationParameterResource(ThermostatConfigurationDescriptor thermostatConfigurationDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.thermostatConfigurationDescriptor = thermostatConfigurationDescriptor;
    }

    @Override
    public ThermostatConfigurationDescriptor loadUpdatedValue() {
        return this.thermostatConfigurationDescriptor;
    }

    public ThermostatConfigurationDescriptor getThermostatConfigurationModel() {
        return thermostatConfigurationDescriptor;
    }

    public void setThermostatConfigurationModel(ThermostatConfigurationDescriptor thermostatConfigurationDescriptor) {
        this.thermostatConfigurationDescriptor = thermostatConfigurationDescriptor;
    }

    @Override
    public String toString() {
        String sb = "ThermostatRawConfigurationParameter{" + "thermostatConfigurationModel=" + thermostatConfigurationDescriptor + '}';
        return sb;
    }

}
