package device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.GenericMessage;
import message.SmartWatchAlarmAcknowledgeMessage;
import message.SmartWatchDisplayMessage;
import message.SmartWatchDrugIDMessage;
import model.descriptors.smartwatch.SmartWatchDisplayDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.smartwatch.SmartWatchAlarmAcknowledgeResource;
import resource.smartwatch.SmartWatchDisplayResource;
import resource.smartwatch.SmartWatchDrugRequestResource;

import java.util.Map;
import java.util.Optional;

import static process.ProcessConfiguration.QOS_0;
import static process.ProcessConfiguration.QOS_2;

public class SmartWatchObject implements GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(SmartWatchObject.class);

    private static final String BASIC_TOPIC = "smartwatches";

    private static final String CONTROL_TOPIC = "control";

    private static final String INFO_TOPIC = "info";

    private String smartWatchID;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private final ObjectMapper mapper;

    public SmartWatchObject() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void init(String smartWatchID, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {
        this.smartWatchID = smartWatchID;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("SmartWatch Object correctly created! Number of hosted resources: {}", resourceMap.keySet().size());
    }

    @Override
    public void start() {
        try {
            if (this.mqttClient != null && this.smartWatchID != null && this.smartWatchID.length() > 0 && this.resourceMap != null && resourceMap.keySet().size() > 0) {

                logger.info("Starting the SmartWatch");

                registerToAvailableResources();
            }
        } catch (Exception e) {
            logger.error("Error starting the Smartwatch! Msg: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void registerToAvailableResources() {
        try {
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                System.out.println(resourceEntry);
                if (resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    GenericResource smartWatchResource = resourceEntry.getValue();

                    logger.info("Registering to resource {} (id: {}) notifications ...", smartWatchResource.getType(), smartWatchResource.getId());

                    /* SmartWatch drug request resource.*/
                    if (smartWatchResource.getType().equals(SmartWatchDrugRequestResource.RESOURCE_TYPE)) {
                        try {
                            SmartWatchDrugRequestResource smartWatchDrugRequestResource = (SmartWatchDrugRequestResource) smartWatchResource;

                            String smartWatchDrugRequestTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, smartWatchID, INFO_TOPIC, resourceEntry.getKey());

                            smartWatchDrugRequestResource.addDataListener((resource, updatedValue) -> {
                                if (resource != null && updatedValue != null) {

                                    try {
                                        publishJsonFormattedMessage(smartWatchDrugRequestTopic,
                                                new SmartWatchDrugIDMessage((String) updatedValue.getValue()), false, QOS_0);
                                    } catch (MqttException | JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                                } else {
                                    logger.error("Drug request error");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    /* SmartWatch alarm acknowledge resource.*/
                    if (smartWatchResource.getType().equals(SmartWatchAlarmAcknowledgeResource.RESOURCE_TYPE)) {
                        try {
                            SmartWatchAlarmAcknowledgeResource smartWatchAlarmAcknowledgeResource = (SmartWatchAlarmAcknowledgeResource) smartWatchResource;
                            String smartWatchAckTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, smartWatchID, INFO_TOPIC, resourceEntry.getKey());

                            smartWatchAlarmAcknowledgeResource.addDataListener((resource, updatedValue) -> {
                                if (resource != null && updatedValue != null) {
                                    try {
                                        publishJsonFormattedMessage(smartWatchAckTopic,
                                                new SmartWatchAlarmAcknowledgeMessage(updatedValue.getValue()), false,
                                                QOS_0);
                                    } catch (MqttException | JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                                } else {
                                    logger.error("Alarm ack error");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    /* SmartWatch display resource.*/
                    if (smartWatchResource.getType().equals(SmartWatchDisplayResource.RESOURCE_TYPE)) {
                        try {
                            SmartWatchDisplayResource smartWatchDisplayResource = (SmartWatchDisplayResource) smartWatchResource;

                            String smartWatchDisplayTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, smartWatchID, CONTROL_TOPIC, resourceEntry.getKey());

                            this.mqttClient.subscribe(smartWatchDisplayTopic, QOS_2, (topic, message) -> {
                                byte[] payload = message.getPayload();
                                Optional<SmartWatchDisplayDescriptor> receivedDisplayValue = parseDisplayMessage(payload);

                                if (receivedDisplayValue.isPresent()) {
                                    logger.info("New display value received: {}", receivedDisplayValue.get().getValue());

                                    smartWatchDisplayResource.setValue(receivedDisplayValue.get().getValue());
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
            logger.error("Error Registering to Resource ! Msg: {}", e.getLocalizedMessage());
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

    private Optional<SmartWatchDisplayDescriptor> parseDisplayMessage(byte[] payload) {
        try {
            if (payload == null) {
                return Optional.empty();
            }
            SmartWatchDisplayMessage displayMessage = mapper.readValue(new String(payload), SmartWatchDisplayMessage.class);
            return Optional.ofNullable(new SmartWatchDisplayDescriptor(displayMessage.getDisplayValue()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void stop() {

    }
}
