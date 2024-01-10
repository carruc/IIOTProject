import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;

public class CoapThermostatConfigurationClient {

    public static void main(String[] args) throws ConnectorException, IOException {

        String serverUri = "coap://127.0.0.1:5684/configuration";

        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("min_temperature", 15.0);
        jsonPayload.addProperty("max_temperature", 25.0);


        performPutRequest(serverUri, jsonPayload);
    }

    private static void performPutRequest(String uri, JsonObject jsonPayload) throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(uri);

        CoapResponse response = coapClient.put(jsonPayload.toString(),  MediaTypeRegistry.APPLICATION_JSON);

        if (response.isSuccess()) {
            System.out.println("PUT Request Successful");
            System.out.println("Response Code: " + response.getCode());
        } else {
            System.out.println("PUT Request Failed");
            System.out.println("Response Code: " + response.getCode());
        }

        coapClient.shutdown();
    }
}

