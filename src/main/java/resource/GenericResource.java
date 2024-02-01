package resource;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericResource<T> {
    private static final Logger logger = LoggerFactory.getLogger(GenericResource.class);

    protected List<ResourceDataListener<T>> resourceDataListeners;

    private String id;

    private String type;

    public GenericResource() {
        this.resourceDataListeners = new ArrayList<>();
    }

    public GenericResource(String id, String type) {
        this.id = id;
        this.type = type;
        this.resourceDataListeners = new ArrayList<>();
    }

    public void addDataListener(ResourceDataListener<T> resourceDataListener) {
        if (this.resourceDataListeners != null)
            this.resourceDataListeners.add(resourceDataListener);
    }

    public void removeDataListener(ResourceDataListener<T> resourceDataListener) {
        if (this.resourceDataListeners != null)
            this.resourceDataListeners.remove(resourceDataListener);
    }

    protected void notifyUpdate(T updatedValue) {
        if (this.resourceDataListeners != null && this.resourceDataListeners.size() > 0) {
            this.resourceDataListeners.forEach(resourceResourceDataListener -> {
                if (resourceResourceDataListener != null)
                    resourceResourceDataListener.onDataChanged(this, updatedValue);
            });
        } else {
            logger.info("Nothing to notify. The list of listeners is null or empty!");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public abstract T loadUpdatedValue();

    @Override
    public String toString() {
        return "GenericDescriptor{" + "type='" + type + '\'' + '}';
    }
}
