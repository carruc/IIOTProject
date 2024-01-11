import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoapThermostatConfigurationClient {
    private final static Logger logger = LoggerFactory.getLogger(CoapThermostatConfigurationClient.class);
    private static final String THERMOSTAT_ENDPOINT = "coap://127.0.0.1:5684/configuration";
    public static void main(String[] args) throws ConnectorException, IOException {

        performPutRequest();
    }

    private static void performPutRequest() throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(THERMOSTAT_ENDPOINT);

        JsonObject payload = new JsonObject();
        payload.addProperty("min_temperature", 15.0);
        payload.addProperty("max_temperature", 25.0);
        payload.addProperty("hvac_res_uri","coap://127.0.0.1:5683/switch");
        payload.addProperty("operational_mode","AUTO");

        CoapResponse response = coapClient.put(payload.toString(), MediaTypeRegistry.APPLICATION_JSON);

        if (response.isSuccess()) {
            System.out.println("PUT request successful");
        } else {
            System.err.println("PUT request failed: " + response.getCode());
        }

        coapClient.shutdown();
    }

}

