import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoapTemperatureClient {

    private static final String THERMOSTAT_ENDPOINT = "coap://127.0.0.1:5684/temperature";
    private final static Logger logger = LoggerFactory.getLogger(CoapTemperatureClient.class);
    public static void main(String[] args) throws ConnectorException, IOException {

        CoapClient coapClient = new CoapClient(THERMOSTAT_ENDPOINT);

        try {
            CoapResponse coapResp;
            coapResp = coapClient.get();

            //Pretty print for the received response
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

            //The "CoapResponse" message contains the response.
            String text = coapResp.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + coapResp.advanced().getMID());
            logger.info("Token: " + coapResp.advanced().getTokenString());

        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }

        coapClient.shutdown();
    }
}
