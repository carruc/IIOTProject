package model.descriptors.smartdruginventory;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class InventoryDescriptor {
    private final Map<String, Point2D.Double> drugIDMap;

    public InventoryDescriptor() {
        this.drugIDMap = new HashMap<>();
    }

    public Map<String, Point2D.Double> getDrugIDMap() {
        return drugIDMap;
    }

    public Point2D.Double getDrugPoint(String drugID){
        return this.drugIDMap.get(drugID);
    }

    public void addDrug(DrugDescriptor drugDescriptor, Point2D.Double point) {
        if (!this.drugIDMap.containsKey(drugDescriptor.getId())) {
            this.drugIDMap.put(drugDescriptor.getId(), point);
        }
    }

    public void updateDrug(DrugDescriptor drugDescriptor, Point2D.Double newPoint) {
        if (!this.drugIDMap.get(drugDescriptor.getId()).equals(newPoint)) {
            this.drugIDMap.put(drugDescriptor.getId(), newPoint);
        }
    }

    public void removeDrug(DrugDescriptor drugDescriptor){
        this.drugIDMap.remove(drugDescriptor.getId());
    }
}
