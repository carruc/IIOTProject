package message;

import model.descriptors.wristband.HealthcareDataDescriptor;
import model.descriptors.wristband.PersonDataDescriptor;

public class IrregularHealthcareDataMessage extends GenericMessage{
    private PersonDataDescriptor person;
    private HealthcareDataDescriptor healthcareData;

    public IrregularHealthcareDataMessage(){
        super();
    }

    public IrregularHealthcareDataMessage(PersonDataDescriptor person, HealthcareDataDescriptor healthcareData) {
        super();
        this.person = person;
        this.healthcareData = healthcareData;
    }

    public PersonDataDescriptor getPerson() {
        return person;
    }

    public void setPerson(PersonDataDescriptor person) {
        this.person = person;
    }

    public HealthcareDataDescriptor getHealthcareData() {
        return healthcareData;
    }

    public void setHealthcareData(HealthcareDataDescriptor healthcareData) {
        this.healthcareData = healthcareData;
    }
}
