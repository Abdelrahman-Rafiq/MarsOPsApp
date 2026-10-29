package Agent;

import Helper.Capability;
import Mission.Mission;

/**
 * Engineer crew member specialized in mechanical and electrical repairs.
 */
public class Engineer extends CrewMember {

    public Engineer(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}