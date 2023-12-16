package device;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.WristbandProcess;
import resource.GenericResource;

import java.util.Map;

public class WristbandSmartObject {
    /*TODO: il wristband è sia un producer che un consumer(allarme)*/

    private static final Logger logger = LoggerFactory.getLogger(WristbandProcess.class);

    private static final String TELEMETRY_TOPIC = "telemetry";

    private String wristbandId;

    private ObjectMapper mapper;

    private IMqttClient mqttClient;

    private Map<String, GenericResource<?>> resourceMap;

    public WristbandSmartObject(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void init(String vehicleId,IMqttClient mqttClient,Map<String,GenericResource<?>> resourceMap){
        this.wristbandId = wristbandId;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap;

        logger.info("Wristband Smart Object correctly created ! Resource Number: {}", resourceMap.keySet().size());
        //il numero di risorse è il numero di elementi della mappa quindi keySet().size()
    }

    /**WristBand behaviour**/

    public void start(){
    }



}
