package process;

import model.descriptors.hvac.ThermostatConfigurationDescriptor;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.hvac.SwitchActuatorResource;
import resource.hvac.TemperatureSensorResource;
import resource.hvac.ThermostatConfigurationResource;
import resource.hvac.coap.CoapSwitchActuatorResource;
import resource.hvac.coap.CoapTemperatureSensorResource;
import resource.hvac.coap.CoapThermostatConfigurationResource;

import java.util.UUID;

/**CoAP Server for Heating Ventilation Air Conditioning (HVAC) System **/

public class ThermostatProcess extends CoapServer {
    private final static Logger logger = LoggerFactory.getLogger(ThermostatProcess.class);

    public ThermostatProcess(int port) {
        super(port);

        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());

        TemperatureSensorResource temperatureSensor = new TemperatureSensorResource();
        SwitchActuatorResource switchActuator = new SwitchActuatorResource();
        ThermostatConfigurationResource configurationParameter = new ThermostatConfigurationResource(new ThermostatConfigurationDescriptor());

        CoapTemperatureSensorResource temperatureResource = new CoapTemperatureSensorResource(deviceId,
                "temperature",
                temperatureSensor);

        CoapSwitchActuatorResource switchResource = new CoapSwitchActuatorResource(deviceId,
                "switch",
                switchActuator);

        CoapThermostatConfigurationResource configurationResource = new CoapThermostatConfigurationResource(deviceId,
                "configuration",
                configurationParameter);

        this.add(temperatureResource);
        this.add(switchResource);
        this.add(configurationResource);

        //notifica quando temp interna cambia
        temperatureSensor.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(GenericResource<Double> resource, Double updatedValue) {

                logger.info("[THERMOSTAT-BEHAVIOUR] -> Updated Temperature Value: {}", updatedValue);

                //TODO Update Check Method
                if(switchActuator.getActive() && isHvacCommunicationRequired(configurationParameter.loadUpdatedValue(), updatedValue))
                    logger.info("[THERMOSTAT-BEHAVIOUR] -> Sending PUT Request to HVAC Unit: {}", configurationParameter.loadUpdatedValue().getHvacUnitResourceUri());
            }
        });

    }

    private static boolean isHvacCommunicationRequired(ThermostatConfigurationDescriptor thermostatConfigurationModel, double currentTemperatureValue){
        return true;
    }

    public static void main(String[] args) {

        ThermostatProcess thermostatProcess = new ThermostatProcess(5684);
        thermostatProcess.start();

        logger.info("Coap Server Started ! Available resources: ");

        thermostatProcess.getRoot().getChildren().stream().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if(!resource.getURI().equals("/.well-known")){
                resource.getChildren().stream().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });

    }
}
