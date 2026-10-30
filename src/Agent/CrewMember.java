package Agent;

import Facility.Habitat;
import Helper.Capability;
import Helper.ResourceType;

public abstract class CrewMember extends Agent {

    private static final int FATIGUE_RECOVERY_PER_FOOD = 15;
    private static final int HEALTH_RECOVERY_PER_FOOD = 10;
    private static final int FATIGUE_RECOVERY_PER_WATER = 10;
    private static final int HEALTH_RECOVERY_PER_WATER = 5;

    private int fatigueLevel;

    public CrewMember(String name, String id, String location) {
        super(name, id, location);
        this.fatigueLevel = 0;
        addCapability(Capability.FIRE_RESPONSE);
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    public boolean increaseFatigue(int amount) {
        if (amount <= 0 || this.fatigueLevel >= 100) {
            return false;
        }
        this.fatigueLevel = Math.min(100, this.fatigueLevel + amount);
        updateAvailability();
        return true;
    }

    public boolean rest(int amount) {
        if (isOnMission() || amount <= 0 || this.fatigueLevel <= 0) {
            return false;
        }
        this.fatigueLevel = Math.max(0, this.fatigueLevel - amount);
        updateAvailability();
        return true;
    }

    public boolean eat(Habitat habitat, int amount) {
        if (isOnMission() || habitat == null || !habitat.isOperational() || amount <= 0) {
            return false;
        }

        int deducted = habitat.deductResource(ResourceType.FOOD, amount);
        if (deducted > 0) {
            this.fatigueLevel = Math.max(0, this.fatigueLevel - (deducted * FATIGUE_RECOVERY_PER_FOOD));
            restoreHealth(deducted * HEALTH_RECOVERY_PER_FOOD);
            updateAvailability();
            return true;
        }
        return false;
    }

    public boolean drink(Habitat habitat, int amount) {
        if (isOnMission() || habitat == null || !habitat.isOperational() || amount <= 0) {
            return false;
        }

        int deducted = habitat.deductResource(ResourceType.WATER, amount);
        if (deducted > 0) {
            this.fatigueLevel = Math.max(0, this.fatigueLevel - (deducted * FATIGUE_RECOVERY_PER_WATER));
            restoreHealth(deducted * HEALTH_RECOVERY_PER_WATER);
            updateAvailability();
            return true;
        }
        return false;
    }

    @Override
    protected void updateAvailability() {
        this.availability = !isOnMission() && this.healthStatus > 0 && this.fatigueLevel < 100;
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
    public String toString() {
        return String.format("%s | Fatigue: %3d%%", super.toString(), fatigueLevel);
    }
}