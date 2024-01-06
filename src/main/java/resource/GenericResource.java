package resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericResource<T> {
    private static final Logger logger = LogManager.getLogger();

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
        if (this.resourceDataListeners != null && this.resourceDataListeners.contains(resourceDataListener))
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

    @Override
    public String toString() {
        return "GenericDescriptor{" + "type='" + type + '\'' + '}';
    }
}
