package device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.ControlMessage;
import message.TelemetryMessage;
import model.descriptors.wristband.GPSLocationDescriptor;
import model.descriptors.wristband.HealthcareDataDescriptor;
import model.descriptors.wristband.OxygenDescriptor;
import model.descriptors.wristband.PersonDataDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.WristbandProcess;
import resource.*;
import utils.SenMLPack;
import utils.SenMLRecord;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;

public class WristbandSmartObject implements GenericSmartObject{

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static final String BASIC_TOPIC = "wristband";

    private static final String TELEMETRY_TOPIC = "telemetry";

    private static final String CONTROL_TOPIC = "control";

    private static final String INFO_TOPIC = "info";

    private String wristbandId;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private ObjectMapper mapper;

    public WristbandSmartObject() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void init(String wristbandId, IMqttClient mqttClient, Map<String, GenericResource<?>> resourceMap) {
        this.wristbandId = wristbandId;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("Wristband Smart Object correctly created ! Resource Number: {}", resourceMap.keySet().size());
        //il numero di risorse è il numero di elementi della mappa quindi keySet().size()
    }

    /** WristBand behaviour **/

    public void start() {
        try{
            if(this.mqttClient != null &&
               this.wristbandId != null  && this.wristbandId.length() > 0 &&
               this.resourceMap != null && resourceMap.keySet().size() > 0){

                logger.info("Starting the wristband");

                registerToAvailableResources();
            }

        }catch (Exception e){
            logger.error("Error Starting the wristband ! Msg: {}", e.getLocalizedMessage());
        }
    }

    //TODO !!!!
    public void stop(){

    }
    private void registerToAvailableResources(){
        try{
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                if(resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    GenericResource wristbandResource = resourceEntry.getValue();

                    logger.info("Registering to Resource {} (id: {}) notifications ...", wristbandResource.getType(), wristbandResource.getId());

                    if(wristbandResource.getType().equals(PersonDataResource.RESOURCE_TYPE)){
                        try{
                            PersonDataResource personDataResource = (PersonDataResource) wristbandResource;

                            String personalInfoTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, INFO_TOPIC, resourceEntry.getKey());
                            publishInfoPersonalData(personalInfoTopic, personDataResource.getPersonDataDescriptor());
                        } catch (Exception e){
                            logger.error("ERROR");
                        }

                    }
                    if (wristbandResource.getType().equals(HealthcareSensorResource.RESOURCE_TYPE)) {
                        HealthcareSensorResource healthcareSensorResource = (HealthcareSensorResource) wristbandResource;

                        healthcareSensorResource.addDataListener(new ResourceDataListener<HealthcareDataDescriptor>() {
                            @Override
                            public void onDataChanged(GenericResource<HealthcareDataDescriptor> resource, HealthcareDataDescriptor updatedValue) {
                                try {

                                    String healthcareTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, TELEMETRY_TOPIC, resourceEntry.getKey());
                                    Optional<String> healthcarePayload = getJsonSenmlHealthcareData(healthcareSensorResource);
                                    publishTelemetryData(healthcareTopic, healthcarePayload);
                                } catch (MqttException | JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (wristbandResource.getType().equals(GPSSensorResource.RESOURCE_TYPE)) {
                        GPSSensorResource gpsSensorResource = (GPSSensorResource) wristbandResource;

                        gpsSensorResource.addDataListener(new ResourceDataListener<GPSLocationDescriptor>() {
                            @Override
                            public void onDataChanged(GenericResource<GPSLocationDescriptor> resource,
                                                      GPSLocationDescriptor updatedValue) {
                                try {
                                    String gpsTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, TELEMETRY_TOPIC, resourceEntry.getKey());
                                    Optional<String> gpsPayload = getJsonSenmlGPSLocation(gpsSensorResource);
                                    publishTelemetryData(gpsTopic, gpsPayload);
                                } catch (MqttException | JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if(wristbandResource.getType().equals(AlarmActuatorResource.RESOURCE_TYPE)){
                        try{
                            AlarmActuatorResource alarmActuatorResource = (AlarmActuatorResource) wristbandResource;

                            String alarmTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, CONTROL_TOPIC,
                                    resourceEntry.getKey());

                            this.mqttClient.subscribe(alarmTopic, new IMqttMessageListener() {
                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                    Optional<Boolean> receivedAlarmValue = parseControlAlarmMessage(message);
                                    if(receivedAlarmValue.isPresent()){
                                        logger.info("New alarm message arrived");
                                        alarmActuatorResource.setValue(receivedAlarmValue.get());
                                    }
                                    else{
                                        logger.error("ERROR");
                                    }
                                }
                            });
                        } catch (Exception e){
                            logger.error("ERROR");
                        }
                    }
                }
            });
        } catch (Exception e){
            logger.error("Error Registering to Resource ! Msg: {}", e.getLocalizedMessage());
        }
    }


    private void publishInfoPersonalData(String topic, PersonDataDescriptor personDataDescriptor) throws MqttException, JsonProcessingException {
        if(this.mqttClient != null && this.mqttClient.isConnected() && topic != null && personDataDescriptor != null){
            String messagePayload = mapper.writeValueAsString(personDataDescriptor);

            logger.info("Sending to topic: {} -> Data: {}", topic, messagePayload);
            System.out.println("Topic: " + topic + " Payload: " + messagePayload);

            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);
            mqttMessage.setRetained(true);

            mqttClient.publish(topic, mqttMessage);

            logger.info("Data Correctly Published to topic: {}", topic);

        }
        else
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
    }

    private void publishTelemetryData(String topic, Optional<String> payload) throws MqttException,
            JsonProcessingException {

        if(this.mqttClient != null && this.mqttClient.isConnected() && payload.isPresent() && topic != null){
            String messagePayload = payload.get();

            logger.info("Sending to topic: {} -> Data: {}", topic, messagePayload);
            System.out.println("Topic: " + topic + " Payload: " + messagePayload);

            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);

            mqttClient.publish(topic, mqttMessage);

            logger.info("Data Correctly Published to topic: {}", topic);

        }
        else
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
    }

    private Optional<String> getJsonSenmlHealthcareData(HealthcareSensorResource healthcareSensorResource){
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
    }

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
    }

    private Optional<Boolean> parseControlAlarmMessage(MqttMessage mqttMessage){
        try{
            if(mqttMessage == null){
                return Optional.empty();
            }
            String payloadString = new String(mqttMessage.getPayload());

            SenMLPack receivedPack = mapper.readValue(payloadString, SenMLPack.class);

            return Optional.ofNullable(receivedPack.get(0).getVb());
        } catch(Exception e){
            return Optional.empty();
        }
    }
}
