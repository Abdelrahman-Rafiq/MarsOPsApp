package Emergency;

import Agent.Agent;
import Colony.Colony;
import Facility.Habitat;
import Helper.Capability;
import Helper.EmergencyState;
import Helper.ResourceType;

public class OxygenLeak extends Emergency {
    private final Habitat affectedHabitat;
    private static final int leakAmount=50;
    public static final int REQUIRED_SPARE_PARTS = 2;
    public static final int REQUIRED_BATTERY = 5;

    public OxygenLeak(Habitat habitat) {
        super("Oxygen leak in " + (habitat != null ? habitat.getLocation() : "Unknown Habitat"),
                (habitat != null ? habitat.getLocation() : "Unknown"),
                new Capability[]{Capability.EQUIPMENT_REPAIR});
        this.affectedHabitat = habitat;
        }

    @Override
    public boolean handle(Colony colony) {
        this.emergencyState = EmergencyState.TRIGGERED;

        affectedHabitat.deductResource(ResourceType.OXYGEN, leakAmount);

        // 2. Determine appropriate responders (ask agents polymorphically; do not check concrete class!)
        Agent humanRepairer = null;
        Agent robotRepairer = null;

        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isAvailable() && !agent.isOnMission() && agent.canHandle(Capability.EQUIPMENT_REPAIR)) {
                if (agent.isHuman() && humanRepairer == null) {
                    humanRepairer = agent;
                } else if (agent.isRobot() && robotRepairer == null) {
                    robotRepairer = agent;
                }
            }
        }

        // 3. Check responder availability
        if (humanRepairer == null || robotRepairer == null) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Required responders unavailable: needs both human and robot repair capability.";
            // Unresolved leak costs twice as much oxygen: deduct initial amount again
            affectedHabitat.deductResource(ResourceType.OXYGEN, leakAmount);
            return false;
        }

        // 4. Check required response resources
        if (colony.getTotalResource(ResourceType.SPARE_PARTS) < REQUIRED_SPARE_PARTS ||
            colony.getTotalResource(ResourceType.BATTERY_POWER) < REQUIRED_BATTERY) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Insufficient response resources: requires spare parts and battery power.";
            // Unresolved leak costs twice as much oxygen: deduct initial amount again
            affectedHabitat.deductResource(ResourceType.OXYGEN, leakAmount);
            return false;
        }

        // 5. Dispatch available responders (move to affected habitat)
        humanRepairer.setLocation(affectedHabitat.getLocation());
        robotRepairer.setLocation(affectedHabitat.getLocation());

        // 6. Consume required response resources (multi-source, all-or-nothing)
        colony.deductResource(ResourceType.SPARE_PARTS, REQUIRED_SPARE_PARTS);
        colony.deductResource(ResourceType.BATTERY_POWER, REQUIRED_BATTERY);

        // 7. Repair leak and update state of habitat
        affectedHabitat.setOperationalStatus(true);
        this.emergencyState = EmergencyState.RESOLVED;
        this.failureReason = "";
        return true;
    }

    public Habitat getAffectedHabitat() {
        return affectedHabitat;
    }

    public int getLeakAmount() {
        return leakAmount;
    }
}
