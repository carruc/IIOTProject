import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.descriptors.wristband.*;
import model.point.PointXYZ;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.ProcessConfiguration;
import resource.GPSSensorResource;
import resource.HealthcareSensorResource;
import utils.SenMLPack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataCollectorManager {
    private final static Logger logger = LoggerFactory.getLogger(DataCollectorManager.class);

    private static final String WRISTBAND_TELEMETRY_TOPIC = "wristband/+/telemetry/+";
    private static final String WRISTBAND_INFO_TOPIC = "wristband/+/info/+";

    private static final Map<String, WristbandDataDescriptor> personData = null;

    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {
        try{
            String clientId = UUID.randomUUID().toString();


            MqttClientPersistence persistence = new MemoryPersistence();

            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d", ProcessConfiguration.MQTT_BROKER_IP,
                    ProcessConfiguration.MQTT_BROKER_PORT), clientId,
                    persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);

            logger.info("CONNECTED");
            System.out.println("Connected");

            mqttClient.subscribe(WRISTBAND_INFO_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    byte[] payload = message.getPayload();
                    System.out.println("Topic: " + topic + " Payload: " + new String(payload));
                    Optional<PersonDataDescriptor> personDataDescriptor = parseInfoPersonalData(message.getPayload());
                }
            });
            mqttClient.subscribe(WRISTBAND_TELEMETRY_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    byte[] payload = message.getPayload();
                    System.out.println("Topic: " + topic + " Payload: " + new String(payload));

                    SenMLPack receivedMessage = mapper.readValue(new String(message.getPayload()), SenMLPack.class);
                    if(receivedMessage != null && receivedMessage.get(0).getBn().equals(HealthcareSensorResource.RESOURCE_TYPE)){
                        Optional<HealthcareDataDescriptor> receivedHealthcareData = parseTelemetryHealthcareData(receivedMessage);
                    }
                    else if(receivedMessage.get(0).getBn().equals(GPSSensorResource.RESOURCE_TYPE)){
                        Optional<GPSLocationDescriptor> receivedGPSLocation = parseTelemetryGPSLocation(receivedMessage);
                    }
                }
            });
        } catch(Exception e){
            logger.error("ERROR");
        }
    }

    private static Optional<PersonDataDescriptor> parseInfoPersonalData(byte[] payload) throws JsonProcessingException {
        try{
            if(payload != null){
                return Optional.ofNullable(mapper.readValue(new String(payload), PersonDataDescriptor.class));
            }
            else{
                return Optional.empty();
            }
        } catch(Exception e){
            return Optional.empty();
        }
    }
    private static Optional<HealthcareDataDescriptor> parseTelemetryHealthcareData(SenMLPack payload){
        return Optional.ofNullable(new HealthcareDataDescriptor(new BPMDescriptor(payload.get(1).getV().doubleValue()),
                new OxygenDescriptor(payload.get(2).getV().doubleValue()),
                new BodyTemperatureDescriptor(payload.get(3).getV().doubleValue())));
    }
    private static Optional<GPSLocationDescriptor> parseTelemetryGPSLocation(SenMLPack payload){
        return Optional.ofNullable(new GPSLocationDescriptor(new PointXYZ(payload.get(1).getV().doubleValue(),
                payload.get(2).getV().doubleValue(), payload.get(3).getV().doubleValue())));
    }

}
