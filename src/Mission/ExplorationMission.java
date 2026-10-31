package Mission;

import Agent.Agent;
import Agent.CrewMember;
import Agent.Vehicle;
import Colony.Colony;
import Environment.Environment;
import Helper.*;
import java.util.Arrays;

public class ExplorationMission extends Mission {

    private static final int REQUIRED_TIME_UNITS = 7;
    private Agent crewMember;
    private Vehicle vehicle;
    private double requiredBatteryLevel;

    public ExplorationMission(Colony colony, String description, String location,
                              Agent crewMember, Vehicle vehicle,
                              double requiredBatteryLevel, Equipment equipment,
                              ResourceType[] resources) {
        super(colony, description, location, resources, equipment, REQUIRED_TIME_UNITS);

        if (crewMember == null || vehicle == null) {
            throw new IllegalArgumentException("Null agents aren't allowed!");
        }
        if (requiredBatteryLevel <= 0) {
            throw new IllegalArgumentException("Non-positive battery level isn't allowed!");
        }
        this.crewMember = crewMember;
        this.vehicle = vehicle;
        this.requiredBatteryLevel = requiredBatteryLevel;
    }

    public Agent getCrewMember() {
        return crewMember;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getRequiredBatteryLevel() {
        return requiredBatteryLevel;
    }

    @Override
    public Capability getRequiredCapability() {
        return Capability.SURFACE_EXPLORATION;
    }

    @Override
    public String toString() {
        return "ExplorationMission{" +
                "missionId='" + missionId + '\'' +
                ", currentStatus=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", location='" + getLocation() + '\'' +
                ", crewMember=" + crewMember.getName() +
                ", vehicle=" + vehicle.getName() +
                ", requiredBatteryLevel=" + requiredBatteryLevel +
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

        if (Environment.getState() == EnvironmentCondition.DUST_STORM) {
            failureReason = "Surface operations are not allowed during a dust storm.";
            return false;
        }

        if (!this.crewMember.isAvailable() || !this.crewMember.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.vehicle.isAvailable() || !this.vehicle.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (this.vehicle.getBatteryLevel() < this.requiredBatteryLevel) {
            failureReason = "Vehicle doesn't have sufficient battery level for the exploration.";
            return false;
        }

        if (this.equipment != null && !this.colony.checkAvailableEquipment(this.equipment)) {
            failureReason = "Equipment isn't available for this mission right now.";
            return false;
        }

        if (!this.colony.checkAvailableResources(this.resources,30)) {
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
        this.assign(this.crewMember);
        this.assign(this.vehicle);
        if (this.equipment != null) {
            this.equipment.setAvailability(false);
        }
        this.colony.deductResource(this.resources, 30);
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
        this.unassign(this.crewMember);
        this.unassign(this.vehicle);
        if (this.equipment != null) {
            this.equipment.setAvailability(true);
        }
    }
}
