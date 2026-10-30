package Emergency;

import Colony.Colony;
import Helper.Capability;
import Helper.EmergencyState;

public abstract class Emergency {
    protected final String emergencyId;
    protected final String location;
    protected final String description;
    protected Capability[] requiredCapabilities;
    protected EmergencyState emergencyState;
    protected String failureReason;
    private static int nextId = 1;

    protected Emergency(String description, String location, Capability[] requiredCapabilities) {
        this.emergencyId = "EMG" + (nextId++);
        this.description = description;
        this.location = location;
        this.requiredCapabilities = (requiredCapabilities != null) ? requiredCapabilities.clone() : new Capability[0];
        this.emergencyState = EmergencyState.TRIGGERED;
        this.failureReason = "";
    }

    public abstract boolean handle(Colony colony);

    public String getEmergencyId() {
        return emergencyId;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public EmergencyState getEmergencyState() {
        return emergencyState;
    }

    public void setEmergencyState(EmergencyState emergencyState) {
        this.emergencyState = emergencyState;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Capability[] getRequiredCapabilities() {
        return requiredCapabilities != null ? requiredCapabilities.clone() : new Capability[0];
    }

    public String displayEmergency() {
        return String.format("[%s] ID: %s | Loc: %-16s | State: %-10s | FailureReason: %s",
                getClass().getSimpleName(),
                emergencyId,
                location,
                emergencyState,
                (failureReason == null || failureReason.isEmpty() ? "None" : failureReason));
    }

    @Override
    public String toString() {
        return displayEmergency();
    }
}
