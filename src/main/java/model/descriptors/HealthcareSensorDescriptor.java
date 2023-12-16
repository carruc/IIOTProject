package model.descriptors;

/** Wrapper class for Healthcare related descriptors.
 */

public class HealthcareSensorDescriptor {
    BPMSensorDescriptor bpmSensor;
    OxygenSensorDescriptor oxygenSensor;
    WristTemperatureSensorDescriptor wristTemperatureSensor;

    public HealthcareSensorDescriptor(BPMSensorDescriptor bpmSensor, OxygenSensorDescriptor oxygenSensor, WristTemperatureSensorDescriptor wristTemperatureSensor) {
        this.bpmSensor = bpmSensor;
        this.oxygenSensor = oxygenSensor;
        this.wristTemperatureSensor = wristTemperatureSensor;
    }

    public BPMSensorDescriptor getBpmSensor() {
        return bpmSensor;
    }

    public void setBpmSensor(BPMSensorDescriptor bpmSensor) {
        this.bpmSensor = bpmSensor;
    }

    public OxygenSensorDescriptor getOxygenSensor() {
        return oxygenSensor;
    }

    public void setOxygenSensor(OxygenSensorDescriptor oxygenSensor) {
        this.oxygenSensor = oxygenSensor;
    }

    public WristTemperatureSensorDescriptor getWristTemperatureSensor() {
        return wristTemperatureSensor;
    }

    public void setWristTemperatureSensor(WristTemperatureSensorDescriptor wristTemperatureSensor) {
        this.wristTemperatureSensor = wristTemperatureSensor;
    }

    @Override
    public String toString() {
        return "WristbandDescriptor{" + "bpmSensor=" + bpmSensor + ", oxygenSensor=" + oxygenSensor + ", wristTemperatureSensor=" + wristTemperatureSensor + '}';
    }
}