package Agent;

import Helper.Capability;

public class Scientist extends CrewMember {
    private static int nextId = 1;

    public Scientist(String name, String location) {
        super(name, "Scientist" + nextId++, location);
        addCapability(Capability.SCIENTIFIC_RESEARCH);
        addCapability(Capability.SURFACE_EXPLORATION);
    }
}