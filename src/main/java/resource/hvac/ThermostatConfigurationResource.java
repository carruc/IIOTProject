package resource.hvac;

import model.descriptors.hvac.ThermostatConfigurationDescriptor;
import resource.GenericResource;

import java.util.UUID;

public class ThermostatConfigurationResource extends GenericResource<ThermostatConfigurationDescriptor> {
    private ThermostatConfigurationDescriptor thermostatConfigurationDescriptor = new ThermostatConfigurationDescriptor();

    private static final String RESOURCE_TYPE = "iot.config.thermostat";

    public ThermostatConfigurationResource(ThermostatConfigurationDescriptor thermostatConfigurationDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.thermostatConfigurationDescriptor = thermostatConfigurationDescriptor;
    }


    public ThermostatConfigurationDescriptor loadUpdatedValue() {
        return this.thermostatConfigurationDescriptor;
    }

    public ThermostatConfigurationDescriptor getThermostatConfigurationModel() {
        return thermostatConfigurationDescriptor;
    }

    public void setThermostatConfigurationModel(ThermostatConfigurationDescriptor thermostatConfigurationModel) {
        this.thermostatConfigurationDescriptor = thermostatConfigurationModel;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ThermostatRawConfigurationParameter{");
        sb.append("thermostatConfigurationModel=").append(thermostatConfigurationDescriptor);
        sb.append('}');
        return sb.toString();
    }
}