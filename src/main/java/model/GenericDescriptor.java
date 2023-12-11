package model;

abstract public class GenericDescriptor<T> {
    private T value;

    public GenericDescriptor(){

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

    public abstract void refreshValue();

    @Override
    public String toString() {
        return "GenericDescriptor{" + "value= " + value + '}';
    }
}