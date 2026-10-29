package Mission;
import Agent.Engineer;
import Helper.*;

import java.util.Arrays;
import Environment.Environment;
import Agent.Vehicle;

public class ExplorationMission extends Mission{

    private static final int requiredTimeUnits = 7;
    private Engineer engineer;
    private Vehicle vehicle;
    private double requiredBatteryLevel;


    public ExplorationMission(Colony colony, String description, String location,
                              Engineer engineer, Vehicle vehicle,
                              double requiredBatteryLevel, Equipment equipment,
                              ResourceType[] resources) {
        super(colony,description, location,resources,equipment);

        if(engineer == null || vehicle == null ){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        if(requiredBatteryLevel <= 0 ){
            throw new IllegalArgumentException("non positive battery level isn't allowed!");
        }
        this.engineer =engineer;
        this.vehicle = vehicle;
        this.requiredBatteryLevel = requiredBatteryLevel;

    }


    @Override
    public String toString() {
        return "ExplorationMission{" +
                "missionId=" + missionId +
                ", colony=" + colony +
                ", current status = "+ this.getStatus()+
                ", description= "+ this.getDescription()+
                ", location= "+ this.getLocation()+
                ", engineer=" + engineer +
                ", vehicle=" + vehicle +
                ", requiredBatteryLevel=" + requiredBatteryLevel +
                ", equipment=" + equipment +
                ", resources=" + Arrays.toString(resources) +
                '}';
    }

    @Override
    protected boolean submitMission(){

        if(this.colony.activeMission != null){
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }


        if(Environment.getState() == EnvironmentCondition.COMMUNICATION_DISRUPTION){
            failureReason = "Dust Storm prevents the Exploration Mission";
        }

        if( !this.engineer.canPerform(this)){
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if( !this.vehicle.canPerform(this)){
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(this.vehicle.getBatteryLevel() < this.requiredBatteryLevel){
            failureReason = "Vehicle doesn't have sufficient battery level for the exploration.";
            return false;
        }

        if(!this.colony.checkAvailableEquipment(this.equipment)){
            failureReason = "Equipment isn't available for this mission right now.";
            return false;
        }

        if(!this.colony.checkAvailableResources(this.resources)){
            failureReason = "There aren't available sufficient resources in the colony.";
            return false;
        }

        return true;
    }

    @Override
    public boolean startMission(){
        if(!super.startMission()){
            return false;
        }
        this.assign(this.engineer);
        this.assign(this.vehicle);
        this.equipment.setAvailability(false);
        this.setStatus(MissionState.IN_PROGRESS);
        this.timeTaken = 0;
        return true;
    }

    public void advanceTime(int timeUnits){
        if(Environment.getState() == EnvironmentCondition.DUST_STORM){
            this.endMission(MissionState.FAILED);
            failureReason = "Dust Storm prevents the Exploration Mission";
        }
        int realTakenTime = super.calculateRealTakenTime(timeUnits,requiredTimeUnits);
        super.advanceTime(realTakenTime, requiredTimeUnits);
        this.vehicle.discharge((int)(this.requiredBatteryLevel * ((double)realTakenTime/requiredTimeUnits)));
    }


    @Override
    protected void endMission(MissionState nextState) {
        super.endMission(nextState);
        this.unassign(this.engineer);
        this.unassign(this.vehicle);
        this.equipment.setAvailability(true);
    }



}
