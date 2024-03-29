import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import message.*;
import model.descriptors.smartdruginventory.SmartDrugInventoryDescriptor;
import model.descriptors.smartdruginventory.SmartDrugSensorDescriptor;
import model.descriptors.smartwatch.SmartWatchAlarmAcknowledgeDescriptor;
import model.descriptors.smartwatch.SmartWatchDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ProcessConfiguration;
import utils.PointXYZUtils;

import java.io.IOException;
import java.util.*;

import static process.ProcessConfiguration.QOS_2;

public class DataCollectorManager {
    private final static Logger logger = LoggerFactory.getLogger(DataCollectorManager.class);

    private static final String VIDEOCAMERA_INFO_TOPIC = "videocameras/+/info/+";
    private static final String VIDEOCAMERA_TELEMETRY_TOPIC = "videocameras/+/telemetry/+";

    private static final String BASE_WRISTBAND_TOPIC = "wristbands";
    private static final String WRISTBAND_TELEMETRY_TOPIC = "wristbands/+/telemetry/+";
    private static final String WRISTBAND_INFO_TOPIC = "wristbands/+/info/+";
    private static final String IRREGULAR_HEALTHCARE_DATA_TOPIC = "irregular_healthcare";
    private static final String HEALTHCARE_TOPIC = "healthcare";
    private static final String GPS_TOPIC = "gps";
    private static final String CONTROL_TOPIC = "control";
    private static final String ALARM_TOPIC = "alarm";

    private static final String BASE_SMARTWATCH_TOPIC = "smartwatches";
    private static final String SMARTWATCH_INFO_TOPIC = "smartwatches/+/info/+";
    private static final String SMARTWATCH_CONTROL_TOPIC = "smartwatches/+/control/+";

    private static final String ACK_TOPIC = "alarm_acknowledge";
    private static final String REQ_TOPIC = "drug_request";
    private static final String DISPLAY_TOPIC = "display_message";

    private static final String BASE_DRUG_INVENTORY_TOPIC = "smartdruginventory";
    private static final String INFO_DRUG_INVENTORY_TOPIC = "smartdruginventory/+/info/+";
    private static final String CONTROL_DRUG_INVENTORY_TOPIC = "smartdruginventory/+/control/+";

    private static final String INV_REQ_TOPIC = "drug_request";
    private static final String INV_SENS_TOPIC = "drug_present";

    private static final Double MAX_TOLERABLE_BPM_VALUE = 100.0;
    private static final Double MAX_TOLERABLE_BODY_TEMPERATURE_VALUE = 38.0;
    private static final Double MIN_TOLERABLE_BPM_VALUE = 55.0;
    private static final Double MIN_TOLERABLE_OXYGEN_VALUE = 90.0;
    private static final Double MIN_TOLERABLE_BODY_TEMPERATURE_VALUE = 35.0;

    private static final GPSLocationDescriptor NURSING_HOUSE_LOCATION = new GPSLocationDescriptor(new PointXYZ(44.64382848606259, 10.914790499876274, 37.27));
    private static final Double MAX_TOLERABLE_WRISTBAND_DISTANCE_METER = 2000.0;

    private static final Map<String, PersonHealthcareDataDescriptor> personHealthcareDataMap = new HashMap<>();
    private static final Map<String, PersonHealthcareAndAlarmFlagDescriptor> personFlagMap = new HashMap<>();
    private static final Map<String, SmartWatchDescriptor> smartWatchMap = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final SmartDrugInventoryDescriptor smartDrugInventoryDescriptor = SmartDrugInventoryDescriptor.getInstance();
    private static final Map<String, String> drugWatchMap = new HashMap<>();

    private static final String THERMOSTAT_ENDPOINT = "coap://127.0.0.1:5684/configuration";

    public static void main(String[] args) {
        runMQTTSubscribers();
    }

    private static void performGetRequest(String uri) throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(THERMOSTAT_ENDPOINT);

        try {
            CoapResponse coapResp;
            coapResp = coapClient.get();

            //Pretty print for the received response
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

            //The "CoapResponse" message contains the response.
            String text = coapResp.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + coapResp.advanced().getMID());
            logger.info("Token: " + coapResp.advanced().getTokenString());
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }

        coapClient.shutdown();
    }

    private static void performPutRequest() throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient(THERMOSTAT_ENDPOINT);

        JsonObject payload = new JsonObject();
        payload.addProperty("min_temperature", 15.0);
        payload.addProperty("max_temperature", 25.0);
        payload.addProperty("hvac_res_uri", "coap://127.0.0.1:5683/switch");
        payload.addProperty("operational_mode", "AUTO");

        CoapResponse response = coapClient.put(payload.toString(), MediaTypeRegistry.APPLICATION_JSON);

        if (response.isSuccess()) {
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(response));
            String text = response.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + response.advanced().getMID());
            logger.info("Token: " + response.advanced().getTokenString());
        } else {
            logger.error("PUT request failed: {}", response.getCode());
        }

        coapClient.shutdown();
    }

    public static void runMQTTSubscribers() {
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

            mqttClient.subscribe(WRISTBAND_INFO_TOPIC, (topic, message) -> {
                try {
                    byte[] payload = message.getPayload();
                    logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));

                    Optional<PersonDataDescriptor> personDataDescriptor = parseInfoPersonalData(message.getPayload());

                    if (personDataDescriptor.isPresent()) {
                        PersonDataDescriptor personData = personDataDescriptor.get();
                        if (!personHealthcareDataMap.containsKey(personData.getWristbandId())) {
                            personHealthcareDataMap.put(personData.getWristbandId(), new PersonHealthcareDataDescriptor(personData));
                        }
                        if (!personFlagMap.containsKey(personData.getWristbandId())) {
                            personFlagMap.put(personData.getWristbandId(), new PersonHealthcareAndAlarmFlagDescriptor());
                        }
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

                            if (!personFlagMap.get(personWristbandId).getHealthcareFlag() && (healthcareDataDescriptor.getBPM() < MIN_TOLERABLE_BPM_VALUE || healthcareDataDescriptor.getBPM() > MAX_TOLERABLE_BPM_VALUE || healthcareDataDescriptor.getOxygen() < MIN_TOLERABLE_OXYGEN_VALUE || healthcareDataDescriptor.getBodyTemperature() < MIN_TOLERABLE_BODY_TEMPERATURE_VALUE || healthcareDataDescriptor.getBodyTemperature() > MAX_TOLERABLE_BODY_TEMPERATURE_VALUE)) {
                                publishJsonFormattedMessage(mqttClient, IRREGULAR_HEALTHCARE_DATA_TOPIC, new IrregularHealthcareDataMessage(personHealthcareDataMap.get(personWristbandId).getPerson(), healthcareDataDescriptor), false, QOS_2);
                                personFlagMap.get(personWristbandId).setHealthcareFlag(true);

                                try {

                                    mqttClient.publish(BASE_SMARTWATCH_TOPIC + "/+/info/" + DISPLAY_TOPIC,
                                            new MqttMessage(new String("Utente: " + personWristbandId + "in " +
                                                    "difficolta' per motivi di salute.").getBytes()));
                                } catch (MqttException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else {
                            throw new Exception();
                        }
                    } else if (GPS_TOPIC.equals(lastTopicPart)) {
                        Optional<GPSLocationDescriptor> receivedGPSLocation = parseTelemetryGPSLocation(payload);

                        if (receivedGPSLocation.isPresent()) {
                            GPSLocationDescriptor gpsLocationDescriptor = receivedGPSLocation.get();
                            //System.out.println(PointXYZUtils.distanceXY(gpsLocationDescriptor.getGPSLocation(),
                            // NURSING_HOUSE_LOCATION.getGPSLocation()));
                            String personWristbandId = topic.replace("wristbands/", "").replace("/telemetry/gps", "");
                            if (!personFlagMap.get(personWristbandId).getAlarmFlag() && PointXYZUtils.distanceXY(gpsLocationDescriptor.getGPSLocation(), NURSING_HOUSE_LOCATION.getGPSLocation()) >= MAX_TOLERABLE_WRISTBAND_DISTANCE_METER) {
                                String alarmTopic = String.format("%s/%s/%s/%s", BASE_WRISTBAND_TOPIC, personWristbandId, CONTROL_TOPIC, ALARM_TOPIC);
                                publishJsonFormattedMessage(mqttClient, alarmTopic, new ControlAlarmMessage(true), false, QOS_2);
                                personFlagMap.get(personWristbandId).setAlarmFlag(true);

                                try {

                                    mqttClient.publish(BASE_SMARTWATCH_TOPIC + "/+/info/" + DISPLAY_TOPIC,
                                            new MqttMessage(new String("Utente: " + personWristbandId + "e' fuori " +
                                                    "dalla zona predefinita.").getBytes()));
                                } catch (MqttException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else {
                            throw new Exception();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mqttClient.subscribe(SMARTWATCH_INFO_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try {
                        byte[] payload = message.getPayload();
                        logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));

                        ArrayList<String> topicParts = new ArrayList<>(Arrays.asList(topic.split("/")));
                        String smartWatchID = topicParts.get(1);
                        String lastTopicPart = topicParts.get(topicParts.size() - 1);

                        if (ACK_TOPIC.equals(lastTopicPart)) {
                            Optional<SmartWatchAlarmAcknowledgeDescriptor> optional = parseAcknowledgeData(payload);

                            if (optional.isPresent()) {
                                SmartWatchAlarmAcknowledgeDescriptor descriptor = optional.get();

                                if (personFlagMap.containsKey(descriptor.getValue())) {
                                    personFlagMap.get(descriptor.getValue()).setAlarmFlag(false);
                                }
                            }
                        }
                        if (REQ_TOPIC.equals(lastTopicPart)) {
                            Optional<SmartWatchDrugRequestDescriptor> optional = parseWatchDrugRequestData(payload);

                            if (optional.isPresent()) {
                                SmartWatchDrugRequestDescriptor descriptor = optional.get();

                                if (!drugWatchMap.containsValue(smartWatchID)) {
                                    try {
                                        mqttClient.publish(BASE_DRUG_INVENTORY_TOPIC + "/" + smartDrugInventoryDescriptor.getSmartDrugInventoryID() + "/control/" + INV_REQ_TOPIC, new MqttMessage(optional.get().getDrugID().getBytes()));
                                    } catch (MqttException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mqttClient.subscribe(INFO_DRUG_INVENTORY_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try {
                        byte[] payload = message.getPayload();
                        logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));

                        ArrayList<String> topicParts = new ArrayList<>(Arrays.asList(topic.split("/")));
                        String drugInventoryID = topicParts.get(1);
                        String lastTopicPart = topicParts.get(topicParts.size() - 1);

                        if (INV_SENS_TOPIC.equals(lastTopicPart)) {
                            Optional<SmartDrugSensorDescriptor> optional = parseInventorySensorData(payload);

                            String drugID = (String) smartDrugInventoryDescriptor.getSmartDrugRequestDescriptor().getValue();

                            if (optional.isPresent()) {
                                SmartDrugSensorDescriptor descriptor = optional.get();

                                try {
                                    mqttClient.publish("smartwatches/" + drugWatchMap.get(drugID) + "/control/" + DISPLAY_TOPIC, new MqttMessage((new String("Il medicinale (" + smartDrugInventoryDescriptor.getDrugMap().get(drugID).getCommercialName() + ")e' " + "pronto.")).getBytes()));
                                } catch (MqttException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            drugWatchMap.remove(drugID);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mqttClient.subscribe(VIDEOCAMERA_INFO_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    try {
                        byte[] payload = message.getPayload();
                        logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mqttClient.subscribe(VIDEOCAMERA_TELEMETRY_TOPIC, new

                    IMqttMessageListener() {
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            try {
                                byte[] payload = message.getPayload();
                                logger.info("Message received at topic: {} Payload:{}", topic, new String(payload));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("ERROR");
            e.printStackTrace();
        }
    }

    private static Optional<SmartDrugSensorDescriptor> parseInventorySensorData(byte[] payload) {
        try {
            if (payload != null) {
                SmartDrugSensorMessage message = mapper.readValue(new String(payload), SmartDrugSensorMessage.class);
                return Optional.ofNullable(new SmartDrugSensorDescriptor(message.getAvailability()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<SmartWatchAlarmAcknowledgeDescriptor> parseAcknowledgeData(byte[] payload) {
        try {
            if (payload != null) {
                SmartWatchAlarmAcknowledgeMessage message = mapper.readValue(new String(payload), SmartWatchAlarmAcknowledgeMessage.class);
                return Optional.ofNullable(new SmartWatchAlarmAcknowledgeDescriptor(message.getAcknowledged()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<SmartWatchDrugRequestDescriptor> parseWatchDrugRequestData(byte[] payload) {
        try {
            if (payload != null) {
                SmartWatchDrugIDMessage message = mapper.readValue(new String(payload), SmartWatchDrugIDMessage.class);
                return Optional.ofNullable(new SmartWatchDrugRequestDescriptor(message.getDrugID()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<PersonDataDescriptor> parseInfoPersonalData(byte[] payload) throws JsonProcessingException {
        try {
            if (payload != null) {
                PersonDataMessage personDataMessage = mapper.readValue(new String(payload), PersonDataMessage.class);
                return Optional.ofNullable(new PersonDataDescriptor(personDataMessage.getCF(), personDataMessage.getName(), personDataMessage.getLastname(), personDataMessage.getAge(), personDataMessage.getRoomNumber(), personDataMessage.getWristbandId()));
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

    private static void publishJsonFormattedMessage(IMqttClient mqttClient, String topic, GenericMessage payload, boolean retained, int qos) {
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
