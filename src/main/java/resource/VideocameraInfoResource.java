package resource;

import model.descriptors.VideocameraInfoDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class VideocameraInfoResource extends GenericResource<VideocameraInfoDescriptor>{
    private static final Logger logger = LoggerFactory.getLogger(VideocameraInfoResource.class);

    public static final String RESOURCE_TYPE = "iot:parameter:camera_info";

    private VideocameraInfoDescriptor videocameraInfoDescriptor;

    public VideocameraInfoResource(VideocameraInfoDescriptor videocameraInfoDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.videocameraInfoDescriptor = videocameraInfoDescriptor;
    }

    public VideocameraInfoDescriptor getVideocameraInfoDescriptor() {
        return videocameraInfoDescriptor;
    }
}
