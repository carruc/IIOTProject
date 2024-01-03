package model.descriptors;

/** Generic descriptor class for actuator/sensor modeling,
 * wrapping it as a Generic value.
 */
abstract public class GenericDescriptor<T> {
    private T value;

    public GenericDescriptor() {
        this.value = null;
    }

    public GenericDescriptor(T value) {
        this.value = value;
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
