package model;

abstract public class GenericDescriptor<T> {
    private String type;
    private String unit;
    private T value;

    public GenericDescriptor(){

    }

    public GenericDescriptor(String type, String unit, T value) {
        this.type = type;
        this.unit = unit;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract void refreshValue();

    @Override
    public String toString() {
        return "GenericDescriptor{" + "type='" + type + '\'' + ", unit='" + unit + '\'' + ", value=" + value + '}';
    }
}
