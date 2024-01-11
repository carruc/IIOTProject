import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.Utils;
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

        CoapResponse response = coapClient.put(payload.toString(), MediaTypeRegistry.APPLICATION_JSON);

        if (response.isSuccess()) {
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(response));
            String text = response.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + response.advanced().getMID());
            logger.info("Token: " + response.advanced().getTokenString());
        } else {
            logger.error("PUT request failed: {}", response.getCode());
        }

        coapClient.shutdown();
    }

}

