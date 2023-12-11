package model;

/**
 * Simple Devie Description and POJO
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public abstract class DeviceDescriptor {

    private String deviceId;

    private String producer;

    private String softwareVersion;

    public DeviceDescriptor() {
        super();
    }

    public DeviceDescriptor(String deviceId, String producer, String softwareVersion) {
        this.deviceId = deviceId;
        this.producer = producer;
        this.softwareVersion = softwareVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    @Override
    public String toString() {
        return "DeviceDescriptor [deviceId=" + deviceId + ", producer=" + producer + ", softwareVersion="
                + softwareVersion + "]";
    }

}
