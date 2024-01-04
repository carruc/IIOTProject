package device;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import resource.GenericResource;

import java.util.Map;

public interface GenericSmartObject {
    public void init(String smartObjectId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap);

    public void start();

    public void stop();
}
