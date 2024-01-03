package model.generators;

import model.descriptors.GenericDescriptor;

public class BPMSensorGenerator extends GenericGenerator{

    private final Double MAX_VALUE = 180.0;
    private final Double MIN_VALUE = 40.0;

    public BPMSensorGenerator(GenericDescriptor genericDescriptor) {
        super(genericDescriptor);
    }

    @Override
    public void refreshValue() {
        this.getGenericDescriptor().setValue(MIN_VALUE + this.getRandom().nextDouble() * (MAX_VALUE - MIN_VALUE));
    }
}
