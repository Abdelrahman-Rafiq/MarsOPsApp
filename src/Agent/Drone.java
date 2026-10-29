package Agent;

import Helper.Capability;

public class Drone extends Vehicle {
    private static int nextId = 1;

    public Drone(String name, String location) {
        super(name, "Drone" + nextId++, location);
        addCapability(Capability.SURFACE_EXPLORATION);
        addCapability(Capability.AERIAL_RECON);
        addCapability(Capability.RESCUE);
        addCapability(Capability.COMMUNICATION);
    }
}