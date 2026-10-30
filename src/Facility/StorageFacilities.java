package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class StorageFacilities extends Facility {
    private static int nextID=1;
    private static final int maxCapacity=2;
    private static final int maxResources=600;

    public StorageFacilities() {
        super("Storage Facility"+(nextID++), maxCapacity,0, maxResources,new Equipment[]{Equipment.createWeldingTorch(),Equipment.createRepairToolkit()});
        addResource(ResourceType.OXYGEN, maxResources);
        addResource(ResourceType.WATER, maxResources);
        addResource(ResourceType.FOOD, maxResources);
        addResource(ResourceType.SPARE_PARTS, maxResources);
        addResource(ResourceType.BATTERY_POWER, maxResources);
        addResource(ResourceType.FUEL, maxResources);
        addResource(ResourceType.MEDICAL_SUPPLIES, maxResources);
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER,POWER_COST)) {
                setOperationalStatus(false);
                return false;
            }
            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            return true;
        }
        return false;
    }
}
