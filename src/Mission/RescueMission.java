package Mission;

import Facility.CommunicationStation;
import Helper.*;
import Agent.Vehicle;
import java.lang.classfile.constantpool.ConstantPool;
import java.util.Arrays;
import Agent.Agent;
import Environment.Environment;

public class RescueMission extends Mission{
    private static final int requiredTimeUnits = 8;
    private Agent personnel;
    private Vehicle vehicle;
    private CommunicationStation communicationStation;


    public RescueMission(Colony colony, String description, String location, Agent personnel,
                         Vehicle vehicle, ResourceType[] resources, CommunicationStation communicationStation, Equipment equipment) {
        super(colony, description, location,resources,equipment);
        if(personnel == null || vehicle == null ||  communicationStation == null ){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        this.personnel = personnel;
        this.vehicle = vehicle;
        this.communicationStation = communicationStation;
    }

    @Override
    public String toString() {
        return "RescueMission{" +
                "missionId=" + missionId +
                ", colony=" + colony +
                ", current status = "+ this.getStatus()+
                ", description= "+ this.getDescription()+
                ", location= "+ this.getLocation()+
                ", personnel=" + personnel +
                ", vehicle=" + vehicle +
                ", equipment=" + equipment +
                ", communication station=" + communicationStation+
                ", resources=" + Arrays.toString(resources) +
                '}';
    }

    @Override
    protected boolean submitMission(){
        if(this.colony.activeMission != null){
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }

        if(Environment.getState()== EnvironmentCondition.COMMUNICATION_DISRUPTION ){
            failureReason = "Communication Disruption prevents Rescue Missions.";
        }
        if(!this.personnel.canPerform(this)){
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(!this.vehicle.canPerform(this))
        {
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(!this.colony.checkAvailableResources(this.resources)){
            failureReason = "There aren't available sufficient resources in the colony.";
            return false;
        }

        if(!this.communicationStation.isOperational())
        {
            failureReason = "Communication Station is NOT available right now.";
            return false;
        }
        return true;
    }


    @Override
    public boolean startMission(){
        if(!super.startMission()){
            return false;
        }
        this.assign(this.personnel);
        this.assign(this.vehicle);
        this.setStatus(MissionState.IN_PROGRESS);
        this.timeTaken = 0;
        return true;
    }



    public void advanceTime(int timeUnits){
        if(Environment.getState()== EnvironmentCondition.COMMUNICATION_DISRUPTION ){
            this.endMission(MissionState.FAILED);
            failureReason = "Communication Disruption prevents Rescue Missions.";
        }
        int realTakenTime = super.calculateRealTakenTime(timeUnits,requiredTimeUnits);
        super.advanceTime(realTakenTime, requiredTimeUnits);
    }

    @Override
    protected void endMission(MissionState nextState) {
        super.endMission(nextState);
        this.unassign(this.personnel);
        this.unassign(this.vehicle);

    }
}
