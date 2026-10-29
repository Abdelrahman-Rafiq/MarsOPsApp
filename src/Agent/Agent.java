package Agent;

import Helper.Capability;
import Mission.Mission;
import java.util.EnumSet;
import java.util.Set;

public abstract class Agent {

    private final String name;
    private final String id;
    protected int healthStatus;
    protected boolean availability;
    private String location;
    protected Set<Capability> capabilities;

    public Agent(String name, String id, String location) {
        this.name = name;
        this.id = id;
        this.healthStatus = 100;
        this.availability = true;
        this.location = location;
        this.capabilities = EnumSet.noneOf(Capability.class);
    }

    public abstract void setHealthStatus(int healthStatus);

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isAvailable() {
        return availability;
    }

    public String getLocation() {
        return location;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public void addCapability(Capability capability) {
        this.capabilities.add(capability);
    }

    public boolean hasCapability(Capability capability) {
        return  capabilities.contains(capability);
    }
    public abstract boolean isHuman();
    public abstract boolean isRobot();

    public abstract boolean canPerform(Mission mission);

    public boolean canPerform(Object mission) {
        if (mission instanceof Mission) {
            return canPerform((Mission) mission);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Location: %s | Health: %d | Available: %b | Capabilities: %s",
                id, name, location, healthStatus, availability, capabilities);
    }
}