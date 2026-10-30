package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class PowerStation extends Facility {
    private final int chargingRate;
    private static int nextID=1;
    private static final int FUEL_CONSUMPTION=5;
    private static final int maxCapacity=2;
    private static final int maxResources=200;

    public PowerStation( ) {
        super("PowerStation"+(nextID++), maxCapacity, 0, maxResources,new Equipment[]{Equipment.createRepairToolkit()});
        this.chargingRate = 100;
        addResource(ResourceType.FUEL, maxResources);
        addResource(ResourceType.BATTERY_POWER, maxResources);
        addResource(ResourceType.OXYGEN, maxResources);
    }
    public int getChargingRate() {return chargingRate;}
    public boolean generatePower() {
        if (isOperational()) {
            if(addResource(ResourceType.BATTERY_POWER, chargingRate)){
                if(deductResource(ResourceType.FUEL, FUEL_CONSUMPTION)==FUEL_CONSUMPTION)
                    return true;
                deductResource(ResourceType.BATTERY_POWER, chargingRate);
                return false;
            }
        }
        return false;
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER,POWER_COST)) {
                setOperationalStatus(false);
                return false;
            }
            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            generatePower();
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return String.format("%s | ChargingRate: %d Battery/tick",
                super.toString(),
                chargingRate);
    }
}
