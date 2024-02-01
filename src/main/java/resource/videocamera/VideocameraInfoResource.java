package resource.videocamera;

import model.descriptors.videocamera.VideocameraInfoDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;

import java.util.UUID;

public class VideocameraInfoResource extends GenericResource<VideocameraInfoDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(VideocameraInfoResource.class);

    public static final String RESOURCE_TYPE = "iot:parameter:camera_info";

    private final VideocameraInfoDescriptor videocameraInfoDescriptor;

    public VideocameraInfoResource(VideocameraInfoDescriptor videocameraInfoDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.videocameraInfoDescriptor = videocameraInfoDescriptor;
    }

    public VideocameraInfoDescriptor getVideocameraInfoDescriptor() {
        return videocameraInfoDescriptor;
    }

    @Override
    public VideocameraInfoDescriptor loadUpdatedValue() {
        return null;
    }
}
