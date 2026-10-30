package Agent;

import Helper.Capability;

public class MedicalOfficer extends CrewMember {
    private static int nextId = 1;

    public MedicalOfficer() {
        this("MedicalOfficer" + nextId, "Habitat1");
    }

    public MedicalOfficer(String name, String location) {
        super(name, "MedicalOfficer" + nextId++, location);
        addCapability(Capability.MEDICAL_CARE);
        addCapability(Capability.RESCUE);
    }
}
