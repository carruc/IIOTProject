package model.descriptors.thermostat;


import utils.HvacMode;

public class ThermostatConfigurationDescriptor {

    private double maxTemperature = 16.0;
    private double minTemperature = 10.0;

    //esempio
    private String hvacUnitResourceUri = "coap://127.0.0.1:5683/switch";
    private HvacMode operationalMode;

    public ThermostatConfigurationDescriptor() {
    }

    public ThermostatConfigurationDescriptor(double maxTemperature, double minTemperature, String hvacUnitResourceUri, HvacMode operationalMode) {
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.hvacUnitResourceUri = hvacUnitResourceUri;
        this.operationalMode = operationalMode;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getHvacUnitResourceUri() {
        return hvacUnitResourceUri;
    }

    public void setHvacUnitResourceUri(String hvacUnitResourceUri) {
        this.hvacUnitResourceUri = hvacUnitResourceUri;
    }

    public HvacMode getOperationalMode() {
        return operationalMode;
    }

    public void setOperationalMode(HvacMode operationalMode) {
        this.operationalMode = operationalMode;
    }

    @Override
    public String toString() {
        String sb = "ThermostatConfigurationDescriptor{" + "maxTemperature=" + maxTemperature + ", minTemperature=" + minTemperature + ", hvacUnitResourceUri='" + hvacUnitResourceUri + '\'' + ", operationalMode=" + operationalMode + '}';
        return sb;
    }
}
