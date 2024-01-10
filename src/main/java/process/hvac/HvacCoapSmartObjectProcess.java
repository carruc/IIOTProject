package process.hvac;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import resource.hvac.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class HvacCoapSmartObjectProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(HvacCoapSmartObjectProcess.class);

    public HvacCoapSmartObjectProcess() {
        super();

        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());

        CoapResource RoomRootResource = new CoapResource("room147");

        SwitchActuatorResource RoomSwitchActuatorResource = new SwitchActuatorResource();

        CoapSwitchActuatorResource SwitchResource = new CoapSwitchActuatorResource(deviceId, "switch", RoomSwitchActuatorResource);

        RoomRootResource.add(SwitchResource);

        this.add(RoomRootResource);
    }

    public static void main(String[] args) {
        HvacCoapSmartObjectProcess hvacCoapSmartObjectProcess = new HvacCoapSmartObjectProcess();
        hvacCoapSmartObjectProcess.start();

        logger.info("Coap Server Started! Available resources: ");

        hvacCoapSmartObjectProcess.getRoot().getChildren().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if (!resource.getURI().equals("/.well-known")) {
                resource.getChildren().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });
    }
}

