package resource.hvac.coap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.hvac.SwitchActuatorResource;

public class CoapSwitchActuatorResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapSwitchActuatorResource.class);

    private static final String OBJECT_TITLE = "SwitchActuator";

    private static final Number SENSOR_VERSION = 0.1;

    private Gson gson;

    private SwitchActuatorResource switchActuator;

    private Boolean isOn = true;

    private String deviceId;

    public CoapSwitchActuatorResource(String deviceId, String name, SwitchActuatorResource switchActuator) {
        super(name);

        if(switchActuator != null && deviceId != null){

            this.deviceId = deviceId;

            this.switchActuator = switchActuator;

            this.gson = new Gson();

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", switchActuator.getType());
            getAttributes().addAttribute("if", "Actuator");
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

            switchActuator.addDataListener(new ResourceDataListener<Boolean>() {
                @Override
                public void onDataChanged(GenericResource<Boolean> resource, Boolean updatedValue) {
                    logger.info("Raw Resource Notification Callback ! New Value: {}", updatedValue);
                    isOn = updatedValue;
                    changed();
                }
            });
        } else {
            logger.error("Error");
        }
    }

    private String getJsonResponse() {
        try {
            // Create a JSON object representing the resource state
            JsonObject json = new JsonObject();
            json.addProperty("deviceId", deviceId);
            json.addProperty("name", getName());
            json.addProperty("isOn", isOn);
            json.addProperty("timestamp", System.currentTimeMillis());

            return gson.toJson(json);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            String jsonPayload = getJsonResponse();
            if (jsonPayload != null)
                exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload, MediaTypeRegistry.APPLICATION_JSON);
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
        else
            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(isOn), MediaTypeRegistry.TEXT_PLAIN);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            if (exchange.getRequestPayload() == null) {
                this.isOn = !isOn;  //imposta valore corrente a opposto di quello precedente
                this.switchActuator.setActive(isOn);
                logger.info("Resource Status Updated: {}", this.isOn);
                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error in GET", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        try {
            if (exchange.getRequestPayload() != null) {
                boolean submittedValue = Boolean.parseBoolean(new String(exchange.getRequestPayload()));
                logger.info("Submitted value: {}", submittedValue);
                this.isOn = submittedValue;
                this.switchActuator.setActive(this.isOn);
                logger.info("Resource Status Updated: {}", this.isOn);
                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error in POST -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}

