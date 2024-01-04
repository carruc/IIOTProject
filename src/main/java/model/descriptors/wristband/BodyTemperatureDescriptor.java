package model.descriptors.wristband;

public class BodyTemperatureDescriptor {

    public static final Double MIN_BODY_TEMPERATURE = 30.0;
    public static final Double MAX_BODY_TEMPERATURE = 40.0;
    public static final Double MAX_BODY_TEMPERATURE_VARIATION = 1.0;

    private Double bodyTemperature = 35.0;

    public BodyTemperatureDescriptor(){

    }

    public BodyTemperatureDescriptor(Double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public Double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Double bodyTemperature) {
        if(bodyTemperature < MIN_BODY_TEMPERATURE)
            this.bodyTemperature = MIN_BODY_TEMPERATURE;
        if(bodyTemperature > MAX_BODY_TEMPERATURE)
            this.bodyTemperature = MAX_BODY_TEMPERATURE;
        this.bodyTemperature = bodyTemperature;
    }

    @Override
    public String toString() {
        return "BodyTemperatureDescriptor{" + "bodyTemperature=" + bodyTemperature + '}';
    }
}
