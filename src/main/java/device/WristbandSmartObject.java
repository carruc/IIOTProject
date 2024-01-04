package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.ControlMessage;
import message.TelemetryMessage;
import model.descriptors.wristband.GPSLocationDescriptor;
import model.descriptors.wristband.HealthcareDataDescriptor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.WristbandProcess;
import resource.*;

import java.util.Map;
import java.util.Optional;

public class WristbandSmartObject implements GenericSmartObject{

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static final String BASIC_TOPIC = "wristband";

    private static final String TELEMETRY_TOPIC = "telemetry";

    private static final String CONTROL_TOPIC = "control";

    private String wristbandId;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    private ObjectMapper mapper;

    public WristbandSmartObject() {
        this.mapper = new ObjectMapper();
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

                    if (wristbandResource.getType().equals(HealthcareSensorResource.RESOURCE_TYPE)) {
                        HealthcareSensorResource healthcareSensorResource = (HealthcareSensorResource) wristbandResource;

                        healthcareSensorResource.addDataListener(new ResourceDataListener<HealthcareDataDescriptor>() {
                            @Override
                            public void onDataChanged(GenericResource<HealthcareDataDescriptor> resource, HealthcareDataDescriptor updatedValue) {
                                try {

                                    String healthcareTopic = String.format("%s/%s/%s/%s", BASIC_TOPIC, wristbandId, TELEMETRY_TOPIC, resourceEntry.getKey());

                                    publishTelemetryData(healthcareTopic,
                                            new TelemetryMessage<>(healthcareSensorResource.getType(), updatedValue));
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
                                    publishTelemetryData(gpsTopic, new TelemetryMessage<>(gpsSensorResource.getType()
                                            , updatedValue.getGPSLocation()));
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
                                    Optional<ControlMessage<Boolean>> controlMessageOptional =
                                            parseControlAlarmMessage(message);
                                    if(controlMessageOptional.isPresent() && controlMessageOptional.get().getType().equals(AlarmActuatorResource.RESOURCE_TYPE)){
                                        logger.info("New alarm message arrived");
                                        Boolean newAlarmValue = controlMessageOptional.get().getData();
                                        alarmActuatorResource.setValue(newAlarmValue);
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


    private void publishTelemetryData(String topic, TelemetryMessage<?> telemetryMessage) throws MqttException,
            JsonProcessingException {
        logger.info("Sending to topic: {} -> Data: {}", topic, telemetryMessage);

        if(this.mqttClient != null && this.mqttClient.isConnected() && telemetryMessage != null && topic != null){

            String messagePayload = mapper.writeValueAsString(telemetryMessage);

            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);

            mqttClient.publish(topic, mqttMessage);

            logger.info("Data Correctly Published to topic: {}", topic);

        }
        else
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
    }

    private Optional<ControlMessage<Boolean>> parseControlAlarmMessage(MqttMessage mqttMessage){
        try{
            if(mqttMessage == null){
                return Optional.empty();
            }
            String payloadString = new String(mqttMessage.getPayload());
            return Optional.ofNullable(mapper.readValue(payloadString,
                    new TypeReference<ControlMessage<Boolean>>() {}));
        } catch(Exception e){
            return Optional.empty();
        }
    }
}
