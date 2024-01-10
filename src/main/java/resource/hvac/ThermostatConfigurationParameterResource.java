package resource.hvac;

import org.server.GenericResource;
import org.server.ResourceDataListener;
import org.server.model.HvacMode;
import org.server.model.ThermostatConfigurationDescriptor;

import java.util.UUID;

public class ThermostatConfigurationParameterResource extends GenericResource<ThermostatConfigurationDescriptor> {

    private ThermostatConfigurationDescriptor thermostatConfigurationDescriptor;

    private static final String RESOURCE_TYPE = "iot.config.thermostat";

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
        final StringBuffer sb = new StringBuffer("ThermostatRawConfigurationParameter{");
        sb.append("thermostatConfigurationModel=").append(thermostatConfigurationDescriptor);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        // Creazione di una configurazione iniziale
        ThermostatConfigurationDescriptor initialConfig = new ThermostatConfigurationDescriptor(20.0, 15.0, "coap://127.0.0.1:5683/room/switch", HvacMode.HEATING);

        // Creazione della risorsa con la configurazione iniziale
        ThermostatConfigurationParameterResource configResource = new ThermostatConfigurationParameterResource(initialConfig);

        // Visualizzazione dello stato iniziale
        System.out.println("Initial Configuration: " + configResource.getThermostatConfigurationModel());

        // Aggiunta di un listener per monitorare i cambiamenti
        configResource.addDataListener(new ResourceDataListener<ThermostatConfigurationDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<ThermostatConfigurationDescriptor> resource, ThermostatConfigurationDescriptor updatedValue) {
                System.out.println("Configuration Updated: " + updatedValue);
            }
        });
    }
}
