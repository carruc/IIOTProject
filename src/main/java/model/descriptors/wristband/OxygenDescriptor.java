package model.descriptors.wristband;

/** Model for an O2 level sensor.
 * Value represents a percentage (0-100).
 */

public class OxygenDescriptor {
    public static final Double MIN_OXYGEN = 60.0;
    public static final Double MAX_OXYGEN = 100.0;
    public static final Double MAX_OXYGEN_VARIATION = 1.0;

    private Double oxygen = 95.0;

    public OxygenDescriptor(){

    }

    public OxygenDescriptor(Double oxygen){
        this.oxygen = oxygen;
    }

    public Double getOxygen() {
        return oxygen;
    }

    public void setOxygen(Double oxygen) {
        if(oxygen < MIN_OXYGEN)
            this.oxygen = MIN_OXYGEN;
        if(oxygen > MAX_OXYGEN)
            this.oxygen = MAX_OXYGEN;
        this.oxygen = oxygen;
    }

    @Override
    public String toString() {
        return "OxygenDescriptor{" + "oxygen=" + oxygen + '}';
    }
}
