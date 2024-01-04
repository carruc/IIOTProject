package model.descriptors;

import java.util.Random;

/** Generic descriptor class for actuator/sensor modeling,
 * wrapping it as a Generic value.
 */
abstract public class GenericDescriptor<T> {
    private T value;

    private String unit;

    public GenericDescriptor(T value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "GenericDescriptor{" + "value=" + value + ", unit='" + unit + '\'' + '}';
    }
}
