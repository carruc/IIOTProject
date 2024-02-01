package process;

import device.WristbandSmartObject;
import model.descriptors.wristband.PersonDataDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.wristband.AlarmActuatorResource;
import resource.wristband.GPSSensorResource;
import resource.wristband.HealthcareSensorResource;
import resource.wristband.PersonDataResource;

import java.util.HashMap;


public class WristbandProcess {

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int STOP_MINUTE = 1;

    public static void main(String[] args) {
        try{
            //String wristbandId = UUID.randomUUID().toString();
            String wristbandId = "wristband47"; //Fixed value for logging purposes

            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), wristbandId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("Wristband connected to the MQTT Broker. WristbandId: {}",wristbandId);

            WristbandSmartObject wristbandSmartObject = new WristbandSmartObject();

            PersonDataDescriptor personDataDescriptor = new PersonDataDescriptor("FPPCVL", "Filippo", "Cavalieri", 21
                    , 5, wristbandId);
            wristbandSmartObject.init(wristbandId, mqttClient,new HashMap<>(){
                {
                    put("healthcare", new HealthcareSensorResource());
                    put("gps", new GPSSensorResource());
                    put("alarm", new AlarmActuatorResource());
                    put("personal_info", new PersonDataResource(personDataDescriptor));
                }
            });

            wristbandSmartObject.start();

            //Stop the wristband after STOP_MINUTE
            try{
                Thread.sleep((long) ONE_MINUTE_IN_MILLISECONDS * STOP_MINUTE);
                wristbandSmartObject.stop();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
