package Emergency;

import Agent.Agent;
import Agent.ExplorationRover;
import Colony.Colony;
import Helper.Capability;
import Helper.EmergencyState;
import Helper.MissionState;
import Helper.ResourceType;
import Mission.Mission;

public class RoverBreakdown extends Emergency {
    private final ExplorationRover damagedRover;
    private final String breakdownLocation;
    public static final int REQUIRED_SPARE_PARTS = 3;
    public static final int REQUIRED_BATTERY = 10;

    public RoverBreakdown(ExplorationRover rover) {
        super("Rover breakdown at " +  rover.getLocation(),
                rover.getLocation(),
                new Capability[]{Capability.EQUIPMENT_REPAIR});
        this.damagedRover = rover;
        this.breakdownLocation = location;
    }

    @Override
    public boolean handle(Colony colony) {
        this.emergencyState = EmergencyState.TRIGGERED;

        damagedRover.takeDamage(50);

        Mission activeMission = colony.getActiveMission();
        if (activeMission == null ||
                activeMission.getStatus() != MissionState.IN_PROGRESS ||
                !activeMission.getAssignedAgents().contains(damagedRover)) {

            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Rover was not part of an active mission.";
            return false;
        }

        activeMission.fail("Exploration rover broke down during the mission.");
        colony.removeActiveMission();

        // 3. Find suitable repair responder (Engineer or RepairRobot)
        Agent responder = null;
        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isAvailable() && !agent.isOnMission() && agent != damagedRover && agent.canHandle(Capability.EQUIPMENT_REPAIR)) {
                responder = agent;
                break;
            }
        }

        if (responder == null) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "No available repair responder (engineer or repair robot) to dispatch.";
            return false;
        }

        // 4. Check required repair resources
        if (colony.getTotalResource(ResourceType.SPARE_PARTS) < REQUIRED_SPARE_PARTS ||
            colony.getTotalResource(ResourceType.BATTERY_POWER) < REQUIRED_BATTERY) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Insufficient repair resources: spare parts or battery power.";
            return false;
        }

        // 5. Dispatch responder to breakdown location
        responder.setLocation(breakdownLocation);
        colony.deductResource(ResourceType.SPARE_PARTS, REQUIRED_SPARE_PARTS);
        colony.deductResource(ResourceType.BATTERY_POWER, REQUIRED_BATTERY);

        // 6. Restore rover health
        damagedRover.restoreHealth(50);
        this.emergencyState = EmergencyState.RESOLVED;
        this.failureReason = "";
        return true;
    }

    public ExplorationRover getDamagedRover() {
        return damagedRover;
    }
}
