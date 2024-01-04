package process;

import device.WristbandSmartObject;
import model.descriptors.AlarmActuatorDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.AlarmActuatorResource;
import resource.GPSSensorResource;
import resource.HealthcareSensorResource;

import java.util.HashMap;
import java.util.UUID;

public class WristbandProcess {

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static int STOP_MINUTE = 3;

    public static void main(String[] args) {
        try{
            String wristbandId = UUID.randomUUID().toString();

            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), wristbandId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("MQTT client Connected ! Client id: {}",wristbandId);

            WristbandSmartObject wristbandSmartObject = new WristbandSmartObject();

            wristbandSmartObject.init(wristbandId,mqttClient,new HashMap<>(){
                {
                    put("healthcare", new HealthcareSensorResource());
                    put("gps", new GPSSensorResource());
                    put("alarm", new AlarmActuatorResource());
                }
            });

            wristbandSmartObject.start();

            //Stop the wristband after STOP_MINUTE
            try{
                Thread.sleep(60000 * STOP_MINUTE);
                wristbandSmartObject.stop();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
