package resource.wristband;

import model.descriptors.wristband.PersonDataDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.GenericResource;

import java.util.UUID;

public class PersonDataResource extends GenericResource<PersonDataDescriptor> {
    private static final Logger logger = LogManager.getLogger();

    public static final String RESOURCE_TYPE = "iot:parameter:person";

    private PersonDataDescriptor personDataDescriptor;

    public PersonDataResource(PersonDataDescriptor personDataDescriptor){
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.personDataDescriptor = personDataDescriptor;
    }

    public PersonDataResource(String id, String type, PersonDataDescriptor personDataDescriptor){
        super(id, type);
        this.personDataDescriptor = personDataDescriptor;
    }

    public PersonDataDescriptor getPersonDataDescriptor() {
        return personDataDescriptor;
    }
}
