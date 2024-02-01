package process;

import device.SmartWatchObject;
import model.descriptors.smartdruginventory.DrugDescriptor;
import model.descriptors.smartdruginventory.SmartDrugInventoryDescriptor;
import model.descriptors.smartwatch.SmartWatchAlarmAcknowledgeDescriptor;
import model.descriptors.smartwatch.SmartWatchDescriptor;
import model.descriptors.smartwatch.SmartWatchDisplayDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.smartwatch.SmartWatchAlarmAcknowledgeResource;
import resource.smartwatch.SmartWatchDisplayResource;
import resource.smartwatch.SmartWatchDrugRequestResource;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartWatchProcess {

    private static final Logger logger = LoggerFactory.getLogger(SmartWatchProcess.class);
    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int STOP_MINUTE = 1;

    public static void main(String[] args) {
        try {
            String smartWatchID = "smartwatch1";
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP, ProcessConfiguration.MQTT_BROKER_PORT), smartWatchID, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("SmartWatch connected to the MQTT Broker. SmartWatchId: {}", smartWatchID);

            SmartWatchObject smartWatchObject = new SmartWatchObject();

            SmartWatchDisplayDescriptor swdd = new SmartWatchDisplayDescriptor();
            SmartWatchDrugRequestDescriptor swdrd = new SmartWatchDrugRequestDescriptor("");
            SmartWatchAlarmAcknowledgeDescriptor swaad = new SmartWatchAlarmAcknowledgeDescriptor("");
            SmartWatchDescriptor smartWatchDescriptor = new SmartWatchDescriptor(swdd, swdrd, swaad, smartWatchID);

            smartWatchObject.init(smartWatchID, mqttClient, new HashMap<String, GenericResource<?>>() {
                {
                    put("alarm_acknowledge", new SmartWatchAlarmAcknowledgeResource(swaad));
                    put("drug_request", new SmartWatchDrugRequestResource(swdrd));
                    put("display_message", new SmartWatchDisplayResource(swdd));
                }
            });

            smartWatchObject.start();

            try{
                Thread.sleep((long) ONE_MINUTE_IN_MILLISECONDS * STOP_MINUTE);
                smartWatchObject.stop();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
