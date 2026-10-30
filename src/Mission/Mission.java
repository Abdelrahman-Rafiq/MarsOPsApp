package Mission;

import Agent.Agent;
import Colony.Colony;
import Helper.Capability;
import Helper.Equipment;
import Helper.MissionState;
import Helper.ResourceType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Mission {
    protected final Colony colony;
    protected static int totalMissions = 0;
    protected String missionId;
    private String description;
    private MissionState status;
    private String location;
    protected ResourceType[] resources;
    protected int requiredDuration;
    protected int timeElapsed;
    protected String failureReason;
    protected Equipment equipment;
    protected final List<Agent> assignedAgents;

    public Mission(Colony colony, String description, String location, ResourceType[] resources, Equipment equipment, int requiredDuration) {
        if (colony == null || resources == null || description == null || description.isEmpty() || location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Null or empty arguments aren't allowed!");
        }
        this.colony = colony;
        this.description = description;
        this.location = location;
        this.resources = resources.clone();
        this.equipment = equipment;
        this.missionId = "Mission-" + (++totalMissions);
        this.requiredDuration = requiredDuration;
        this.timeElapsed = 0;
        this.failureReason = "";
        this.assignedAgents = new ArrayList<>();
    }

    public Mission(Colony colony, String description, String location, ResourceType[] resources, Equipment equipment) {
        this(colony, description, location, resources, equipment, 5);
    }

    public String getMissionId() {
        return missionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MissionState getStatus() {
        return status;
    }

    public void setStatus(MissionState status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ResourceType[] getResources() {
        return resources.clone();
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getRequiredDuration() {
        return requiredDuration;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public List<Agent> getAssignedAgents() {
        return Collections.unmodifiableList(assignedAgents);
    }

    public Capability getRequiredCapability() {
        return this.equipment != null ? this.equipment.getProvidedCapability() : Capability.SURFACE_EXPLORATION;
    }

    public abstract boolean submitMission();

    public boolean startMission() {
        if (!this.submitMission()) {
            this.setStatus(MissionState.FAILED);
            return false;
        }
        this.colony.setActiveMission(this);
        this.setStatus(MissionState.ASSIGNED);
        return true;
    }

    public void progress(int timeUnits) {
        this.timeElapsed += timeUnits;
    }

    public boolean isCompleted() {
        return this.timeElapsed >= this.requiredDuration;
    }

    public void fail(String reason) {
        this.failureReason = reason;
        endMission(MissionState.FAILED);
    }

    public void complete() {
        endMission(MissionState.COMPLETED);
    }

    protected void endMission(MissionState nextState) {
        this.setStatus(nextState);
        this.colony.removeActiveMission();
    }

    protected void assign(Agent agent) {
        if (agent != null) {
            agent.setOnMission(true);
            if (!assignedAgents.contains(agent)) {
                assignedAgents.add(agent);
            }
        }
    }

    protected void unassign(Agent agent) {
        if (agent != null) {
            agent.setOnMission(false);
            assignedAgents.remove(agent);
        }
    }
}
