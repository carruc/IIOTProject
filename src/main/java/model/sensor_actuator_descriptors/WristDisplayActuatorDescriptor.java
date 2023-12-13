package model.sensor_actuator_descriptors;

/** Modeling class for the facility's operators' wristband display.
 * Value represents displayed message.
 */
public class WristDisplayActuatorDescriptor extends GenericDescriptor<String> {
    public WristDisplayActuatorDescriptor(String value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
