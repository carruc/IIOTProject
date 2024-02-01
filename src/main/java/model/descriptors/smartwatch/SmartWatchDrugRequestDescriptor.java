package model.descriptors.smartwatch;

import model.descriptors.GenericDescriptor;

public class SmartWatchDrugRequestDescriptor extends GenericDescriptor {

    public SmartWatchDrugRequestDescriptor(String drugID) {
        super(drugID,"drugID");
    }

    public String getDrugID(){
        return (String) this.getValue();
    }

    public void setDrugID(String drugID){
        this.setValue(drugID);
    }

    @Override
    public String toString() {
        return "SmartWatchDrugRequestDescriptor{" + "drugID=" + this.getValue() + '}';
    }
}
