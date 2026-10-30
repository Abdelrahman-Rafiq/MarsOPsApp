package Agent;

import Helper.Capability;

public class RepairRobot extends RoboticUnit {
    private static int nextId = 1;

    public RepairRobot() {
        this("RepairRobot" + nextId, "Storage Facility1");
    }

    public RepairRobot(String name, String location) {
        super(name, "RepairRobot" + nextId++, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
        addCapability(Capability.FIRE_RESPONSE);
    }
}
