package resource;

public interface ResourceDataListener<T> {
    void onDataChanged(GenericResource<T> resource, T updatedValue);
}
