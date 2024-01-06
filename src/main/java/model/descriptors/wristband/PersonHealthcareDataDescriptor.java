package model.descriptors.wristband;

import java.util.ArrayList;

public class PersonHealthcareDataDescriptor {
    PersonDataDescriptor person;
    ArrayList<HealthcareDataDescriptor> healthcareDataList;

    public PersonHealthcareDataDescriptor(PersonDataDescriptor person) {
        this.person = person;
        this.healthcareDataList = new ArrayList<>();
    }

    public PersonDataDescriptor getPerson() {
        return person;
    }

    public void setPerson(PersonDataDescriptor person) {
        this.person = person;
    }

    public void addHealthcareData(HealthcareDataDescriptor healthcareData){
        healthcareDataList.add(healthcareData);
    }

    public void removeHealthcareData(HealthcareDataDescriptor healthcareData){
        healthcareDataList.remove(healthcareData);
    }
}
