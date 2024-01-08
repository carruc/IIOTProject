package process;

import device.VideoCameraSmartObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class VideoCameraProcess {
    private static final Logger logger = LoggerFactory.getLogger(VideoCameraProcess.class);


    public static void main(String[] args) {
        try {
            String cameraId = "camera1"; // Valore fisso per l'identificatore della telecamera
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), cameraId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("MQTT client Connected ! Client id: {}", cameraId);
            System.out.println("Connected");

            VideoCameraSmartObject videoCameraSmartObject = new VideoCameraSmartObject();

            videoCameraSmartObject.init(cameraId, mqttClient, new HashMap<>());
            videoCameraSmartObject.start();


        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}

