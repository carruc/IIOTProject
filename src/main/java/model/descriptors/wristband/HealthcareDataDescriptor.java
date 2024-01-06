package model.descriptors.wristband;

/** Wrapper class for Healthcare data.
 */

public class HealthcareDataDescriptor {

    private BPMDescriptor BPMDescriptor;
    private OxygenDescriptor oxygenDescriptor;
    private BodyTemperatureDescriptor bodyTemperatureDescriptor;

    public HealthcareDataDescriptor(){
        this.BPMDescriptor = new BPMDescriptor();
        this.oxygenDescriptor = new OxygenDescriptor();
        this.bodyTemperatureDescriptor = new BodyTemperatureDescriptor();
    }

    public HealthcareDataDescriptor(BPMDescriptor BPMDescriptor, OxygenDescriptor oxygenDescriptor, BodyTemperatureDescriptor bodyTemperatureDescriptor) {
        this.BPMDescriptor = BPMDescriptor;
        this.oxygenDescriptor = oxygenDescriptor;
        this.bodyTemperatureDescriptor = bodyTemperatureDescriptor;
    }

    public HealthcareDataDescriptor(Double BPM, Double oxygen, Double bodyTemperature) {
        this.BPMDescriptor = new BPMDescriptor(BPM);
        this.oxygenDescriptor = new OxygenDescriptor(oxygen);
        this.bodyTemperatureDescriptor = new BodyTemperatureDescriptor(bodyTemperature);
    }

    public Double getBPM() {
        return BPMDescriptor.getBPM();
    }

    public void setBPM(Double BPM) {
        BPMDescriptor.setBPM(BPM);
    }

    public Double getOxygen() {
        return oxygenDescriptor.getOxygen();
    }

    public void setOxygen(Double oxygen) {
        oxygenDescriptor.setOxygen(oxygen);
    }

    public Double getBodyTemperature() {
        return bodyTemperatureDescriptor.getBodyTemperature();
    }
    public void setBodyTemperature(Double bodyTemperature) {
        bodyTemperatureDescriptor.setBodyTemperature(bodyTemperature);
    }

    /*public String getBPMUnit() {
        return BPMDescriptor.getUnit();
    }

    public String getOxygenUnit() {
        return oxygenDescriptor.getUnit();
    }

    public String getBodyTemperatureUnit() {
        return bodyTemperatureDescriptor.getUnit();
    }*/

    @Override
    public String toString() {
        return "HealthcareDataDescriptor{" + "BPM=" + BPMDescriptor.getBPM() + ", oxygen=" + oxygenDescriptor.getOxygen() + ", " +
               "bodyTemperature=" + bodyTemperatureDescriptor.getBodyTemperature() + '}';
    }
}
