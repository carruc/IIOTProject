import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;

public class CoapTemperatureClient {

    public static void main(String[] args) throws ConnectorException, IOException {
        String serverUri = "coap://127.0.0.1:5684/temperature";


        CoapClient coapClient = new CoapClient(serverUri);

        CoapResponse response = coapClient.get();

        if (response.isSuccess()) {
            System.out.println("GET Request Successful");
            System.out.println("Response Code: " + response.getCode());
            System.out.println("Payload: " + response.getResponseText());
        } else {
            System.out.println("GET Request Failed");
            System.out.println("Response Code: " + response.getCode());
        }

        coapClient.shutdown();
    }
}
