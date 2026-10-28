package Emergency;

import Helper.Capability;
import Helper.EmergencyState;

abstract public class Emergency {
    protected final String emergencyId;
    protected final String location;
    protected Capability[] requiredCapability;
    protected EmergencyState emergencyState;
    private static int nextId=0;
    protected Emergency(String Location,Capability[] requiredCapability) {
        emergencyId = String.valueOf(nextId++);
        location = Location;
        this.requiredCapability = requiredCapability.clone();
        emergencyState = EmergencyState.TRIGGERED;
    }
    abstract public void handle(MarsColony colony);
    public String displayEmergency(){
        return String.format("Id:%s, Emergency Location:%s, Emergency State:%s", emergencyId,location,emergencyState);
    }
}
