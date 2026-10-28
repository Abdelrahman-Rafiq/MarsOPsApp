package Mission;
import java.util.Arrays;

public class RescueMission extends Mission{
    private static final int requiredTimeUnits = 8;
    private Agent personnel;
    private Vehicle vehicle;
    private CommunicationStation communicationStation;

    public RescueMission(Colony colony, String description, String location, Agent personnel,
                         Vehicle vehicle, Resource[] resources, CommunicationStation communicationStation) {
        super(colony, description, location,resources);
        if(personnel == null || vehicle == null ||  communicationStation == null){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        this.personnel = personnel;
        this.vehicle = vehicle;
        this.communicationStation = communicationStation;
    }

    @Override
    public String toString() {
        return "RescueMission{" +
                ", missionId=" + missionId +
                "personnel=" + personnel +
                ", communicationStation=" + communicationStation +
                ", vehicle=" + vehicle +
                ", resources=" + Arrays.toString(resources) +
                ", colony=" + colony +
                '}';
    }

    @Override
    protected boolean submitMission(){
        if(this.colony.activeMission != null){
            failureReason = "Another mission is active in the colony right now.";
            return false;
        }

        if(Environment.getStatus().equals("Communication Disruption") ){
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

        if(!this.communicationStation.isAvailable())
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
        this.setStatus("IN_PROGRESS");
        this.timeTaken = 0;
        return true;
    }


    public void advanceTime(int timeUnits){
        if(Environment.getStatus().equals("Communication Disruption") ){
            this.endMission("FAILED");
            failureReason = "Communication Disruption prevents Rescue Missions.";
        }
        int realTakenTime = super.calculateRealTakenTime(timeUnits,requiredTimeUnits);
        super.advanceTime(realTakenTime, requiredTimeUnits);
    }

    @Override
    protected void endMission(String nextState) {
        super.endMission(nextState);
        this.unassign(this.personnel);
        this.unassign(this.vehicle);
        this.communicationStation.setAvailability(true);

    }
}
