package Mission;

import Agent.Agent;
import Agent.Vehicle;
import Colony.Colony;
import Environment.Environment;
import Facility.CommunicationStation;
import Helper.Capability;
import Helper.EnvironmentCondition;
import Helper.Equipment;
import Helper.MissionState;
import Helper.ResourceType;
import java.util.Arrays;

public class RescueMission extends Mission {
    private static final int REQUIRED_TIME_UNITS = 8;
    private Agent personnel;
    private Vehicle vehicle;
    private CommunicationStation communicationStation;

    public RescueMission(Colony colony, String description, String location,
                         Agent personnel, Vehicle vehicle,
                         ResourceType[] resources, CommunicationStation communicationStation, Equipment equipment) {
        super(colony, description, location, resources, equipment, REQUIRED_TIME_UNITS);
        if (personnel == null || vehicle == null || communicationStation == null) {
            throw new IllegalArgumentException("Null arguments aren't allowed!");
        }
        this.personnel = personnel;
        this.vehicle = vehicle;
        this.communicationStation = communicationStation;
    }

    public Agent getPersonnel() {
        return personnel;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public CommunicationStation getCommunicationStation() {
        return communicationStation;
    }

    @Override
    public Capability getRequiredCapability() {
        return Capability.RESCUE;
    }

    @Override
    public String toString() {
        return "RescueMission{" +
                "missionId='" + missionId + '\'' +
                ", currentStatus=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", location='" + getLocation() + '\'' +
                ", personnel=" + personnel.getName() +
                ", vehicle=" + vehicle.getName() +
                ", communicationStation=" + communicationStation.getLocation() +
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

        if (Environment.getState() == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
            failureReason = "Communication Disruption prevents Rescue Missions.";
            return false;
        }

        if (!this.personnel.isAvailable() || !this.personnel.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.vehicle.isAvailable() || !this.vehicle.canPerform(this)) {
            failureReason = "Agent does not possess the required capability.";
            return false;
        }

        if (!this.communicationStation.isOperational()) {
            failureReason = "Communication Station is NOT available right now.";
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
        this.assign(this.personnel);
        this.assign(this.vehicle);
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
        this.unassign(this.personnel);
        this.unassign(this.vehicle);
        if (this.equipment != null) {
            this.equipment.setAvailability(true);
        }
    }
}
