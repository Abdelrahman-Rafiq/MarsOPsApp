package Agent;

import Helper.Capability;
import Mission.Mission;


public class Scientist extends CrewMember {

    public Scientist(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.SCIENTIFIC_RESEARCH);
        addCapability(Capability.SURFACE_EXPLORATION);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}