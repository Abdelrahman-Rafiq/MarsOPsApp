package Agent;

import Helper.Capability;
import Helper.Equipment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplorationRover extends Vehicle {
    private static int nextId = 1;
    private final List<Equipment> carriedEquipment;

    public ExplorationRover() {
        this("Rover" + nextId, "Surface");
    }

    public ExplorationRover(String name, String location) {
        super(name, "ExplorationRover" + nextId++, location);
        this.carriedEquipment = new ArrayList<>();
        addCapability(Capability.SURFACE_EXPLORATION);
        addCapability(Capability.RESCUE);
        addCapability(Capability.COMMUNICATION);
    }

    public boolean loadEquipment(Equipment equipment) {
        if (equipment == null || this.carriedEquipment.contains(equipment)) {
            return false;
        }
        return this.carriedEquipment.add(equipment);
    }

    public boolean unloadEquipment(Equipment equipment) {
        if (equipment == null) {
            return false;
        }
        return this.carriedEquipment.remove(equipment);
    }

    public List<Equipment> getCarriedEquipment() {
        return Collections.unmodifiableList(carriedEquipment);
    }

    @Override
    public String toString() {
        return String.format("%s | Cargo: %s",
                super.toString(),
                carriedEquipment.isEmpty() ? "None" : carriedEquipment);
    }
}
