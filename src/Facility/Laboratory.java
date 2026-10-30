package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class Laboratory extends Facility
{
    private boolean hasFire;
    private static int nextID=1;
    private static final int maxCapacity=2;
    private static final int maxResources=200;

    public Laboratory() {
        super("Laboratory"+(nextID++), maxCapacity,0, maxResources, new Equipment[]{Equipment.createMicroscope(), Equipment.createSpectrometer(),Equipment.createFireExtinguisher()});
        this.hasFire = false;
        addResource(ResourceType.BATTERY_POWER, maxResources);
        addResource(ResourceType.OXYGEN, maxResources);
        addResource(ResourceType.WATER, maxResources);
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER , POWER_COST) ||
                    !hasEnoughResource(ResourceType.OXYGEN, OXYGEN_COST*getCurrentOccupancy())){
                setOperationalStatus(false);
                return false;
            }
            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            deductResource(ResourceType.OXYGEN, OXYGEN_COST*getCurrentOccupancy());
            return true;
        }
        return false;
    }
    //have microscope, spectrometer and fire extinguisher by default
    public boolean hasFire()
    {
        return hasFire;
    }
    public void setHasFire(boolean hasFire) { 
        this.hasFire = hasFire;
        setOperationalStatus(false);
    }
}
