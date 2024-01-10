package resource.hvac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.server.GenericResource;
import org.server.ResourceDataListener;
import org.server.model.ThermostatConfigurationDescriptor;
import org.server.utils.CoreInterfaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CoapThermostatConfigurationParameterResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapThermostatConfigurationParameterResource.class);

    private static final String OBJECT_TITLE = "ThermostatConfiguration";

    private static final Number VERSION = 0.1;

    private ThermostatConfigurationParameterResource thermostatConfigurationParameterResource;

    private ThermostatConfigurationDescriptor configurationModelValue;

    private ObjectMapper objectMapper;

    private String deviceId;

    public CoapThermostatConfigurationParameterResource(String deviceId, String name, ThermostatConfigurationParameterResource thermostatConfigurationParameterResource) {
        super(name);

        if (thermostatConfigurationParameterResource != null && deviceId != null) {
            this.deviceId = deviceId;
            this.thermostatConfigurationParameterResource = thermostatConfigurationParameterResource;
            this.configurationModelValue = thermostatConfigurationParameterResource.loadUpdatedValue();


            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", thermostatConfigurationParameterResource.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_P.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }

        this.thermostatConfigurationParameterResource.addDataListener(new ResourceDataListener<ThermostatConfigurationDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<ThermostatConfigurationDescriptor> resource, ThermostatConfigurationDescriptor updatedValue) {
                configurationModelValue = updatedValue;
                changed();
            }
        });
    }

    private Optional<String> getJsonResponse() {
        try {
            Gson gson = new Gson();

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("min_temperature", configurationModelValue.getMinTemperature());
            jsonData.put("max_temperature", configurationModelValue.getMaxTemperature());
            jsonData.put("hvac_res_uri", configurationModelValue.getHvacUnitResourceUri());
            jsonData.put("operational_mode", configurationModelValue.getOperationalMode().name());

            String jsonString = gson.toJson(jsonData);

            return Optional.of(jsonString);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {

            if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
                Optional<String> jsonPayload = getJsonResponse();

                if (jsonPayload.isPresent())
                    exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload.get(), exchange.getRequestOptions().getAccept());
                else
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }

            else
                exchange.respond(CoAP.ResponseCode.CONTENT, this.objectMapper.writeValueAsBytes(configurationModelValue), MediaTypeRegistry.TEXT_PLAIN);

        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}

