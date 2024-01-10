package process.hvac;

import model.descriptors.ThermostatConfigurationDescriptor;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.hvac.*;
import utils.HvacMode;

import java.io.IOException;
import java.util.UUID;


public class ThermostatCoapSmartObjectProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(ThermostatCoapSmartObjectProcess.class);
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:5684/switch";

    public ThermostatCoapSmartObjectProcess(int port) {
        super(port);

        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());

        //INIT Emulated Physical Sensors and Actuators
        TemperatureSensorResource temperatureSensorResource = new TemperatureSensorResource();
        SwitchActuatorResource switchActuatorResource = new SwitchActuatorResource();
        ThermostatConfigurationParameterResource configurationRawParameter = new ThermostatConfigurationParameterResource(new ThermostatConfigurationDescriptor());

        CoapTemperatureResource temperatureResource = new CoapTemperatureResource(deviceId,
                "temperature",
                temperatureSensorResource);

        CoapSwitchActuatorResource switchResource = new CoapSwitchActuatorResource(deviceId,
                "switch",
                switchActuatorResource);

        CoapThermostatConfigurationParameterResource configurationResource = new CoapThermostatConfigurationParameterResource(deviceId,
                "configuration",
                configurationRawParameter);

        this.add(temperatureResource);
        this.add(switchResource);
        this.add(configurationResource);

        /**Verifica sulla temperatura**/
        temperatureSensorResource.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(GenericResource<Double> resource, Double updatedValue) {

                logger.info("[THERMOSTAT-BEHAVIOUR] -> Updated Temperature Value: {}", updatedValue);

                if (switchActuatorResource.getActive() && isHvacCommunicationRequired(configurationRawParameter.loadUpdatedValue(), updatedValue)) {
                    logger.info("[THERMOSTAT-BEHAVIOUR] -> Sending PUT Request to HVAC Unit: {}", configurationRawParameter.loadUpdatedValue().getHvacUnitResourceUri());

                    ThermostatConfigurationDescriptor currentConfiguration = configurationRawParameter.loadUpdatedValue();
                    double minTemperature = currentConfiguration.getMinTemperature();
                    double maxTemperature = currentConfiguration.getMaxTemperature();

                    if (updatedValue < minTemperature) {
                        currentConfiguration.setOperationalMode(HvacMode.HEATING);
                    } else if (updatedValue > maxTemperature) {
                        currentConfiguration.setOperationalMode(HvacMode.AIR_CONDITIONING);
                    } else {
                        currentConfiguration.setOperationalMode(HvacMode.OFF);
                    }

                    String switchResourceUri = switchResource.getURI();
                    sendSwitchActuatorPutRequest();

                }
            }
        });

    }

    /**
     * Metodo che effettua controllo sulla temperatura e indica se c'è necessità di modificare il valore o no*/
    private static boolean isHvacCommunicationRequired(ThermostatConfigurationDescriptor thermostatConfigurationDescriptor, double currentTemperatureValue) {
        double minTemperature = thermostatConfigurationDescriptor.getMinTemperature();
        double maxTemperature = thermostatConfigurationDescriptor.getMaxTemperature();


        if (currentTemperatureValue < minTemperature) {
            logger.info("[THERMOSTAT-BEHAVIOUR] -> Temperature below the minimum threshold. Turning on heating.");
            return true;
        }

        if (currentTemperatureValue > maxTemperature) {
            logger.info("[THERMOSTAT-BEHAVIOUR] -> Temperature above the maximum threshold. Turning on air conditioning.");
            return true;
        }

        return false;
    }

    private static void sendSwitchActuatorPutRequest() {
        CoapClient coapClient = new CoapClient(COAP_ENDPOINT);

        Request request = new Request(CoAP.Code.PUT);

        String myPayload = "true";
        logger.info("PUT Request Random Payload: {}", myPayload);
        request.setPayload(myPayload);

        request.setConfirmable(true);

        logger.info("Request Pretty Print: \n{}", Utils.prettyPrint(request));

        //Synchronously send the POST request (blocking call)
        CoapResponse coapResp = null;

        try {

            coapResp = coapClient.advanced(request);

            //Pretty print for the received response
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

            //The "CoapResponse" message contains the response.
            String text = coapResp.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + coapResp.advanced().getMID());
            logger.info("Token: " + coapResp.advanced().getTokenString());

        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        ThermostatCoapSmartObjectProcess smartObjectProcess = new ThermostatCoapSmartObjectProcess(5684);
        smartObjectProcess.start();

        logger.info("Coap Server Started ! Available resources: ");

        smartObjectProcess.getRoot().getChildren().stream().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if(!resource.getURI().equals("/.well-known")){
                resource.getChildren().stream().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });

    }


}
