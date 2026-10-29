package Mission;
import Helper.Capability;
import Helper.Equipment;
import Helper.ResourceType;
import Agent.Agent;
import Helper.MissionState;

public abstract class Mission {
    protected final Colony colony;
    protected static int totalMissions = 0;
    protected String missionId;
    private String description;
    private MissionState status;
    private String location;
    protected ResourceType[] resources;
    protected int timeTaken;
    protected String failureReason;
    protected Equipment equipment;

    public Mission(Colony colony, String description, String location, ResourceType[] resources,Equipment equipment) {
        if(colony == null || resources == null ||  description == null || description.isEmpty() || location.isEmpty() || location == null || equipment == null){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        this.description = description;
        this.location = location;
        this.colony = colony;
        this.resources = resources.clone();
        this.missionId = "Mission:"+totalMissions++;
        this.equipment =equipment;
        this.timeTaken = 0;
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

    // status
    public void setStatus(MissionState status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    protected abstract boolean submitMission();

    public Capability getRequiredCapability(){
        return this.equipment.getProvidedCapability();
    }

    public boolean startMission(){
        if(!this.submitMission()){
            this.setStatus(MissionState.FAILED);
            return false;
        }
        this.colony.setActiveMission(this);
        this.setStatus(MissionState.ASSIGNED);
        return true;
    }

    protected void endMission(MissionState nextState){
        this.setStatus(nextState);
        this.colony.removeActiveMission();
    }

    protected void assign(Agent agent){
        agent.setOnMission(true);
    }
    protected void unassign(Agent agent) {
        agent.setOnMission(false);
    }

    protected int calculateRealTakenTime(int timeUnits,int requiredTimeUnits){
        int realTaken = timeUnits ;
        if(this.timeTaken + timeUnits> requiredTimeUnits){
            realTaken -= (this.timeTaken - requiredTimeUnits );
        }
        return realTaken;
    }
    protected void advanceTime(int timeUnits, int requiredTimeUnits){

        this.timeTaken += timeUnits;
        this.colony.deductResource(this.resources,(int)(timeUnits/requiredTimeUnits));
        if(this.timeTaken == requiredTimeUnits){
            this.endMission(MissionState.COMPLETED);
        }


    }
}
