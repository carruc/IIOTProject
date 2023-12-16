package model.descriptors;

import java.util.Random;

/** ECG sensor model for BPM monitoring.
 */
public class BPMSensorDescriptor extends GenericDescriptor<Double>{
    private Random random = new Random(System.currentTimeMillis());
    public BPMSensorDescriptor(){
        super(0.0);
        refreshValue();
    }

    @Override
    public void refreshValue() {
        setValue(random.nextDouble() * 150);
    }
}
