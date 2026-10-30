package Mission;

import Agent.Agent;
import Colony.Colony;
import Facility.Facility;
import Helper.Capability;
import Helper.Equipment;
import Helper.MissionState;
import Helper.ResourceType;
import java.util.Arrays;

public class RepairMission extends Mission {
    private static final int REQUIRED_TIME_UNITS = 5;
    private Agent engineer;
    private Agent repairRobot;
    private Facility targetFacility;
    private  Equipment targetEquipment;

    private RepairMission(Colony colony, String description, String location,
                          Facility targetFacility, Equipment targetEquipment,
                          Agent engineer, Agent repairRobot, ResourceType[] resources, Equipment requiredEquipment) {
        super(colony, description, location, resources, requiredEquipment, REQUIRED_TIME_UNITS);
        if (engineer == null || repairRobot == null) {
            throw new IllegalArgumentException("Null agents aren't allowed!");
        }
        this.engineer = engineer;
        this.repairRobot = repairRobot;
        this.targetEquipment = targetEquipment;
        this.targetFacility = targetFacility;
    }

    public RepairMission(Colony colony, String description, String location, Facility target,
                         Agent engineer, Agent repairRobot, ResourceType[] resources, Equipment requiredEquipment) {
        this(colony, description, location, target, null, engineer, repairRobot, resources, requiredEquipment);
    }

    public RepairMission(Colony colony, String description, String location, Equipment target,
                         Agent engineer, Agent repairRobot, ResourceType[] resources, Equipment requiredEquipment) {
        this(colony, description, location, null, target, engineer, repairRobot, resources, requiredEquipment);
    }

    public Agent getEngineer() {
        return engineer;
    }

    public Agent getRepairRobot() {
        return repairRobot;
    }

    public Facility getTargetFacility() {
        return targetFacility;
    }

    public Equipment getTargetEquipment() {
        return targetEquipment;
    }

    @Override
    public Capability getRequiredCapability() {
        return Capability.EQUIPMENT_REPAIR;
    }

    @Override
    public String toString() {
        return "RepairMission{" +
                "missionId='" + missionId + '\'' +
                ", currentStatus=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", location='" + getLocation() + '\'' +
                ", engineer=" + engineer.getName() +
                ", repairRobot=" + repairRobot.getName() +
                ", target=" + (targetFacility != null ? targetFacility.getLocation() : (targetEquipment != null ? targetEquipment.getName() : "None")) +
                ", equipment=" + equipment +
                ", resources=" + Arrays.toString(resources) +
                '}';
    }

    @Override
    public boolean submitMission() {
        if (this.colony.getActiveMission() != null) {
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }

        if (!this.engineer.isAvailable() || !this.engineer.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.repairRobot.isAvailable() || !this.repairRobot.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.colony.checkAvailableResources(this.resources,5)) {
            failureReason = "There aren't available sufficient resources in the colony.";
            return false;
        }

        if (this.targetEquipment != null && !this.targetEquipment.isDamaged()) {
            failureReason = "The target Equipment is NOT damaged.";
            return false;
        }

        if (this.targetFacility != null && this.targetFacility.isOperational()) {
            failureReason = "The target Facility is NOT damaged.";
            return false;
        }

        return true;
    }

    @Override
    public boolean startMission() {
        if (!super.startMission()) {
            return false;
        }
        this.assign(this.engineer);
        this.assign(this.repairRobot);
        if (this.equipment != null) {
            this.equipment.setAvailability(false);
        }
        this.setStatus(MissionState.IN_PROGRESS);
        this.timeElapsed = 0;
        return true;
    }

    @Override
    public void endMission(MissionState nextState) {
        super.endMission(nextState);
        this.unassign(this.engineer);
        this.unassign(this.repairRobot);
        if (this.equipment != null) {
            this.equipment.setAvailability(true);
        }
        if (nextState == MissionState.COMPLETED) {
            if (this.targetFacility != null) {
                this.targetFacility.setOperationalStatus(true);
            } else if (this.targetEquipment != null) {
                this.targetEquipment.fixIt();
            }
        }
    }
}
