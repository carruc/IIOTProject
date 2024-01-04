import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ProcessConfiguration;

import java.util.UUID;

public class DataCollectorManager {
    private final static Logger logger = LoggerFactory.getLogger(DataCollectorManager.class);

    private static final String TARGET_TOPIC = "#";

    public static void main(String[] args) {
        try{
            String clientId = UUID.randomUUID().toString();

            MqttClientPersistence persistence = new MemoryPersistence();

            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), clientId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("CONNECTED");
            System.out.println("Connected");
            mqttClient.subscribe("wristband/+/telemetry/+", new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    byte[] payload = message.getPayload();
                    System.out.println("Topic: " + topic + " Payload: " + new String(payload));
                }
            });
        } catch(Exception e){
            logger.error("ERROR");
        }
    }


}
