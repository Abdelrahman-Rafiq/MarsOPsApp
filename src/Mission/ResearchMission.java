package Mission;

import Agent.Agent;
import Colony.Colony;
import Facility.Laboratory;
import Helper.Capability;
import Helper.Equipment;
import Helper.MissionState;
import Helper.ResourceType;
import java.util.Arrays;

public class ResearchMission extends Mission {
    private static final int REQUIRED_TIME_UNITS = 10;
    private Agent scientist;
    private Laboratory laboratory;
    private String experimentName;

    public ResearchMission(Colony colony, String description, String location,
                           Agent scientist, Laboratory laboratory, String experimentName,
                           Equipment equipment, ResourceType[] resources) {
        super(colony, description, location, resources, equipment, REQUIRED_TIME_UNITS);
        if (scientist == null || laboratory == null || experimentName == null || experimentName.isEmpty()) {
            throw new IllegalArgumentException("Null or empty arguments aren't allowed!");
        }
        this.experimentName = experimentName;
        this.scientist = scientist;
        this.laboratory = laboratory;
    }

    public Agent getScientist() {
        return scientist;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public String getExperimentName() {
        return experimentName;
    }

    @Override
    public Capability getRequiredCapability() {
        return Capability.SCIENTIFIC_RESEARCH;
    }

    @Override
    public String toString() {
        return "ResearchMission{" +
                "missionId='" + missionId + '\'' +
                ", currentStatus=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", location='" + getLocation() + '\'' +
                ", scientist=" + scientist.getName() +
                ", laboratory=" + laboratory.getLocation() +
                ", experimentName='" + experimentName + '\'' +
                ", equipment=" + equipment +
                ", resources=" + Arrays.toString(resources) +
                '}';
    }

    @Override
    public boolean submitMission() {
        if (this.colony.getActiveMission() != null && this.colony.getActiveMission() != this) {
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }

        if (!this.scientist.isAvailable() || !this.scientist.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.laboratory.isOperational() || this.laboratory.hasFire()) {
            failureReason = "Laboratory is NOT available right now.";
            return false;
        }

        if (this.equipment != null && !this.colony.checkAvailableEquipment(this.equipment)) {
            failureReason = "Equipment isn't available for this mission right now.";
            return false;
        }

        if (!this.colony.checkAvailableResources(this.resources,20)) {
            failureReason = "There aren't available sufficient resources in the colony.";
            return false;
        }

        return true;
    }

    @Override
    public boolean startMission() {
        if (!super.startMission()) {
            return false;
        }
        this.assign(this.scientist);
        if (this.equipment != null) {
            this.equipment.setAvailability(false);
        }
        this.colony.deductResource(this.resources, 20);
        if (this.equipment != null) {
            this.equipment.useIt();
            this.equipment.setAvailability(false);
        }
        this.setStatus(MissionState.IN_PROGRESS);
        this.timeElapsed = 0;
        return true;
    }

    @Override
    public void endMission(MissionState nextState) {
        super.endMission(nextState);
        this.unassign(this.scientist);
        if (this.equipment != null) {
            this.equipment.setAvailability(true);
        }
    }
}
