package Emergency;

import Agent.Agent;
import Colony.Colony;
import Facility.Laboratory;
import Helper.Capability;
import Helper.EmergencyState;
import Helper.Equipment;
import Helper.ResourceType;

public class Fire extends Emergency {
    private final Laboratory affectedLaboratory;
    public static final int REQUIRED_WATER = 10;

    public Fire(Laboratory laboratory) {
        super("Fire outbreak in " + (laboratory != null ? laboratory.getLocation() : "Unknown Laboratory"),
                  laboratory.getLocation(), new Capability[]{Capability.FIRE_RESPONSE});
        this.affectedLaboratory = laboratory;
    }

    @Override
    public boolean handle(Colony colony) {
        this.emergencyState = EmergencyState.TRIGGERED;

        affectedLaboratory.setHasFire(true);
        affectedLaboratory.setOperationalStatus(false);
        affectedLaboratory.deductResource(ResourceType.OXYGEN, 30);

        // Damage equipment in the laboratory they become unusable
        for (Equipment eq : affectedLaboratory.getEquipments()) {
            if(eq.getName().equals("Chemical Fire Suppressor"))
                continue;
            for(int i=0;i<7;i++)
                eq.useIt();
        }

        // Identify available fire response responder
        Agent responder = null;
        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isAvailable() && !agent.isOnMission() && agent.canHandle(Capability.FIRE_RESPONSE)) {
                responder = agent;
                break;
            }
        }
        // Inside Fire.handle(Colony colony), right after deducting OXYGEN:
        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isHuman() && affectedLaboratory.getLocation().equals(agent.getLocation())) {
                agent.takeDamage(20);
            }
        }

        if (responder == null) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "No available fire-response personnel or robotic unit.";
            return false;
        }

        int extIndex = affectedLaboratory.getEquipmentIndex(Capability.FIRE_RESPONSE);
        if (extIndex == -1 || !affectedLaboratory.getEquipments().get(extIndex).useIt()) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Fire suppression equipment is missing or damaged.";
            return false;
        }

        if (colony.getTotalResource(ResourceType.WATER) < REQUIRED_WATER) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Insufficient water in the colony to suppress fire.";
            return false;
        }

        responder.setLocation(affectedLaboratory.getLocation());
        colony.deductResource(ResourceType.WATER, REQUIRED_WATER);

        affectedLaboratory.setHasFire(false);
        affectedLaboratory.setOperationalStatus(true);
        this.emergencyState = EmergencyState.RESOLVED;
        this.failureReason = "";
        return true;
    }

    public Laboratory getAffectedLaboratory() {
        return affectedLaboratory;
    }
}
