package Agent;

import Facility.PowerStation;
import Helper.ResourceType;

public abstract class RoboticUnit extends Agent {

    private int batteryLevel;

    public RoboticUnit(String name, String id, String location) {
        super(name, id, location);
        this.batteryLevel = 100;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public boolean setBatteryLevel(int batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            return false;
        }
        this.batteryLevel = batteryLevel;
        updateAvailability();
        return true;
    }

    public boolean discharge(int amount) {
        if (amount <= 0 || this.batteryLevel < amount) {
            this.batteryLevel = Math.max(0, this.batteryLevel - Math.max(0, amount));
            updateAvailability();
            return false;
        }
        this.batteryLevel -= amount;
        updateAvailability();
        return true;
    }

    public boolean charge(PowerStation powerStation, int amount) {
        if (isOnMission() || powerStation == null || !powerStation.isOperational() || amount <= 0 || this.batteryLevel >= 100) {
            return false;
        }

        int needed = Math.min(amount, 100 - this.batteryLevel);
        int deducted = powerStation.deductResource(ResourceType.BATTERY_POWER, needed);
        if (deducted > 0) {
            this.batteryLevel += deducted;
            updateAvailability();
            return true;
        }
        return false;
    }

    @Override
    protected void updateAvailability() {
        this.availability = !isOnMission() && this.healthStatus > 0 && this.batteryLevel > 0;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public boolean isRobot() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s | Battery: %3d%%", super.toString(), batteryLevel);
    }
}