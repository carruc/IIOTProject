package model.descriptors;

import java.util.Random;

/** Generic descriptor class for actuator/sensor modeling,
 * wrapping it as a Generic value.
 */
abstract public class GenericDescriptor<T> {
    private T value;

    private final Random random = new Random(System.currentTimeMillis());

    public GenericDescriptor() {
        this.value = null;
    }

    public GenericDescriptor(T value) {
        this.value = value;
    }

    public abstract void refreshValue();

    public Random getRandom() {
        return random;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GenericDescriptor{" + "value= " + value + '}';
    }
}
