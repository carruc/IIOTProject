package message;

public class SmartWatchDrugIDMessage extends GenericMessage{

    private String drugID;

    public SmartWatchDrugIDMessage(String drugID) {
        this.drugID = drugID;
    }

    public String getDrugID() {
        return drugID;
    }

    public void setDrugID(String drugID) {
        this.drugID = drugID;
    }
}
