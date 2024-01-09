package process;

import device.VideoCameraSmartObject;
import model.descriptors.VideocameraInfoDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.VideocameraInfoResource;
import resource.VideocameraResource;

import java.util.HashMap;

public class VideocameraProcess {
    private static final Logger logger = LoggerFactory.getLogger(VideocameraProcess.class);


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

            VideoCameraSmartObject videoCameraSmartObject = new VideoCameraSmartObject();

            VideocameraInfoDescriptor videocameraInfoDescriptor = new VideocameraInfoDescriptor(120,cameraId);

            videoCameraSmartObject.init(cameraId, mqttClient, new HashMap<>() {
                {
                    put("people_counter", new VideocameraResource());

                    put("device_info", new VideocameraInfoResource(videocameraInfoDescriptor));
                }

            });
            videoCameraSmartObject.start();


        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
