package model.descriptors.wristband;

import model.descriptors.GenericDescriptor;

/** ECG sensor model for BPM monitoring.
 */
public class BPMDescriptor extends GenericDescriptor<Double> {

    public static final Double MIN_BPM = 20.0;
    public static final Double MAX_BPM = 180.0;
    public static final Double MAX_BPM_VARIATION = 3.0;

    public static final Double DEFAULT_BPM = 75.0;

    public static final String BPM_UNIT = "bpm";
    public BPMDescriptor(){
        super(DEFAULT_BPM, BPM_UNIT);
    }

    public BPMDescriptor(Double BPM){
        super(BPM, BPM_UNIT);
    }

    public Double getBPM() {
        return getValue();
    }

    public void setBPM(Double BPM) {
        if(BPM < MIN_BPM)
            setValue(MIN_BPM);
        if(BPM > MAX_BPM)
            setValue(MAX_BPM);
        setValue(BPM);
    }

    @Override
    public String toString() {
        return "BPMDescriptor{" + "value=" + getBPM() + ", unit='" + getUnit() + '\'' + '}';
    }
}
