package device;

import message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.descriptors.VideocameraDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import resource.GenericResource;
import resource.VideocameraInfoResource;
import resource.VideocameraResource;

import java.util.Map;

import static process.ProcessConfiguration.QOS_0;


public class VideoCameraSmartObject implements GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(VideoCameraSmartObject.class);

    private static final String BASIC_TOPIC = "videocameras";

    private static final String INFO_TOPIC = "info";

    private static final String TELEMETRY_TOPIC = "telemetry";

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

        logger.info("Video Camera Smart Object correctly created! Number of hosted resources: {}", resourceMap.keySet().size());
    }

    /** Videocamera behaviour **/

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

                    /**data topic**/

                    if (cameraResource.getType().equals(VideocameraResource.RESOURCE_TYPE)) {
                        try {
                            VideocameraResource videoCameraResource = (VideocameraResource) cameraResource;

                            String cameraDataTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, cameraId, TELEMETRY_TOPIC, resourceEntry.getKey());
                            publishJsonFormattedMessage(cameraDataTopic,
                                    new VideocameraDataMessage(videoCameraResource.getVideoCameraData()),
                                    false, QOS_0);
                        } catch (Exception e) {
                            logger.error("ERROR");
                        }
                    }

                    /**info topic**/
                    if (cameraResource.getType().equals(VideocameraInfoResource.RESOURCE_TYPE)) {
                        try {
                            VideocameraInfoResource videocameraInfoResource = (VideocameraInfoResource) cameraResource;

                            String cameraInfoTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, cameraId, INFO_TOPIC, resourceEntry.getKey());
                            publishJsonFormattedMessage(cameraInfoTopic,
                                    new VideocameraInfoMessage(videocameraInfoResource.getVideocameraInfoDescriptor()), true, QOS_0);
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

    /**
    private void publishInfoCameraData(String topic, VideocameraDescriptor videoCameraDescriptor) throws MqttException, JsonProcessingException {
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
     **/


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
}



