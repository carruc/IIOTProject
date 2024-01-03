package model.generators;

import model.descriptors.GenericDescriptor;

import java.util.Random;

public abstract class GenericGenerator {

    private final Random random = new Random(System.currentTimeMillis());

    private final GenericDescriptor genericDescriptor;

    public GenericGenerator(GenericDescriptor genericDescriptor){
        this.genericDescriptor = genericDescriptor;
    }

    public GenericDescriptor getGenericDescriptor() {
        return genericDescriptor;
    }

    public Random getRandom() {
        return random;
    }

    public abstract void refreshValue();
}
