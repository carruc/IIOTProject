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

public class CoapTemperatureResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapTemperatureResource.class);

    private static final String OBJECT_TITLE = "TemperatureSensor";
    private static final Number SENSOR_VERSION = 0.1;
    private final String UNIT = "Cel";

    private TemperatureSensorResource temperatureSensorResource;
    private ObjectMapper objectMapper;
    private Double updatedTemperatureValue = 0.0;
    private String deviceId;

    public CoapTemperatureResource(String deviceId, String name, TemperatureSensorResource temperatureSensorResource) {
        super(name);

        if (temperatureSensorResource != null && deviceId != null) {
            this.deviceId = deviceId;
            this.temperatureSensorResource = temperatureSensorResource;

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true);
            setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", temperatureSensorResource.getType());
            getAttributes().addAttribute("if", CoreInterfaces.CORE_S.getValue());
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }

        this.temperatureSensorResource.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(GenericResource<Double> resource, Double updatedValue) {
                updatedTemperatureValue = updatedValue;
                changed();
            }
        });
    }

    private Optional<String> getJsonResponse() {
        try {
            Gson gson = new Gson();


            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("deviceId", deviceId);
            jsonData.put("sensorName", getName());
            jsonData.put("version", SENSOR_VERSION);
            jsonData.put("unit", UNIT);
            jsonData.put("temperature", updatedTemperatureValue);
            jsonData.put("timestamp", System.currentTimeMillis());

            String jsonString = gson.toJson(jsonData);

            return Optional.of(jsonString);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.setMaxAge(TemperatureSensorResource.UPDATE_PERIOD);

        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            Optional<String> jsonPayload = getJsonResponse();

            if (jsonPayload.isPresent())
                exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload.get(), exchange.getRequestOptions().getAccept());
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        } else
            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(updatedTemperatureValue), MediaTypeRegistry.TEXT_PLAIN);
    }
}

