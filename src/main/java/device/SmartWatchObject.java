package device;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import resource.GenericResource;

import java.util.Map;

public class SmartWatchObject implements GenericSmartObject{
    @Override
    public void init(String smartObjectId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
