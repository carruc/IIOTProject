package resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//SPOSTA IN RESOURCE
public abstract class GenericResource<T> {

    private static final Logger logger = LoggerFactory.getLogger(GenericResource.class);

    protected List<DataListener<T>> dataListeners;

    private String type;

    public GenericResource() {
        this.dataListeners = new ArrayList<>();
    }

    public abstract T loadUpdatedValue();

    public void addDataListener(DataListener<T> resourceDataListener){
        if(this.dataListeners != null)
            this.dataListeners.add(resourceDataListener);
    }

    public void removeDataListener(DataListener<T> resourceDataListener){
        if(this.dataListeners != null && this.dataListeners.contains(resourceDataListener))
            this.dataListeners.remove(resourceDataListener);
    }

    protected void notifyUpdate(T updatedValue){
        if(this.dataListeners != null && this.dataListeners.size() > 0)
            this.dataListeners.forEach(resourceDataListener -> {
                if(resourceDataListener != null)
                    resourceDataListener.onDataChanged(this);
            });
        else
            logger.info("Nothing to notify. The list of listeners is null or empty!");
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
