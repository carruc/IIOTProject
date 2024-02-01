package device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.GenericMessage;
import message.SmartDrugInventoryMessage;
import message.SmartDrugSensorMessage;
import message.SmartWatchDrugIDMessage;
import model.descriptors.smartdruginventory.SmartDrugInventoryDescriptor;
import model.descriptors.smartdruginventory.SmartDrugRequestDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.smartdruginventory.SmartDrugRequestResource;
import resource.smartdruginventory.SmartDrugSensorResource;

import java.util.Map;
import java.util.Optional;

import static process.ProcessConfiguration.QOS_0;
import static process.ProcessConfiguration.QOS_2;

public class SmartDrugInventoryObject implements GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(SmartDrugInventoryObject.class);

    private static final String BASIC_TOPIC = "smartdruginventory";

    private static final String CONTROL_TOPIC = "control";

    private static final String INFO_TOPIC = "info";

    private String smartDrugInventoryID;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private static final SmartDrugInventoryDescriptor smartDrugInventoryDescriptor = SmartDrugInventoryDescriptor.getInstance();

    private final ObjectMapper mapper;

    public SmartDrugInventoryObject() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void init(String smartDrugInventoryID, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {
        this.smartDrugInventoryID = smartDrugInventoryID;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("Smart Drug Inventory Object correctly created! Number of hosted resources: {}", resourceMap.keySet().size());
    }

    @Override
    public void start() {
        try {
            if (this.mqttClient != null && this.smartDrugInventoryID != null && this.smartDrugInventoryID.length() > 0 && this.resourceMap != null && resourceMap.keySet().size() > 0) {

                logger.info("Starting the Smart Drug Inventory");

                registerToAvailableResources();
            }
        } catch (Exception e) {
            logger.error("Error starting the Smart Drug Inventory! Msg: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void registerToAvailableResources() {
        try {
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                if (resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    GenericResource drugInventoryResources = resourceEntry.getValue();

                    logger.info("Registering to resource {} (id: {}) notifications ...", drugInventoryResources.getType(), drugInventoryResources.getId());

                    /* Drug Availability Resource.*/
                    if (drugInventoryResources.getType().equals(SmartDrugSensorResource.RESOURCE_TYPE)) {
                        try {
                            SmartDrugSensorResource smartDrugSensorResource =
                                    (SmartDrugSensorResource) drugInventoryResources;
                            String smartDrugSensorTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC,
                                    smartDrugInventoryID, INFO_TOPIC,
                                    resourceEntry.getKey());

                            smartDrugSensorResource.addDataListener((resource, updatedValue) ->{
                                if (resource != null && updatedValue != null) {

                                    try {
                                        publishJsonFormattedMessage(smartDrugSensorTopic,
                                                new SmartDrugSensorMessage(updatedValue.getValue()), false,
                                                QOS_0);
                                    } catch (MqttException | JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                                } else {
                                    logger.error("Drug request error");
                                }
                            });
                            publishJsonFormattedMessage(smartDrugSensorTopic,
                                    new SmartDrugSensorMessage(smartDrugSensorResource.getValue()), false,
                                    QOS_0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    /* Smart inventory drug request handling.*/
                    if (drugInventoryResources.getType().equals(SmartDrugRequestResource.RESOURCE_TYPE)) {
                        try {
                            SmartDrugRequestResource smartDrugInventoryResource = (SmartDrugRequestResource) drugInventoryResources;

                            String smartDrugInventoryTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC,
                                    smartDrugInventoryID, CONTROL_TOPIC,
                                    resourceEntry.getKey());

                            this.mqttClient.subscribe(smartDrugInventoryTopic, QOS_2, (topic, message) -> {
                                byte[] payload = message.getPayload();
                                Optional<SmartDrugRequestDescriptor> receivedSmartDrugValue =
                                        parseDrugRequestMessage(payload);
                                if (receivedSmartDrugValue.isPresent()) {
                                    logger.info("New Smart Drug Request value received: {}",
                                            receivedSmartDrugValue.get().getValue());
                                    smartDrugInventoryResource.setValue((String) receivedSmartDrugValue.get().getValue());

                                    if(smartDrugInventoryDescriptor.isAvailable()){
                                        smartDrugInventoryDescriptor.getDrug(smartDrugInventoryResource.getValue());
                                        try {
                                            publishJsonFormattedMessage("smartdruginventory/" + smartDrugInventoryDescriptor.getSmartDrugInventoryID() + "/control/drug_present",
                                                    new SmartDrugSensorMessage(true), false, 2);
                                        } catch (MqttException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                } else {
                                    throw new Exception();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error Registering to Resource! Msg: {}", e.getLocalizedMessage());
        }
    }

    private void publishJsonFormattedMessage(String topic, GenericMessage payload, boolean retained, int qos) throws MqttException, JsonProcessingException {
        if (this.mqttClient != null && this.mqttClient.isConnected() && topic != null && payload != null) {
            String messagePayload = mapper.writeValueAsString(payload);

            logger.info("Sending to topic: {} -> Data: {}", topic, messagePayload);
            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            MqttMessage.validateQos(qos);
            mqttMessage.setQos(qos);
            if (retained)
                mqttMessage.setRetained(true);
            mqttClient.publish(topic, mqttMessage);

            logger.info("Data correctly published to topic: {}", topic);
        } else
            logger.error("Error: topic or message = Null or MQTT Client is not connected!");
    }

    private Optional<SmartDrugRequestDescriptor> parseDrugRequestMessage(byte[] payload) {
        try {
            if (payload == null) {
                return Optional.empty();
            }
            SmartDrugInventoryMessage drugMessage = mapper.readValue(new String(payload),
                    SmartDrugInventoryMessage.class);
            return Optional.ofNullable(new SmartDrugRequestDescriptor(drugMessage.getMessage()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void stop() {

    }
}
