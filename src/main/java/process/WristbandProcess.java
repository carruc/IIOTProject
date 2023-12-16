package process;

import device.WristbandSmartObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.HealthcareSensorResource;

import java.util.HashMap;
import java.util.UUID;

public class WristbandProcess {

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static String MQTT_BROKER_IP = "127.0.0.1";
    private static int MQTT_BROKER_PORT = 1883;

    public static void main(String[] args) {
        try{
            String wristbandId = UUID.randomUUID().toString();

            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", MQTT_BROKER_IP, MQTT_BROKER_PORT), wristbandId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("MQTT client Connected ! Client id: {}",wristbandId);

            WristbandSmartObject wristbandSmartObject = new WristbandSmartObject();

            wristbandSmartObject.init(wristbandId,mqttClient,new HashMap<>(){
                {
                    put("healthData", new HealthcareSensorResource());
                }
            });


        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
