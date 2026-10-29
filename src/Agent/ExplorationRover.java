package Agent;

import Helper.Capability;
import Mission.Mission;

public class ExplorationRover extends RoboticUnit {

    public ExplorationRover(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.SURFACE_EXPLORATION);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0 || getBatteryLevel() <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}