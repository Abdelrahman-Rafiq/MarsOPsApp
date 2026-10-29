package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class PowerStation extends Facility {
    private final int chargingRate;
    private static int nextID=1;
    private static final int FUEL_CONSUMPTION=5;
    public PowerStation(int capacity, int currentOccupancy, int maxResources, int chargingRate) {
        super("PowerStation"+(nextID++), capacity, currentOccupancy, maxResources,new Equipment[]{Equipment.createRepairToolkit()});
        this.chargingRate = Math.max(0, chargingRate);
    }
    public int getChargingRate() {return chargingRate;}
    public boolean generatePower() {
        if (isOperational()) {
            addResource(ResourceType.BATTERY_POWER, chargingRate);
            return true;
        }
        return false;
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER,POWER_COST)||
                    !hasEnoughResource(ResourceType.OXYGEN,OXYGEN_COST*getCurrentOccupancy())||
                    !hasEnoughResource(ResourceType.FUEL,FUEL_CONSUMPTION)) {
                setOperationalStatus(false);
                return false;
            }
            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            deductResource(ResourceType.OXYGEN, OXYGEN_COST*getCurrentOccupancy());
            deductResource(ResourceType.FUEL, FUEL_CONSUMPTION);
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
