import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import message.*;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PointXYZUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import process.ProcessConfiguration;

import java.io.IOException;
import java.util.*;

import static process.ProcessConfiguration.QOS_2;

public class DataCollectorManager {
    private final static Logger logger = LoggerFactory.getLogger(DataCollectorManager.class);

    private static final String VIDEOCAMERA_INFO_TOPIC = "videocamera/+/info/+";
    private static final String VIDEOCAMERA_TELEMETRY_TOPIC = "videocamera/+/telemetry/+";


    private static final String BASE_WRISTBAND_TOPIC = "wristbands";
    private static final String WRISTBAND_TELEMETRY_TOPIC = "wristbands/+/telemetry/+";
    private static final String WRISTBAND_INFO_TOPIC = "wristbands/+/info/+";
    private static final String IRREGULAR_HEALTHCARE_DATA_TOPIC = "irregular_healthcare";
    private static final String HEALTHCARE_TOPIC = "healthcare";
    private static final String GPS_TOPIC = "gps";
    private static final String CONTROL_TOPIC = "control";
    private static final String ALARM_TOPIC = "alarm";


    private static final Double MAX_TOLERABLE_BPM_VALUE = 100.0;
    private static final Double MAX_TOLERABLE_BODY_TEMPERATURE_VALUE = 38.0;
    private static final Double MIN_TOLERABLE_BPM_VALUE = 55.0;
    private static final Double MIN_TOLERABLE_OXYGEN_VALUE = 90.0;
    private static final Double MIN_TOLERABLE_BODY_TEMPERATURE_VALUE = 35.0;

    private static final GPSLocationDescriptor NURSING_HOUSE_LOCATION =
            new GPSLocationDescriptor(new PointXYZ(44.64382848606259, 10.914790499876274, 37.27));
    private static final Double MAX_TOLERABLE_WRISTBAND_DISTANCE_METER = 2000.0;

    private static final Map<String, PersonHealthcareDataDescriptor> personHealthcareDataMap = new HashMap<>();
    private static final Map<String, PersonHealthcareAndAlarmFlagDescriptor> personFlagMap = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        runMQTTSubscribers();
    }

    private static void performGetRequest(String uri) throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(uri);

        CoapResponse response = coapClient.get();
        if (response.isSuccess()) {
            System.out.println("GET Request Successful");
            System.out.println("Response Code: " + response.getCode());
            System.out.println("Payload: " + response.getResponseText());
        } else {
            System.out.println("GET Request Failed");
            System.out.println("Response Code: " + response.getCode());
        }

        coapClient.shutdown();
    }

    private static void performPutRequest(String uri, JsonObject jsonPayload) throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(uri);

        CoapResponse response = coapClient.put(jsonPayload.toString(),  MediaTypeRegistry.APPLICATION_JSON);

        if (response.isSuccess()) {
            System.out.println("PUT Request Successful");
            System.out.println("Response Code: " + response.getCode());
        } else {
            System.out.println("PUT Request Failed");
            System.out.println("Response Code: " + response.getCode());
        }

        coapClient.shutdown();
    }

    public static void runMQTTSubscribers(){
        try {
            String clientId = UUID.randomUUID().toString();

            MqttClientPersistence persistence = new MemoryPersistence();

            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP, ProcessConfiguration.MQTT_BROKER_PORT), clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("Data collector and manager connected to the MQTT Broker");

            mqttClient.subscribe(WRISTBAND_INFO_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try {
                        byte[] payload = message.getPayload();
                        logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));

                        Optional<PersonDataDescriptor> personDataDescriptor = parseInfoPersonalData(message.getPayload());

                        if (personDataDescriptor.isPresent()) {
                            PersonDataDescriptor personData = personDataDescriptor.get();
                            if (!personHealthcareDataMap.containsKey(personData.getWristbandId())) {
                                personHealthcareDataMap.put(personData.getWristbandId(), new PersonHealthcareDataDescriptor(personData));
                            }
                            if(!personFlagMap.containsKey(personData.getWristbandId())){
                                personFlagMap.put(personData.getWristbandId(),
                                        new PersonHealthcareAndAlarmFlagDescriptor());
                            }
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mqttClient.subscribe(WRISTBAND_TELEMETRY_TOPIC, (topic, message) -> {
                try {
                    byte[] payload = message.getPayload();
                    logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));

                    ArrayList<String> topicParts = new ArrayList<>(Arrays.asList(topic.split("/")));
                    String lastTopicPart = topicParts.get(topicParts.size() - 1);

                    if (HEALTHCARE_TOPIC.equals(lastTopicPart)) {
                        Optional<HealthcareDataDescriptor> receivedHealthcareData = parseTelemetryHealthcareData(payload);

                        if (receivedHealthcareData.isPresent()) {
                            HealthcareDataDescriptor healthcareDataDescriptor = receivedHealthcareData.get();
                            String personWristbandId = topic.replace("wristbands/", "").replace("/telemetry/healthcare", "");
                            personHealthcareDataMap.get(personWristbandId).addHealthcareData(healthcareDataDescriptor);


                            if (personFlagMap.get(personWristbandId).getHealthcareFlag() == false && (healthcareDataDescriptor.getBPM() < MIN_TOLERABLE_BPM_VALUE || healthcareDataDescriptor.getBPM() > MAX_TOLERABLE_BPM_VALUE
                                    || healthcareDataDescriptor.getOxygen() < MIN_TOLERABLE_OXYGEN_VALUE
                                    || healthcareDataDescriptor.getBodyTemperature() < MIN_TOLERABLE_BODY_TEMPERATURE_VALUE || healthcareDataDescriptor.getBodyTemperature() > MAX_TOLERABLE_BODY_TEMPERATURE_VALUE)) {
                                publishJsonFormattedMessage(mqttClient, IRREGULAR_HEALTHCARE_DATA_TOPIC,
                                        new IrregularHealthcareDataMessage(personHealthcareDataMap.get(personWristbandId).getPerson(), healthcareDataDescriptor), false, QOS_2);
                                personFlagMap.get(personWristbandId).setHealthcareFlag(true);
                            }
                        } else {
                            throw new Exception();
                        }
                    } else if (GPS_TOPIC.equals(lastTopicPart)) {
                        Optional<GPSLocationDescriptor> receivedGPSLocation = parseTelemetryGPSLocation(payload);

                        if(receivedGPSLocation.isPresent()) {
                            GPSLocationDescriptor gpsLocationDescriptor = receivedGPSLocation.get();
                            System.out.println(PointXYZUtils.distanceXY(gpsLocationDescriptor.getGPSLocation(),
                                    NURSING_HOUSE_LOCATION.getGPSLocation()));
                            String personWristbandId = topic.replace("wristbands/", "").replace("/telemetry/gps", "");
                            if(personFlagMap.get(personWristbandId).getAlarmFlag() == false && PointXYZUtils.distanceXY(gpsLocationDescriptor.getGPSLocation(),
                                    NURSING_HOUSE_LOCATION.getGPSLocation()) >= MAX_TOLERABLE_WRISTBAND_DISTANCE_METER){
                                String alarmTopic = String.format("%s/%s/%s/%s", BASE_WRISTBAND_TOPIC,
                                        personWristbandId, CONTROL_TOPIC, ALARM_TOPIC);
                                publishJsonFormattedMessage(mqttClient, alarmTopic, new ControlAlarmMessage(true)
                                        , false, QOS_2);
                                personFlagMap.get(personWristbandId).setAlarmFlag(true);
                            }
                        }
                        else{
                            throw new Exception();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mqttClient.subscribe(VIDEOCAMERA_INFO_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try{
                        byte[] payload = message.getPayload();
                        System.out.println("Topic: " + topic + " Payload: " + new String(payload));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            mqttClient.subscribe(VIDEOCAMERA_TELEMETRY_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try{
                        byte[] payload = message.getPayload();
                        System.out.println("Topic: " + topic + " Payload: " + new String(payload));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            logger.error("ERROR");
            e.printStackTrace();
        }
    }

    private static Optional<PersonDataDescriptor> parseInfoPersonalData(byte[] payload) throws JsonProcessingException {
        try {
            if (payload != null) {
                PersonDataMessage personDataMessage = mapper.readValue(new String(payload), PersonDataMessage.class);
                return Optional.ofNullable(new PersonDataDescriptor(personDataMessage.getCF(),
                        personDataMessage.getName(), personDataMessage.getLastname(), personDataMessage.getAge(),
                        personDataMessage.getRoomNumber(), personDataMessage.getWristbandId()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<HealthcareDataDescriptor> parseTelemetryHealthcareData(byte[] payload) {
        try {
            if (payload != null) {
                HealthcareDataMessage healthcareDataMessage = mapper.readValue(new String(payload), HealthcareDataMessage.class);
                return Optional.ofNullable(new HealthcareDataDescriptor(healthcareDataMessage.getBPM(), healthcareDataMessage.getOxygen(), healthcareDataMessage.getBodyTemperature()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<GPSLocationDescriptor> parseTelemetryGPSLocation(byte[] payload) {
        try {
            if (payload != null) {
                GPSLocationMessage gpsLocationMessage = mapper.readValue(new String(payload), GPSLocationMessage.class);
                return Optional.ofNullable(new GPSLocationDescriptor(gpsLocationMessage.getGpsLocation()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static void publishJsonFormattedMessage(IMqttClient mqttClient, String topic, GenericMessage payload,
                                                    boolean retained, int qos) {
        new Thread(() -> {
            try {
                if (mqttClient != null && mqttClient.isConnected() && topic != null && payload != null) {
                    String messagePayload = mapper.writeValueAsString(payload);

                    logger.info("Sending to topic: {} -> Data: {}", topic, messagePayload);

                    MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                    MqttMessage.validateQos(qos);
                    mqttMessage.setQos(qos);

                    mqttClient.publish(topic, mqttMessage);

                    logger.info("Data correctly published to topic: {}", topic);
                } else
                    logger.error("Error: topic or message = Null or MQTT Client is not connected!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
