package resource.hvac.coap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.descriptors.hvac.ThermostatConfigurationDescriptor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.hvac.ThermostatConfigurationResource;

public class CoapThermostatConfigurationResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapThermostatConfigurationResource.class);

    private static final String OBJECT_TITLE = "ThermostatConfiguration";

    private static final Number VERSION = 0.1;

    private ThermostatConfigurationResource thermostatConfiguration;

    private ThermostatConfigurationDescriptor configurationDescriptorValue;

    private Gson gson;

    private String deviceId;

    public CoapThermostatConfigurationResource(String deviceId, String name, ThermostatConfigurationResource thermostatConfiguration) {

        super(name);

        if (thermostatConfiguration != null && deviceId != null) {

            this.deviceId = deviceId;

            this.thermostatConfiguration = thermostatConfiguration;
            this.configurationDescriptorValue = thermostatConfiguration.loadUpdatedValue();

            this.gson = new Gson();

            setObservable(true); // enable observing
            setObserveType(CoAP.Type.CON); // configure the notification type to CONs

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", thermostatConfiguration.getType());
            getAttributes().addAttribute("if", "Parameter");
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }

        this.thermostatConfiguration.addDataListener(new ResourceDataListener<ThermostatConfigurationDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<ThermostatConfigurationDescriptor> resource, ThermostatConfigurationDescriptor updatedValue) {
                configurationDescriptorValue = updatedValue;
                changed();
            }
        });
    }

    private String getJsonResponse() {
        try {
            // Create a JSON object representing the resource state
            JsonObject json = new JsonObject();
            json.addProperty("deviceId", deviceId);
            json.addProperty("name", getName());
            json.addProperty("version", VERSION);
            json.addProperty("minTemperature", configurationDescriptorValue.getMinTemperature());
            json.addProperty("maxTemperature", configurationDescriptorValue.getMaxTemperature());
            json.addProperty("hvacUnitResourceUri", configurationDescriptorValue.getHvacUnitResourceUri());
            json.addProperty("operationalMode", configurationDescriptorValue.getOperationalMode());
            json.addProperty("timestamp", System.currentTimeMillis());

            return gson.toJson(json);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            //se la richiesta accetta un JSON rispondo in JSON
            if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
                String jsonPayload = getJsonResponse();
                if (jsonPayload != null)
                    exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload, MediaTypeRegistry.APPLICATION_JSON);
                else
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }
            // altrimenti rispondo con un textplain
            else
                exchange.respond(CoAP.ResponseCode.CONTENT, this.gson.toJson(configurationDescriptorValue), MediaTypeRegistry.TEXT_PLAIN);

        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

}
