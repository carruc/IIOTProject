package message;

public class SmartDrugSensorMessage extends GenericMessage{
    private Boolean available;

    public SmartDrugSensorMessage(Boolean available) {
        this.available = available;
    }

    public Boolean getAvailability() {
        return available;
    }

    public void setAvailability(Boolean available) {
        this.available = available;
    }
}
