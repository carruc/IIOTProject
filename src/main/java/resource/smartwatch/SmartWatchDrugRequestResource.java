package resource.smartwatch;

import model.descriptors.smartdruginventory.DrugDescriptor;
import model.descriptors.smartdruginventory.SmartDrugInventoryDescriptor;
import model.descriptors.smartdruginventory.InventoryDescriptor;
import model.descriptors.smartwatch.SmartWatchDrugRequestDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.GenericResource;
import resource.ResourceDataListener;
import resource.wristband.AlarmActuatorResource;

import java.awt.geom.Point2D;
import java.util.*;

public class SmartWatchDrugRequestResource extends GenericResource<SmartWatchDrugRequestDescriptor> {
    private static final Logger logger = LoggerFactory.getLogger(AlarmActuatorResource.class);

    public static final String RESOURCE_TYPE = "iot:sensor:smartwatch:drug";

    private static final long SMARTWATCH_MESSAGE_UPDATE_STARTING_DELAY = 5000;
    private static final long SMARTWATCH_MESSAGE_UPDATE_PERIOD = 20000;

    private Timer timer;
    private Random random;

    public final SmartWatchDrugRequestDescriptor smartWatchDrugRequestDescriptor;
    public final List<DrugDescriptor> drugList = new ArrayList<>(List.of(new DrugDescriptor[]{
            new DrugDescriptor("Tachipirina", "123", 19),
            new DrugDescriptor("Flomax", "111", 12),
            new DrugDescriptor("Valium", "004", 100)
    }));;

    public SmartWatchDrugRequestResource(SmartWatchDrugRequestDescriptor smartWatchDrugRequestDescriptor) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.smartWatchDrugRequestDescriptor = smartWatchDrugRequestDescriptor;
    }

    private void init() {
        timer = new Timer();
        random = new Random();
        startPeriodicTask();
    }

    private void startPeriodicTask() {
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    smartWatchDrugRequestDescriptor.setDrugID(drugList.get(random.nextInt(drugList.size())).getId());
                    notifyUpdate(smartWatchDrugRequestDescriptor);
                }
            }, SMARTWATCH_MESSAGE_UPDATE_STARTING_DELAY, SMARTWATCH_MESSAGE_UPDATE_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SmartWatchDrugRequestDescriptor loadUpdatedValue() {
        return null;
    }

    public void setValue(String message) {
        this.smartWatchDrugRequestDescriptor.setDrugID(message);
    }

    public String getValue() {
        return this.smartWatchDrugRequestDescriptor.getDrugID();
    }

    public SmartWatchDrugRequestDescriptor getSmartWatchDrugRequestDescriptor() {
        return smartWatchDrugRequestDescriptor;
    }

    public static void main(String[] args) {

        SmartDrugInventoryDescriptor drugInventoryDescriptor = SmartDrugInventoryDescriptor.getInstance();

        SmartWatchDrugRequestResource drugRequest =
                new SmartWatchDrugRequestResource(new SmartWatchDrugRequestDescriptor("111"));

        drugRequest.addDataListener(new ResourceDataListener<SmartWatchDrugRequestDescriptor>() {
            @Override
            public void onDataChanged(GenericResource<SmartWatchDrugRequestDescriptor> resource, SmartWatchDrugRequestDescriptor updatedValue) {
                if (resource != null && updatedValue != null) {
                    logger.info("Device: {} -> New drug request: {}", resource.getId(), updatedValue);
                    System.out.println("Device " + resource.getId() + "Value: " + updatedValue);
                } else {
                    logger.error("Drug request error");
                }
            }
        });


    }
}
