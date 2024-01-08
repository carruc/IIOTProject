package device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.descriptors.VideoCameraDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.VideoCameraResource;

import java.util.Map;


public class VideoCameraSmartObject implements GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(VideoCameraSmartObject.class);

    private static final String BASIC_TOPIC = "camera";

    private static final String TELEMETRY_TOPIC = "telemetry";

    private static final String INFO_TOPIC = "info";

    private String cameraId;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private ObjectMapper mapper;

    public VideoCameraSmartObject() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void init(String cameraId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {
        this.cameraId = cameraId;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("Video Camera Smart Object correctly created! Resource Number: {}", resourceMap.keySet().size());
    }

    public void start() {
        try {
            if (this.mqttClient != null &&
                    this.cameraId != null && this.cameraId.length() > 0 &&
                    this.resourceMap != null && resourceMap.keySet().size() > 0) {

                logger.info("Starting the video camera");

                registerToAvailableResources();
            }

        } catch (Exception e) {
            logger.error("Error Starting the video camera! Msg: {}", e.getLocalizedMessage());
        }
    }

    public void stop(){
    }

    private void registerToAvailableResources() {
        try {
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                if (resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    GenericResource cameraResource = resourceEntry.getValue();

                    logger.info("Registering to Resource {} (id: {}) notifications ...", cameraResource.getType(), cameraResource.getId());

                    if (cameraResource.getType().equals(VideoCameraResource.RESOURCE_TYPE)) {
                        try {
                            VideoCameraResource videoCameraResource = (VideoCameraResource) cameraResource;

                            String cameraInfoTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, cameraId, INFO_TOPIC, resourceEntry.getKey());
                            publishInfoCameraData(cameraInfoTopic, videoCameraResource.getVideoCameraData());
                        } catch (Exception e) {
                            logger.error("ERROR");
                        }

                    }

                }
            });
        } catch (Exception e) {
            logger.error("Error Registering to Resource ! Msg: {}", e.getLocalizedMessage());
        }
    }

    private void publishInfoCameraData(String topic, VideoCameraDescriptor videoCameraDescriptor) throws MqttException, JsonProcessingException {
        if (this.mqttClient != null && this.mqttClient.isConnected() && topic != null && videoCameraDescriptor != null) {
            String messagePayload = mapper.writeValueAsString(videoCameraDescriptor);

            logger.info("Sending to topic: {} -> Data: {}", topic, messagePayload);
            System.out.println("Topic: " + topic + " Payload: " + messagePayload);

            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);
            mqttMessage.setRetained(true);

            mqttClient.publish(topic, mqttMessage);

            logger.info("Data Correctly Published to topic: {}", topic);

        } else
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
    }
}

