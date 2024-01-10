package resource.hvac;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import utils.CoreInterfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CoapSwitchActuatorResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapSwitchActuatorResource.class);

    private static final String OBJECT_TITLE = "SwitchActuator";
    private static final Number SENSOR_VERSION = 0.1;

    private ObjectMapper objectMapper;
    private SwitchActuatorResource switchActuatorResource;
    private Boolean isOn = true;
    private String deviceId;

    public CoapSwitchActuatorResource(String deviceId, String name, SwitchActuatorResource switchActuatorResource) {
        super(name);

        if (switchActuatorResource != null && deviceId != null) {
            this.deviceId = deviceId;
            this.switchActuatorResource = switchActuatorResource;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", switchActuatorResource.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_A.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

            switchActuatorResource.addDataListener(new ResourceDataListener<Boolean>() {
                @Override
                public void onDataChanged(GenericResource<Boolean> resource, Boolean updatedValue) {
                    logger.info("Raw Resource Notification Callback ! New Value: {}", updatedValue);
                    isOn = updatedValue;
                    changed();
                }
            });
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }
    }

    private Optional<String> getJsonResponse() {
        try {
            Gson gson = new Gson();

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("deviceId", deviceId);
            jsonData.put("actuatorName", getName());
            jsonData.put("version", SENSOR_VERSION);
            jsonData.put("isOn", isOn);
            jsonData.put("timestamp", System.currentTimeMillis());

            String jsonString = gson.toJson(jsonData);

            return Optional.of(jsonString);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            Optional<String> jsonPayload = getJsonResponse();

            if (jsonPayload.isPresent())
                exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload.get(), exchange.getRequestOptions().getAccept());
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        } else
            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(isOn), MediaTypeRegistry.TEXT_PLAIN);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            if (exchange.getRequestPayload() == null) {
                isOn = !isOn;
                switchActuatorResource.setActive(isOn);
                logger.info("Resource Status Updated: {}", isOn);
                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error Handling POST -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        try {
            if (exchange.getRequestPayload() != null) {
                boolean submittedValue = Boolean.parseBoolean(new String(exchange.getRequestPayload()));
                logger.info("Submitted value: {}", submittedValue);
                isOn = submittedValue;
                switchActuatorResource.setActive(isOn);
                logger.info("Resource Status Updated: {}", isOn);
                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error Handling PUT -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}

