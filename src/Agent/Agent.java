package Agent;

import Helper.Capability;
import Mission.Mission;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class Agent {

    private final String name;
    private final String id;
    protected int healthStatus;
    protected boolean availability;
    private boolean onMission;
    private String location;
    protected final Set<Capability> capabilities;

    public Agent(String name, String id, String location) {
        this.name = name;
        this.id = id;
        this.healthStatus = 100;
        this.onMission = false;
        this.availability = true;
        this.location = location;
        this.capabilities = EnumSet.noneOf(Capability.class);
    }

    protected abstract void updateAvailability();

    public boolean setHealthStatus(int healthStatus) {
        if (healthStatus < 0 || healthStatus > 100) {
            return false;
        }
        this.healthStatus = healthStatus;
        updateAvailability();
        return true;
    }

    public boolean takeDamage(int amount) {
        if (amount <= 0 || this.healthStatus <= 0) {
            return false;
        }
        this.healthStatus = Math.max(0, this.healthStatus - amount);
        updateAvailability();
        return true;
    }

    public boolean restoreHealth(int amount) {
        if (amount <= 0 || this.healthStatus <= 0 || this.healthStatus >= 100) {
            return false;
        }
        this.healthStatus = Math.min(100, this.healthStatus + amount);
        updateAvailability();
        return true;
    }

    public boolean isOnMission() {
        return onMission;
    }

    public void setOnMission(boolean onMission) {
        this.onMission = onMission;
        updateAvailability();
    }

    public boolean isAvailable() {
        updateAvailability();
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getHealthStatus() {
        return healthStatus;
    }

    public String getLocation() {
        return location;
    }

    public boolean setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return false;
        }
        this.location = location;
        return true;
    }

    public Set<Capability> getCapabilities() {
        return Collections.unmodifiableSet(capabilities);
    }

    public boolean addCapability(Capability capability) {
        if (capability == null) {
            return false;
        }
        return this.capabilities.add(capability);
    }

    public boolean hasCapability(Capability capability) {
        return capabilities.contains(capability);
    }

    public abstract boolean isHuman();

    public abstract boolean isRobot();

    public boolean canPerform(Mission mission) {
        if (mission == null || !isAvailable()) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }

    public boolean canHandle(Capability capability) {
        if (capability == null || !isAvailable()) {
            return false;
        }
        return hasCapability(capability);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %-18s | Name: %-14s | Loc: %-16s | Health: %3d%% | OnMission: %-5b | Available: %-5b | Capabilities: %s",
                getClass().getSimpleName(), id, name, location, healthStatus, onMission, isAvailable(), capabilities);
    }
}
