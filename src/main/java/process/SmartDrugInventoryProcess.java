package process;

import device.SmartDrugInventoryObject;
import device.SmartWatchObject;
import model.descriptors.smartdruginventory.DrugDescriptor;
import model.descriptors.smartdruginventory.SmartDrugInventoryDescriptor;
import model.descriptors.smartdruginventory.SmartDrugRequestDescriptor;
import model.descriptors.smartdruginventory.SmartDrugSensorDescriptor;
import model.descriptors.smartwatch.SmartWatchAlarmAcknowledgeDescriptor;
import model.descriptors.smartwatch.SmartWatchDescriptor;
import model.descriptors.smartwatch.SmartWatchDisplayDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ProcessConfiguration;
import resource.GenericResource;
import resource.smartdruginventory.SmartDrugRequestResource;
import resource.smartdruginventory.SmartDrugSensorResource;
import resource.smartwatch.SmartWatchAlarmAcknowledgeResource;
import resource.smartwatch.SmartWatchDisplayResource;
import resource.smartwatch.SmartWatchDrugRequestResource;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartDrugInventoryProcess {

    private static final Logger logger = LoggerFactory.getLogger(process.SmartDrugInventoryProcess.class);

    public static void main(String[] args) {
        try {
            String smartDrugInventoryID = "druginv1";
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP, ProcessConfiguration.MQTT_BROKER_PORT), smartDrugInventoryID, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("SmartDrugInventory connected to the MQTT Broker. SmartDrugInventoryId: {}", smartDrugInventoryID);

            SmartDrugInventoryObject smartDrugInventoryObject = new SmartDrugInventoryObject();

            SmartDrugInventoryDescriptor drugInventoryDescriptor = SmartDrugInventoryDescriptor.getInstance();


            smartDrugInventoryObject.init(smartDrugInventoryID, mqttClient, new HashMap<String, GenericResource<?>>() {
                {
                    put("drug_request",
                            new SmartDrugRequestResource(drugInventoryDescriptor.getSmartDrugRequestDescriptor()));

                    put("drug_present", new SmartDrugSensorResource(drugInventoryDescriptor.getSmartDrugSensorDescriptor()));
                }
            });

            smartDrugInventoryObject.start();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
