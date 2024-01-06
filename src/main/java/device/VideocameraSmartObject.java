package device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VideocameraSmartObject {

    /*TODO: la videocamera è un producer che pubblica i dati*/

    private static final Logger logger = LogManager.getLogger();

    private static String BROKER_URL = "tcp://broker.emqx.io:1883";     //sito del broker: https://www.emqx.com/en/mqtt/public-mqtt5-broker

    private static final String DEVICE_TOPIC = "Videocamera";

    //Info Sub-Topic used to publish device information
    private static final String DEVICE_INFO_TOPIC = "caminfo";

    private static final String SENSOR_TOPIC = "sensor/videocamera";    //puo essere migliorato








}
