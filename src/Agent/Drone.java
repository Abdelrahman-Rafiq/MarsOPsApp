package Agent;

import Helper.Capability;
import Mission.Mission;

public class Drone extends RoboticUnit {

    public Drone(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.SURFACE_EXPLORATION);
        addCapability(Capability.AERIAL_RECON);
        addCapability(Capability.RESCUE);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0 || getBatteryLevel() <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}