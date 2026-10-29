package Agent;


import Mission.Mission;

public abstract class RoboticUnit extends Agent {

    private int batteryLevel;

    public RoboticUnit(String name, String id, String location) {
        super(name, id, location);
        this.batteryLevel = 100;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
        if (batteryLevel <= 0)
            this.availability = false;
    }

    @Override
    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
        if (healthStatus <= 0)
            this.availability = false;
    }

    @Override
    public  boolean isHuman(){
        return false;
    }

    @Override
    public boolean isRobot() {
        return true;
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public String toString() {
        return String.format("%s | Battery: %d%%", super.toString(), batteryLevel);
    }
}