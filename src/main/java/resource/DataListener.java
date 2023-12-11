package resource;

//SPOSTA IN RESOURCE
public interface DataListener<T> {
    public void onDataChanged(GenericResource<T> object);
}
