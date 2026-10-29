package Agent;

import Helper.Capability;
import Mission.Mission;


public class MedicalOfficer extends CrewMember {

    public MedicalOfficer(String name, String id, String location) {
        super(name, id, location);
        addCapability(Capability.RESCUE);
        addCapability(Capability.MEDICAL_CARE);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.availability || this.healthStatus <= 0) {
            return false;
        }
        return hasCapability(mission.getRequiredCapability());
    }
}