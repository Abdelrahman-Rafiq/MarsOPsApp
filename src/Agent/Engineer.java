package Agent;

import Helper.Capability;

public class Engineer extends CrewMember {
    private static int nextId = 1;

    public Engineer() {
        this("Engineer" + nextId, "Habitat1");
    }

    public Engineer(String name, String location) {
        super(name, "Engineer" + nextId++, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
    }
}
