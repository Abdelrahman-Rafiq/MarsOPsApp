package Mission;

import Facility.*;
import Helper.Equipment;
import Helper.ResourceType;
import java.util.Arrays;

public class RepairMission extends Mission{
    private static final int requiredTimeUnits = 5;
    private Engineer engineer;
    private RepairRobot repairRobot;
    private Facility targetFacility;
    private Equipment targetEquipment;

    private RepairMission(Colony colony, String description, String location,
                          Facility targetFacility, Equipment targetEquipment,
                          Engineer engineer, RepairRobot repairRobot, ResourceType[] resources) {
        super(colony, description, location,resources);
        if(engineer == null || repairRobot == null){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        this.engineer = engineer;
        this.repairRobot = repairRobot;
        this.targetEquipment = targetEquipment;
        this.targetFacility = targetFacility;
    }

    public RepairMission(Colony colony, String description, String location,Facility target,
                          Engineer engineer,RepairRobot repairRobot, Resource[] resources) {
        this(colony,description,location,target,null,engineer,repairRobot,resources);
    }
    public RepairMission(Colony colony, String description, String location,Equipment target,
                         Engineer engineer,RepairRobot repairRobot, Resource[] resources) {
        this(colony,description,location,null,target,engineer,repairRobot,resources);
    }

    @Override
    public String toString() {
        return "RepairMission{" +
                ", missionId=" + missionId +
                "engineer=" + engineer +
                ", repairRobot=" + repairRobot +
                ", resources=" + Arrays.toString(resources) +
                ", targetFacility=" + targetFacility +
                ", targetEquipment=" + targetEquipment +
                ", colony=" + colony +
                '}';
    }

    @Override
    protected boolean submitMission(){
        if(this.colony.activeMission != null){
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }
        if(!this.engineer.canPerform(this)){
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(!this.repairRobot.canPerform(this))
        {
            failureReason = "Agent can't perform this mission.";
            return false;
        }

        if(!this.colony.checkAvailableResources(this.resources)){
            failureReason = "There aren't available sufficient resources in the colony.";
            return false;
        }

        if(this.targetEquipment != null && !this.targetEquipment.isDamaged()){
            failureReason = "The target Equipment is NOT damaged.";
        }
        if(this.targetFacility != null && !this.targetFacility.isDamaged()){
            failureReason = "The target Facility is NOT damaged.";
        }
        return true;
    }


    @Override
    public boolean startMission(){
        if(!super.startMission()){
            return false;
        }
        this.assign(this.engineer);
        this.assign(this.repairRobot);
        this.setStatus("IN_PROGRESS");
        this.timeTaken = 0;
        return true;
    }


    public void advanceTime(int timeUnits){
        int realTakenTime = super.calculateRealTakenTime(timeUnits,requiredTimeUnits);
        super.advanceTime(realTakenTime, requiredTimeUnits);
    }

    @Override
    protected void endMission(String nextState) {
        super.endMission(nextState);
        this.unassign(this.engineer);
        this.unassign(this.repairRobot);
        if(this.targetFacility != null){
            this.targetFacility.setAvailability(true);
        }
        else{
            this.targetEquipment.setAvailability(true);
        }
        this.setStatus("IN_PROGRESS");
    }
}
