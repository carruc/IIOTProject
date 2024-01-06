package message;

import model.descriptors.wristband.HealthcareDataDescriptor;

public class HealthcareDataMessage extends GenericMessage{
    private Double BPM;
    private Double oxygen;
    private Double bodyTemperature;

    public HealthcareDataMessage(){

    }
    public HealthcareDataMessage(Double BPM, Double oxygen, Double bodyTemperature) {
        this.BPM = BPM;
        this.oxygen = oxygen;
        this.bodyTemperature = bodyTemperature;
    }
    public HealthcareDataMessage(HealthcareDataDescriptor healthcareDataDescriptor){
        this(healthcareDataDescriptor.getBPM(), healthcareDataDescriptor.getOxygen(), healthcareDataDescriptor.getBodyTemperature());
    }

    public Double getBPM() {
        return BPM;
    }

    public void setBPM(Double BPM) {
        this.BPM = BPM;
    }

    public Double getOxygen() {
        return oxygen;
    }

    public void setOxygen(Double oxygen) {
        this.oxygen = oxygen;
    }

    public Double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }
}
