package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class Laboratory extends Facility
{
    private boolean hasFire;
    private static int nextID=1;
    public Laboratory(int capacity,int currentOccupancy, int maxResources) {
        super("Laboratory"+(nextID++), capacity,currentOccupancy, maxResources, new Equipment[]{Equipment.createMicroscope(), Equipment.createSpectrometer(),Equipment.createFireExtinguisher()});
        this.hasFire = false;
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
