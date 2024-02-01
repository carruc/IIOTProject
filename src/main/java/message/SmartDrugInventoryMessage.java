package message;

import model.descriptors.smartdruginventory.DrugDescriptor;

public class SmartDrugInventoryMessage extends GenericMessage{

    private String message;

    private final DrugDescriptor drug;

    public SmartDrugInventoryMessage(DrugDescriptor drug){
        this.drug = drug;
    }
    public SmartDrugInventoryMessage(DrugDescriptor drug, Boolean available) {
        this.drug = drug;
        if(available){
            setAvailable();
        }else{
            setUnavailable();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setUnavailable(){
        this.message = "Drug \"" + drug.getCommercialName() +"\" currently not available or under-stocked.}";
    }

    public void setAvailable(){
        this.message = "Drug \"" + drug.getCommercialName() +"\" available at Smart Drug Inventory.}";
    }
}
