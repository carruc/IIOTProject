package model.descriptors.smartdruginventory;

public class DrugDescriptor {

    private final int MAX_QUANTITY = 255;
    private final int MIN_QUANTITY = 10;

    private final String commercialName;

    private final String id;

    private int quantity;

    public DrugDescriptor(String commercialName, String id, int quantity) {
        this.commercialName = commercialName;
        this.id = id;
        this.quantity = quantity;
    }

    public String getCommercialName() {
        return commercialName;
    }
    public String getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Boolean get(){
        if((this.quantity - 1) >= MIN_QUANTITY){
            this.quantity = this.quantity - 1;
            return true;
        }else{
            return false;
        }
    }

    public Boolean put(int quantity){
        if((this.quantity + quantity) <= MAX_QUANTITY){
            this.quantity = this.quantity + quantity;
            return true;
        }else{
            return false;
        }
    }

    public Boolean isAvailable(){
        return quantity > MIN_QUANTITY;
    }

}
