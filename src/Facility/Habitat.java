package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class Habitat extends Facility
{ 
   private int currentOccupancy;
   private final LifeSupportSystem lifeSupport;
    public Habitat(String location, int currentOccupancy, int capacity, int maxResources, Equipment[] equipment) {
        super(location, capacity, maxResources, equipment);
        this.currentOccupancy = Math.max(0, Math.min(currentOccupancy, capacity));
        this.lifeSupport = new LifeSupportSystem(89);
    }
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }
    public boolean  addOccupiers() {
        if(currentOccupancy==getCapacity())
            return false;
        currentOccupancy++;
        return true;
    }
    public boolean  removeOccupiers() {
        if(currentOccupancy==0)
            return false;
        currentOccupancy--;
        return true;
    }
    public boolean updateOxygen() {
        if(isOperational()){
            deductResource(ResourceType.OXYGEN, oxygenUsage*currentOccupancy);
            if(lifeSupport.isFunctional())
                addResource(ResourceType.OXYGEN, lifeSupport.getFiltrationRate());
            return true;
        }
        return false;
    }
    public LifeSupportSystem getLifeSupport() {
        return lifeSupport;
    }
    @Override
    public String toString() {
        return String.format("%s | Occupancy: %d/%d | %s",
                super.toString(),
                currentOccupancy,
                getCapacity(),
                lifeSupport);
    }
}
