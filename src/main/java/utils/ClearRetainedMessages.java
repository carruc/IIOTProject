package utils;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import process.ProcessConfiguration;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.UUID;

public class ClearRetainedMessages {

    public static void clearRetainedMessages(String topic) throws MqttException {

        String clientId = UUID.randomUUID().toString();

        MqttClientPersistence persistence = new MemoryPersistence();
        IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP, ProcessConfiguration.MQTT_BROKER_PORT), clientId, persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        mqttClient.connect(options);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(0);
        mqttMessage.setRetained(true);
        mqttClient.publish(topic, mqttMessage);

        mqttClient.disconnect();
        mqttClient.close();
        System.out.println("Done!");
    }
}
