package Agent;

import Helper.Capability;
import Mission.Mission;


public class RepairRobot extends RoboticUnit {

    public RepairRobot(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0 || getBatteryLevel() <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}