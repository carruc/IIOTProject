import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import process.ProcessConfiguration;

import java.security.spec.ECField;
import java.util.ArrayList;

//INSERT THIS IN THE stop method!!!!!!!!!!!!!!!!!!!
public class ClearRetainedMessages {

    public static void main(String[] args) {
        try {
            String wristbandId = "ciccio";

            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), wristbandId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            ArrayList<String> list = new ArrayList<>();
            list.add("6a9af02e-faf4-4c07-afde-965a6c370179");
            list.add("426e9c33-b4f3-4309-a24a-37e0f669a0e1");
            list.add("e945c405-0702-4693-9dc5-75340c676e50");
            list.add("b641b220-789a-4f3c-99ed-6f06fceca376");
            list.add("ed26890a-0600-4acd-bc44-5cfeb0055bf1");


            String topic = String.format("wristband/%s/info/personal_info", "ed26890a-0600-4acd-bc44-5cfeb0055bf1");
            MqttMessage mqttMessage = new MqttMessage(new byte[] {'\0'});
            mqttMessage.setQos(0);
            mqttMessage.setRetained(true);
            mqttClient.publish(topic, mqttMessage);

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
