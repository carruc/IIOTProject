package device;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import resource.GenericResource;

import java.util.Map;

public interface GenericSmartObject {
    void init(String smartObjectId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap);

    void start();

    void stop();
}
