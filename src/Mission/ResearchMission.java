package Mission;

import java.util.Arrays;

import Agent.Scientist;
import Facility.Laboratory;
import Helper.Capability;
import Helper.Equipment;
import Helper.MissionState;
import Helper.ResourceType;

public class ResearchMission extends Mission{
    private static final int requiredTimeUnits =10;
    private Scientist scientist;
    private Laboratory laboratory;
    private String experimentName;


    public ResearchMission(Colony colony, String description, String location,
                           Scientist scientist, Laboratory laboratory, String experimentName,
                           Equipment equipment, ResourceType[] resources) {
        super(colony, description, location,resources,equipment);
        if(scientist == null || laboratory == null || experimentName == null || experimentName.isEmpty()){
            throw new IllegalArgumentException("null/empty arguments aren't allowed!");
        }
        this.experimentName = experimentName;
        this.scientist = scientist;
        this.laboratory = laboratory;
    }

    @Override
    public String toString() {
        return "ResearchMission{" +
                "missionId=" + missionId +
                ", colony=" + colony +
                ", current status = "+ this.getStatus()+
                ", description= "+ this.getDescription()+
                ", location= "+ this.getLocation()+
                ", scientist=" + scientist +
                ", laboratory=" + laboratory +
                ", experiment name=" + experimentName +
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
        if(!this.scientist.canPerform(this)){
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(!this.laboratory.isOperational()){
            failureReason = "Laboratory is NOT available right now.";
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


    public void advanceTime(int timeUnits){
        int realTakenTime = super.calculateRealTakenTime(timeUnits,requiredTimeUnits);
        super.advanceTime(realTakenTime, requiredTimeUnits);
    }

    @Override
    public boolean startMission(){
        if(!super.startMission()){
            return false;
        }
        this.assign(this.scientist);
        this.equipment.setAvailability(false);
        this.setStatus(MissionState.IN_PROGRESS);
        this.timeTaken = 0;
        return true;

    }


    @Override
    protected void endMission(MissionState nextState) {
        super.endMission(nextState);
        this.unassign(this.scientist);
        this.equipment.setAvailability(true);
    }



}
