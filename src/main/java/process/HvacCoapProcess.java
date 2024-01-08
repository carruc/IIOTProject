package process;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.hvac.SwitchActuatorResource;
import resource.hvac.TemperatureSensorResource;
import resource.hvac.coap.CoapSwitchActuatorResource;
import resource.hvac.coap.CoapTemperatureSensorResource;

import java.util.UUID;

/**CoAP Server for Heating Ventilation Air Conditioning (HVAC) System **/
public class HvacCoapProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(HvacCoapProcess.class);

    public HvacCoapProcess() {
        super();
        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());

        CoapResource compressorRootResource = new CoapResource("compressor");
        CoapResource roomRootResource = new CoapResource("room");

        TemperatureSensorResource outdoorTemperatureSensor = new TemperatureSensorResource();
        SwitchActuatorResource compressorSwitchActuator = new SwitchActuatorResource();

        SwitchActuatorResource roomSwitchActuator = new SwitchActuatorResource();

        CoapTemperatureSensorResource outdoorTemperatureResource = new CoapTemperatureSensorResource(deviceId, "temperature",outdoorTemperatureSensor);
        CoapSwitchActuatorResource compressorSwitchResource = new CoapSwitchActuatorResource(deviceId, "switch", compressorSwitchActuator);
        CoapSwitchActuatorResource roomSwitchResource = new CoapSwitchActuatorResource(deviceId, "switch", roomSwitchActuator);

        compressorRootResource.add(outdoorTemperatureResource);
        compressorRootResource.add(compressorSwitchResource);
        roomRootResource.add(roomSwitchResource);
    }

    public static void main(String[] args) {
        HvacCoapProcess hvacCoapProcess = new HvacCoapProcess();
        hvacCoapProcess.start();

        logger.info("Coap Server Started ! Available resources: ");

        hvacCoapProcess.getRoot().getChildren().stream().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if(!resource.getURI().equals("/.well-known")){
                resource.getChildren().stream().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });
    }
}
