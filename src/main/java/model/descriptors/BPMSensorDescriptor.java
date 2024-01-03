package model.descriptors;

import java.util.Random;

/** ECG sensor model for BPM monitoring.
 */
public class BPMSensorDescriptor extends GenericDescriptor<Double>{
    public BPMSensorDescriptor(){
        super(0.0);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return "BPMSensorDescriptor{" + + '}';
    }
}
