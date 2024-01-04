package model.descriptors.wristband;

/** ECG sensor model for BPM monitoring.
 */
public class BPMDescriptor{

    public static final Double MIN_BPM = 40.0;
    public static final Double MAX_BPM = 180.0;
    public static final Double MAX_BPM_VARIATION = 5.0;

    private Double BPM = 80.0;
    public BPMDescriptor(){

    }

    public BPMDescriptor(Double BPM){
        this.BPM = BPM;
    }

    public Double getBPM() {
        return BPM;
    }

    public void setBPM(Double BPM) {
        if(BPM < MIN_BPM)
            this.BPM = MIN_BPM;
        if(BPM > MAX_BPM)
            this.BPM = MAX_BPM;
        this.BPM = BPM;
    }

    @Override
    public String toString() {
        return "BPMDescriptor{" + "BPM=" + BPM + '}';
    }
}
