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
import resource.hvac.TemperatureSensorResource;

public class CoapTemperatureSensorResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(CoapTemperatureSensorResource.class);

    private static final String OBJECT_TITLE = "TemperatureSensor";

    private static final Number SENSOR_VERSION = 0.1;

    private TemperatureSensorResource temperatureSensor;

    private Gson gson;

    private Double updatedTemperatureValue = 0.0;

    private String deviceId;

    public CoapTemperatureSensorResource(String deviceId, String name, TemperatureSensorResource temperatureSensor) {

        super(name);

        if (temperatureSensor != null && deviceId != null) {

            this.deviceId = deviceId;

            this.temperatureSensor = temperatureSensor;

            this.gson = new Gson();

            setObservable(true); // enable observing
            setObserveType(CoAP.Type.CON); // configure the notification type to CONs

            getAttributes().setTitle(OBJECT_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", temperatureSensor.getType());    //resource type
            getAttributes().addAttribute("if", "Sensor");                  //Interface Description
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_JSON));   //content type
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
        } else {
            logger.error("Error -> NULL Raw Reference !");
        }

        this.temperatureSensor.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(GenericResource<Double> resource, Double updatedValue) {
                updatedTemperatureValue = updatedValue;
                changed();
            }
        });

    }

    private String getJsonResponse() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("deviceId", deviceId);
            json.addProperty("name", getName());
            json.addProperty("unit", "C");
            json.addProperty("value", updatedTemperatureValue);
            json.addProperty("timestamp", System.currentTimeMillis());

            return gson.toJson(json);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        //max age è dopo quanto risorsa diventa obsoleta, deve essere uguale a update_period
        exchange.setMaxAge(TemperatureSensorResource.UPDATE_PERIOD);

        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            String jsonPayload = getJsonResponse();
            if (jsonPayload != null)
                exchange.respond(CoAP.ResponseCode.CONTENT, jsonPayload, MediaTypeRegistry.APPLICATION_JSON);
            else
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
        else
            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(updatedTemperatureValue), MediaTypeRegistry.TEXT_PLAIN);

    }
}

