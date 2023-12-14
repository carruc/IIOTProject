package model.descriptors;

import java.util.Random;

/** ECG sensor model for BPM monitoring.
 */
public class BPMSensorDescriptor extends GenericDescriptor<Double>{

    public BPMSensorDescriptor(){
        super(new Random().nextDouble() * 150);
    }

    @Override
    public void refreshValue() {

    }
}
