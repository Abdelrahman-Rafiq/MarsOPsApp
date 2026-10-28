package Mission;
import Helper.ResourceType;

public abstract class Mission {
    protected final Colony colony;
    protected static int totalMissions = 0;
    protected int missionId;
    private String description;
    private String status;
    private String location;
    protected ResourceType[] resources;
    protected int timeTaken;
    protected String failureReason;

    public Mission(Colony colony, String description, String location, ResourceType[] resources) {
        if(colony == null || resources == null ||  description == null || description.isEmpty() || location.isEmpty() || location == null){
            throw new IllegalArgumentException("null arguments aren't allowed!");
        }
        this.description = description;
        this.location = location;
        this.colony = colony;
        this.resources = resources.clone();
        this.missionId = totalMissions;
        totalMissions++;
        this.timeTaken = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    // status
    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    protected abstract boolean submitMission();

    public boolean startMission(){
        if(!this.submitMission()){
            this.setStatus("FAILED");
            return false;
        }
        this.colony.setActiveMission(this);
        this.setStatus("ASSIGNED");
        return true;
    }
    protected void endMission(String nextState){
        this.setStatus(nextState);
        this.colony.removeActiveMission();
    }

    protected void assign(Agent agent){
        agent.setStatus("Busy");
    }
    protected void unassign(Agent agent) {
        agent.setStatus("Available");
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
        this.colony.deductResources(this.resources,(double)timeUnits/requiredTimeUnits);
        if(this.timeTaken == requiredTimeUnits){
            this.endMission("COMPLETED");
        }


    }


}
