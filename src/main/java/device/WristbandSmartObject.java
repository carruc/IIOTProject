package device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import message.*;
import model.descriptors.wristband.AlarmValueDescriptor;
import model.descriptors.wristband.GPSLocationDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ProcessConfiguration;
import resource.*;
import resource.wristband.AlarmActuatorResource;
import resource.wristband.GPSSensorResource;
import resource.wristband.HealthcareSensorResource;
import resource.wristband.PersonDataResource;
import utils.SenMLPack;
import utils.SenMLRecord;

import java.util.Map;
import java.util.Optional;

import static process.ProcessConfiguration.QOS_0;
import static process.ProcessConfiguration.QOS_2;

public class WristbandSmartObject implements GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(WristbandSmartObject.class);

    private static final String BASIC_TOPIC = "wristbands";

    private static final String TELEMETRY_TOPIC = "telemetry";

    private static final String CONTROL_TOPIC = "control";

    private static final String INFO_TOPIC = "info";

    private String wristbandId;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private final ObjectMapper mapper;

    public WristbandSmartObject() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void init(String wristbandId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {
        this.wristbandId = wristbandId;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("Wristband Smart Object correctly created ! Number of hosted resources: {}", resourceMap.keySet().size());
    }

    /** WristBand behaviour **/

    public void start() {
        try {
            if (this.mqttClient != null && this.wristbandId != null && this.wristbandId.length() > 0 && this.resourceMap != null && resourceMap.keySet().size() > 0) {

                logger.info("Starting the wristband");

                registerToAvailableResources();
            }
        } catch (Exception e) {
            logger.error("Error Starting the wristband ! Msg: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void stop() {

    }

    private void registerToAvailableResources() {
        try {
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                if (resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    GenericResource wristbandResource = resourceEntry.getValue();

                    logger.info("Registering to resource {} (id: {}) notifications ...", wristbandResource.getType(), wristbandResource.getId());

                    if (wristbandResource.getType().equals(PersonDataResource.RESOURCE_TYPE)) {
                        try {
                            PersonDataResource personDataResource = (PersonDataResource) wristbandResource;
                            String personalInfoTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, INFO_TOPIC, resourceEntry.getKey());
                            publishJsonFormattedMessage(personalInfoTopic,
                                    new PersonDataMessage(personDataResource.getPersonDataDescriptor()), true, QOS_0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (wristbandResource.getType().equals(HealthcareSensorResource.RESOURCE_TYPE)) {
                        HealthcareSensorResource healthcareSensorResource = (HealthcareSensorResource) wristbandResource;

                        healthcareSensorResource.addDataListener((resource, updatedValue) -> {
                            try {

                                String healthcareTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, TELEMETRY_TOPIC, resourceEntry.getKey());
                                publishJsonFormattedMessage(healthcareTopic,
                                        new HealthcareDataMessage(healthcareSensorResource.getHealthcareData()),
                                    false, QOS_0);
                            } catch (MqttException | JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    if (wristbandResource.getType().equals(GPSSensorResource.RESOURCE_TYPE)) {
                        GPSSensorResource gpsSensorResource = (GPSSensorResource) wristbandResource;

                        gpsSensorResource.addDataListener((resource, updatedValue) -> {
                            try {
                                String gpsTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, TELEMETRY_TOPIC, resourceEntry.getKey());
                                publishJsonFormattedMessage(gpsTopic,
                                        new GPSLocationMessage(gpsSensorResource.getGpsLocationDescriptor()), false,
                                        QOS_0);
                            } catch (MqttException | JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    if (wristbandResource.getType().equals(AlarmActuatorResource.RESOURCE_TYPE)) {
                        try {
                            AlarmActuatorResource alarmActuatorResource = (AlarmActuatorResource) wristbandResource;

                            String alarmTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, CONTROL_TOPIC, resourceEntry.getKey());

                            this.mqttClient.subscribe(alarmTopic, QOS_2, (topic, message) -> {
                                byte[] payload = message.getPayload();
                                Optional<AlarmValueDescriptor> receivedAlarmValue = parseControlAlarmMessage(payload);
                                if (receivedAlarmValue.isPresent()) {
                                    logger.info("New alarm value received: {}", receivedAlarmValue.get().getValue());
                                    alarmActuatorResource.setValue(receivedAlarmValue.get().getValue());
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

    /*private Optional<String> getJsonSenmlHealthcareData(HealthcareSensorResource healthcareSensorResource){
        try{
            SenMLPack senMLPack = new SenMLPack();

            HealthcareDataDescriptor healthcareDataDescriptor = healthcareSensorResource.getHealthcareData();

            SenMLRecord firstRecord = new SenMLRecord();
            firstRecord.setBn(healthcareSensorResource.getType());
            firstRecord.setT(System.currentTimeMillis());

            SenMLRecord BPMRecord = new SenMLRecord();
            BPMRecord.setN("BPM");
            BPMRecord.setV(healthcareDataDescriptor.getBPM());
            BPMRecord.setU(healthcareDataDescriptor.getBPMUnit());

            SenMLRecord oxygenRecord = new SenMLRecord();
            oxygenRecord.setN("oxygen");
            oxygenRecord.setV(healthcareDataDescriptor.getOxygen());
            oxygenRecord.setU(healthcareDataDescriptor.getOxygenUnit());

            SenMLRecord bodyTemperatureRecord = new SenMLRecord();
            bodyTemperatureRecord.setN("body_temperature");
            bodyTemperatureRecord.setV(healthcareDataDescriptor.getBodyTemperature());
            bodyTemperatureRecord.setU(healthcareDataDescriptor.getBodyTemperatureUnit());

            senMLPack.add(firstRecord);
            senMLPack.add(BPMRecord);
            senMLPack.add(oxygenRecord);
            senMLPack.add(bodyTemperatureRecord);

            return Optional.of(mapper.writeValueAsString(senMLPack));
        } catch(Exception e){
            return Optional.empty();
        }
    }*/

    /*
    private Optional<String> getJsonSenmlGPSLocation(GPSSensorResource gpsSensorResource){
        try{
            SenMLPack senMLPack = new SenMLPack();

            GPSLocationDescriptor gpsLocationDescriptor = gpsSensorResource.getGpsLocationDescriptor();

            SenMLRecord firstRecord = new SenMLRecord();
            firstRecord.setBn(gpsSensorResource.getType());
            firstRecord.setBu(gpsLocationDescriptor.getUnit());
            firstRecord.setT(System.currentTimeMillis());

            SenMLRecord latitudeRecord = new SenMLRecord();
            latitudeRecord.setN("latitude");
            latitudeRecord.setV(gpsLocationDescriptor.getGPSLocation().getLatitude());

            SenMLRecord longitudeRecord = new SenMLRecord();
            longitudeRecord.setN("longitude");
            longitudeRecord.setV(gpsLocationDescriptor.getGPSLocation().getLongitude());

            SenMLRecord elevationRecord = new SenMLRecord();
            elevationRecord.setN("elevation");
            elevationRecord.setV(gpsLocationDescriptor.getGPSLocation().getElevation());

            senMLPack.add(firstRecord);
            senMLPack.add(latitudeRecord);
            senMLPack.add(longitudeRecord);
            senMLPack.add(elevationRecord);

            return Optional.of(mapper.writeValueAsString(senMLPack));
        } catch(Exception e){
            return Optional.empty();
        }
    }*/

    private Optional<AlarmValueDescriptor> parseControlAlarmMessage(byte[] payload) {
        try {
            if (payload == null) {
                return Optional.empty();
            }
            ControlAlarmMessage controlAlarmMessage = mapper.readValue(new String(payload), ControlAlarmMessage.class);
            return Optional.ofNullable(new AlarmValueDescriptor(controlAlarmMessage.getAlarmValue()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
