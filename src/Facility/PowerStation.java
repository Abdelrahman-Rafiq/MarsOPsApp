package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class PowerStation extends Facility {
    private final int chargingRate;
    public PowerStation(String location, int capacity, int maxResources,Equipment[] equipment, int chargingRate) {
        super(location, capacity, maxResources, equipment);
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
    public String toString() {
        return String.format("%s | ChargingRate: +%d Battery/tick",
                super.toString(),
                chargingRate);
    }
}
