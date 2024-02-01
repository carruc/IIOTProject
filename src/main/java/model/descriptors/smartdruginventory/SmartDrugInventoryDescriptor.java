package model.descriptors.smartdruginventory;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartDrugInventoryDescriptor {

    private static SmartDrugInventoryDescriptor smartDrugInventoryDescriptor = null;
    private static InventoryDescriptor inventoryDescriptor;

    private CartesianMotorActuatorDescriptor cartesianMotorActuatorDescriptor = new CartesianMotorActuatorDescriptor();
    private final SmartDrugSensorDescriptor smartDrugSensorDescriptor = new SmartDrugSensorDescriptor(false);
    private final SmartDrugRequestDescriptor smartDrugRequestDescriptor = new SmartDrugRequestDescriptor("0");
    private final String smartDrugInventoryID = "smartinvetory1";

    private final Map<String, DrugDescriptor> drugMap = new HashMap<>();

    public static SmartDrugInventoryDescriptor getInstance(){
        if(smartDrugInventoryDescriptor == null){
            smartDrugInventoryDescriptor = new SmartDrugInventoryDescriptor();
            inventoryDescriptor = new InventoryDescriptor();

            List<DrugDescriptor> drugDescriptors = new ArrayList<>(List.of(new DrugDescriptor[]{
                    new DrugDescriptor("Tachipirina", "123", 19),
                    new DrugDescriptor("Flomax", "111", 12),
                    new DrugDescriptor("Valium", "004", 100)
            }));

            for(int i = 0; i < 3; ++i){
                inventoryDescriptor.addDrug(drugDescriptors.get(i), new Point2D.Double(i, i));
            }

        }
        return smartDrugInventoryDescriptor;
    }

    public Boolean getDrug(String drugID) {
        DrugDescriptor drug = this.drugMap.get(drugID);
        if (!this.cartesianMotorActuatorDescriptor.isIdle() || !this.drugMap.containsKey(drugID) || !drug.isAvailable()) {
            return false;
        }

        this.smartDrugRequestDescriptor.setValue(drugID);

        drug.get();
        this.cartesianMotorActuatorDescriptor.to(this.inventoryDescriptor.getDrugPoint(drugID));
        this.cartesianMotorActuatorDescriptor.to(this.cartesianMotorActuatorDescriptor.getStartingPoint());
        this.smartDrugSensorDescriptor.setValue(true);
        return true;
    }

    public Boolean getDrug() {
        return getDrug((String) smartDrugRequestDescriptor.getValue());
    }

    public Boolean isAvailable(){
        return this.cartesianMotorActuatorDescriptor.isIdle();
    }

    public SmartDrugRequestDescriptor getSmartDrugRequestDescriptor() {
        return smartDrugRequestDescriptor;
    }

    public void setSmartDrugRequestDescriptor(SmartDrugRequestDescriptor smartDrugRequestDescriptor) {
        this.smartDrugRequestDescriptor.setValue(smartDrugRequestDescriptor.getValue());
    }

    public Boolean addDrug(DrugDescriptor drug, Point2D.Double point) {
        if (this.drugMap.containsKey(drug.getId())) {
            return false;
        }

        this.drugMap.put(drug.getId(), drug);
        this.inventoryDescriptor.addDrug(drug, point);
        return true;
    }

    public Map<String, DrugDescriptor> getDrugMap() {
        return drugMap;
    }

    public SmartDrugSensorDescriptor getSmartDrugSensorDescriptor() {
        return smartDrugSensorDescriptor;
    }

    public InventoryDescriptor getInventoryDescriptor() {
        return inventoryDescriptor;
    }

    public String getSmartDrugInventoryID() {
        return smartDrugInventoryID;
    }

    public Boolean getSensor() {
        return this.smartDrugSensorDescriptor.getValue();
    }

    public String getValue() {
        return (String) this.smartDrugRequestDescriptor.getValue();
    }
}
