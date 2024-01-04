package model.descriptors;

import java.util.Random;

/** ECG sensor model for BPM monitoring.
 */
public class BPMSensorDescriptor extends GenericDescriptor<Double>{

    private final Double MAX_BPM = 180.0;
    private final Double MIN_BPM = 40.0;
    public BPMSensorDescriptor(){
        super(0.0);
    }

    @Override
    public void refreshValue() {
        this.setValue(MIN_BPM + this.getRandom().nextDouble() * (MAX_BPM - MIN_BPM));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return "BPMSensorDescriptor{" + + '}';
    }
}
