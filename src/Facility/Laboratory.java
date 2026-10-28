package Facility;

import Helper.Equipment;

public class Laboratory extends Facility
{
    private boolean hasFire;

    public Laboratory(String location, int capacity, int maxResources, Equipment[] equipment) {
        super(location, capacity, maxResources,equipment);
        this.hasFire = false;
    }
    
    public boolean hasFire()
    {
        return hasFire;
    }
    public void setHasFire(boolean hasFire) { 
        this.hasFire = hasFire; 
    }
    
    
}
