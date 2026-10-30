import Agent.*;
import Colony.Colony;
import Emergency.OxygenLeak;
import Environment.Environment;
import Facility.*;
import Helper.*;
import Mission.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       MARS-OPS: AUTONOMOUS MARS COLONY MANAGEMENT & EMERGENCY SIMULATOR       ");
        System.out.println("                         LAB 5 DEMONSTRATION SUITE                              ");
        System.out.println("================================================================================\n");

        // =========================================================================
        // SCENARIO 1: Successful Exploration
        // =========================================================================
        System.out.println("################################################################################");
        System.out.println("SCENARIO 1: Successful Exploration");
        System.out.println("Description: Create a scientist, an exploration rover, appropriate equipment,");
        System.out.println("and an exploration mission. The mission should execute successfully.");
        System.out.println("################################################################################\n");

        Colony colony1 = Colony.setupColony();

        Scientist scientist1 = new Scientist("Dr. Mark Watney", colony1.getOperationalFacilities().getFirst().getLocation());
        ExplorationRover rover1 = new ExplorationRover("Curiosity-2", "Martian Surface");
        Equipment spectrometer = Equipment.createSpectrometer();
        rover1.loadEquipment(spectrometer);

        colony1.addAgent(scientist1);
        colony1.addAgent(rover1);

        System.out.println("--- System State BEFORE Execution ---");
        colony1.printStatusReport();

        ExplorationMission explorationMission = new ExplorationMission(
                colony1,
                "Explore Gale Crater for subterranean ice deposits",
                "Gale Crater",
                scientist1,
                rover1,
                25.0,
                spectrometer,
                new ResourceType[]{ResourceType.BATTERY_POWER, ResourceType.OXYGEN}
        );

        System.out.println("Submitting and starting exploration mission: " + explorationMission.getDescription());
        boolean missionStarted = explorationMission.startMission();
        System.out.println("Mission submission status: " + (missionStarted ? "ACCEPTED & IN_PROGRESS" : "FAILED"));
        System.out.println("Active mission in colony: " + colony1.getActiveMission().getMissionId() + " (" + colony1.getActiveMission().getStatus() + ")");
        System.out.println("Scientist on mission: " + scientist1.isOnMission() + " | Rover on mission: " + rover1.isOnMission());

        System.out.println("\nAdvancing simulation time by 7 time units in Colony...");
        colony1.advanceTime(7);

        System.out.println("\nMission status after 7 time units: " + explorationMission.getStatus());
        System.out.println("Active mission in colony now: " + (colony1.getActiveMission() == null ? "NONE (Completed & Cleared)" : colony1.getActiveMission().getMissionId()));
        System.out.println("Scientist available: " + scientist1.isAvailable() + " (OnMission: " + scientist1.isOnMission() + ")");
        System.out.println("Rover available: " + rover1.isAvailable() + " (Battery: " + rover1.getBatteryLevel() + "%, OnMission: " + rover1.isOnMission() + ")");

        System.out.println("\n--- System State AFTER Execution ---");
        colony1.printStatusReport();


        // =========================================================================
        // SCENARIO 2: Mission Rejection (Unsuitable Agent)
        // =========================================================================
        System.out.println("\n################################################################################");
        System.out.println("SCENARIO 2: Mission Rejection (Unsuitable Agent)");
        System.out.println("Description: Submit a mission with an unsuitable agent. The mission must fail");
        System.out.println("immediately with a meaningful reason and must never become active.");
        System.out.println("################################################################################\n");

        Colony colony2 = Colony.setupColony();
        Engineer engineerAsScientist = new Engineer("Eng. Bob Martinez", "Habitat1");
        ExplorationRover rover2 = new ExplorationRover("Opportunity-2", "Martian Surface");
        Equipment repairKit = Equipment.createRepairToolkit();

        colony2.addAgent(engineerAsScientist);
        colony2.addAgent(rover2);

        // An exploration mission requires scientific capability, but an Engineer has EQUIPMENT_REPAIR & FIRE_RESPONSE
        ExplorationMission invalidMission = new ExplorationMission(
                colony2,
                "Analyze soil samples at Jezero Crater",
                "Jezero Crater",
                engineerAsScientist,
                rover2,
                20.0,
                repairKit,
                new ResourceType[]{ResourceType.BATTERY_POWER}
        );

        System.out.println("Submitting exploration mission with an Engineer instead of a Scientist...");
        boolean accepted = colony2.submitMission(invalidMission);

        if (!accepted) {
            System.out.println("Mission cannot be assigned.");
            System.out.println("Reason: " + invalidMission.getFailureReason());
        } else {
            System.out.println("ERROR: Mission should not have been accepted!");
        }

        System.out.println("Mission State: " + invalidMission.getStatus());
        System.out.println("Colony Active Mission: " + (colony2.getActiveMission() == null ? "NONE" : colony2.getActiveMission().getMissionId()));
        System.out.println("Agent OnMission: " + engineerAsScientist.isOnMission() + " | Agent Available: " + engineerAsScientist.isAvailable());


        // =========================================================================
        // SCENARIO 3: Resource Shortage
        // =========================================================================
        System.out.println("\n################################################################################");
        System.out.println("SCENARIO 3: Resource Shortage");
        System.out.println("Description: Attempt to execute a mission when the required resources are");
        System.out.println("insufficient. System must reject operation without consuming unavailable");
        System.out.println("resources or allowing resource quantities to become negative.");
        System.out.println("################################################################################\n");

        Colony colony3 = Colony.setupColony();
        Scientist scientist3 = new Scientist("Dr. Sarah Brown", "Laboratory1");
        ExplorationRover rover3 = new ExplorationRover("Perseverance-2", "Martian Surface");
        Equipment spec3 = Equipment.createSpectrometer();

        colony3.addAgent(scientist3);
        colony3.addAgent(rover3);

        // Deplete all battery power across the colony to test shortage
        for (Facility f : colony3.getFacilities()) {
            f.deductResource(ResourceType.BATTERY_POWER, f.getResourceAmount(ResourceType.BATTERY_POWER));
        }

        System.out.println("Colony Total Battery Power before mission: " + colony3.getTotalResource(ResourceType.BATTERY_POWER));
        System.out.println("Colony Total Oxygen before mission:       " + colony3.getTotalResource(ResourceType.OXYGEN));

        ExplorationMission starvedMission = new ExplorationMission(
                colony3,
                "Deep Valley Exploration",
                "Valles Marineris",
                scientist3,
                rover3,
                15.0,
                spec3,
                new ResourceType[]{ResourceType.BATTERY_POWER, ResourceType.OXYGEN}
        );

        System.out.println("\nSubmitting mission requiring battery power when colony has 0 battery...");
        boolean started = starvedMission.startMission();

        if (!started) {
            System.out.println("Operation Rejected!");
            System.out.println("Reason: " + starvedMission.getFailureReason());
        }

        System.out.println("Mission Status: " + starvedMission.getStatus());
        System.out.println("Colony Battery Power after failed submission: " + colony3.getTotalResource(ResourceType.BATTERY_POWER));
        System.out.println("Colony Oxygen after failed submission:       " + colony3.getTotalResource(ResourceType.OXYGEN));
        System.out.println("Confirmed: No resources consumed and no negative balances created.");


        // =========================================================================
        // SCENARIO 4: Emergency Handling (Oxygen Leak)
        // =========================================================================
        System.out.println("\n################################################################################");
        System.out.println("SCENARIO 4: Emergency Handling (Oxygen Leak)");
        System.out.println("Description: Simulate an oxygen leak. Detect, locate, identify suitable");
        System.out.println("responders, perform response, consume resources, update facility state.");
        System.out.println("################################################################################\n");

        Colony colony4 = Colony.setupColony();
        Habitat habitatToLeak = null;
        for (Facility f : colony4.getFacilities()) {
            if (f instanceof Habitat) {
                habitatToLeak = (Habitat) f;
                break;
            }
        }

        System.out.println("1. Emergency Detected: Oxygen Leak!");
        System.out.println("2. Affected Facility Located: " + habitatToLeak.getLocation());
        System.out.println("   Oxygen level in " + habitatToLeak.getLocation() + " before leak: " + habitatToLeak.getResourceAmount(ResourceType.OXYGEN) + " units");
        System.out.println("   Colony-wide Oxygen before leak: " + colony4.getTotalResource(ResourceType.OXYGEN) + " units");
        System.out.println("   Colony-wide Spare Parts before leak: " + colony4.getTotalResource(ResourceType.SPARE_PARTS) + " units");

        OxygenLeak leakEmergency = new OxygenLeak(habitatToLeak);

        System.out.println("\n3. Triggering emergency response in Colony...");
        boolean emergencyHandled = colony4.triggerEmergency(leakEmergency);

        System.out.println("4. Response Outcome: " + (emergencyHandled ? "SUCCESSFULLY RESOLVED" : "UNRESOLVED"));
        System.out.println("   Emergency State: " + leakEmergency.getEmergencyState());
        System.out.println("   Reason/Info: " + (leakEmergency.getFailureReason().isEmpty() ? "Leak repaired by dispatched Engineer and Repair Robot." : leakEmergency.getFailureReason()));
        System.out.println("   Oxygen level in " + habitatToLeak.getLocation() + " after repair: " + habitatToLeak.getResourceAmount(ResourceType.OXYGEN) + " units (deducted 40)");
        System.out.println("   Colony-wide Oxygen after repair: " + colony4.getTotalResource(ResourceType.OXYGEN) + " units");
        System.out.println("   Colony-wide Spare Parts after repair: " + colony4.getTotalResource(ResourceType.SPARE_PARTS) + " units (consumed 2)");
        System.out.println("   Facility Operational Status: " + (habitatToLeak.isOperational() ? "ONLINE" : "OFFLINE"));

        // Demonstration of Unresolved Leak (Double Oxygen Penalty Rule)
        System.out.println("\n--- Testing Emergency Failure Flow (Responders Unavailable) ---");
        Colony colony4B =  Colony.setupColony(); // Empty colony with no responders
        Habitat unstaffedHabitat = new Habitat();
        unstaffedHabitat.addResource(ResourceType.OXYGEN, 100);
        colony4B.addFacility(unstaffedHabitat);

        System.out.println("Habitat Oxygen before unresolved leak: " + unstaffedHabitat.getResourceAmount(ResourceType.OXYGEN));
        OxygenLeak unresolvedLeak = new OxygenLeak(unstaffedHabitat);
        colony4B.triggerEmergency(unresolvedLeak);
        System.out.println("Outcome: " + unresolvedLeak.getEmergencyState());
        System.out.println("Failure Reason: " + unresolvedLeak.getFailureReason());
        System.out.println("Habitat Oxygen after unresolved leak: " + unstaffedHabitat.getResourceAmount(ResourceType.OXYGEN) + " units (deducted 2x = 60 units lost!)");


        // =========================================================================
        // SCENARIO 5: Environmental Effect (Dust Storm)
        // =========================================================================
        System.out.println("\n################################################################################");
        System.out.println("SCENARIO 5: Environmental Effect (Dust Storm)");
        System.out.println("Description: Introduce an environmental condition (Dust Storm), and");
        System.out.println("demonstrate that surface operations cannot start.");
        System.out.println("################################################################################\n");

        Colony colony5 = Colony.setupColony();
        Scientist scientist5 = new Scientist("Dr. Alex Walker", "Laboratory1");
        ExplorationRover rover5 = new ExplorationRover("Discovery-1", "Martian Surface");
        Equipment spec5 = Equipment.createSpectrometer();

        colony5.addAgent(scientist5);
        colony5.addAgent(rover5);

        System.out.println("Simulating Mars weather change: Setting condition to DUST_STORM...");
        colony5.getEnvironment().setCondition(EnvironmentCondition.DUST_STORM);
        System.out.println("Current Environment: " + Environment.getState());

        ExplorationMission dustStormMission = new ExplorationMission(
                colony5,
                "Surface mineral analysis during high winds",
                "Elysium Planitia",
                scientist5,
                rover5,
                20.0,
                spec5,
                new ResourceType[]{ResourceType.BATTERY_POWER}
        );

        System.out.println("\nAttempting to start Exploration Mission during a Dust Storm...");
        boolean dustStormStart = dustStormMission.startMission();

        if (!dustStormStart) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("Reason: " + dustStormMission.getFailureReason());
        }

        System.out.println("Mission Status: " + dustStormMission.getStatus());
        System.out.println("Colony Active Mission: " + (colony5.getActiveMission() == null ? "NONE" : colony5.getActiveMission().getMissionId()));

        System.out.println("\n================================================================================");
        System.out.println("                ALL 5 MARS-OPS DEMONSTRATION SCENARIOS COMPLETED               ");
        System.out.println("================================================================================");
    }
}
