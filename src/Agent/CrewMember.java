package Agent;

import Mission.Mission;

public abstract class CrewMember extends Agent {

    private int fatigueLevel;
    private static final int FOOD_COST = 1;
    private static final int WATER_COST = 2;

    public CrewMember(String name, String id, String location) {
        super(name, id, location);
        this.fatigueLevel = 0;
    }

    public void setFatigueLevel(int fatigueLevel) {
        this.fatigueLevel = fatigueLevel;
        if (fatigueLevel == 100)
            this.availability = false;
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public boolean isRobot() {
        return false;
    }

    @Override
    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
        if (healthStatus == 0)
            this.availability = false;
    }

    @Override
    public abstract boolean canPerform(Mission mission);


    @Override
    public String toString() {
        return String.format("%s | Fatigue: %d", super.toString(), fatigueLevel);
    }
}