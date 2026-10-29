package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class StorageFacilities extends Facility {
    private static int nextID=1;
    public StorageFacilities(int capacity, int currentOccupancy, int maxResources) {
        super("Storage Facility"+(nextID++), capacity, currentOccupancy, maxResources,new Equipment[]{Equipment.createWeldingTorch(),Equipment.createRepairToolkit()});
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER,POWER_COST)||
                    !hasEnoughResource(ResourceType.OXYGEN,OXYGEN_COST*getCurrentOccupancy())) {
                setOperationalStatus(false);
                return false;
            }
            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            deductResource(ResourceType.OXYGEN, OXYGEN_COST*getCurrentOccupancy());
            return true;
        }
        return false;
    }
}
