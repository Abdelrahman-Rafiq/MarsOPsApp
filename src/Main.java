import Agent.*;
import Colony.Colony;
import Emergency.OxygenLeak;
import Environment.Environment;
import Facility.*;
import Helper.*;
import Mission.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== MARS-OPS MINIMAL DEMONSTRATION SUITE ===\n");

        // ---------------------------------------------------------------------
        // SCENARIO 1: Successful Exploration
        // ---------------------------------------------------------------------
        System.out.println("--- SCENARIO 1: Successful Exploration ---");
        Colony colony = Colony.setupColony();

        Scientist scientist = new Scientist("Dr. Mark Watney", "Habitat1");
        ExplorationRover rover = new ExplorationRover("Curiosity-2", "Surface");
        Engineer engineer = new Engineer("Eng. Tarek", "Habitat1");
        RepairRobot repairBot = new RepairRobot("FixBot-1", "Storage Facility1");

        Equipment spectrometer = Equipment.createSpectrometer();
        rover.loadEquipment(spectrometer);

        colony.addAgent(scientist);
        colony.addAgent(rover);
        colony.addAgent(engineer);
        colony.addAgent(repairBot);

        System.out.println("[Before Mission]");
        colony.printStatusReport();

        ExplorationMission expMission = new ExplorationMission(
                colony, "Explore Gale Crater", "Gale Crater",
                scientist, rover, 25.0, spectrometer,
                new ResourceType[]{ResourceType.BATTERY_POWER, ResourceType.OXYGEN}
        );

        expMission.startMission();
        System.out.println("Mission Started: " + expMission.getStatus());
        colony.advanceTime(7);
        System.out.println("Mission After 7 Sols: " + expMission.getStatus());

        System.out.println("\n[After Mission]");
        colony.printStatusReport();

        // ---------------------------------------------------------------------
        // SCENARIO 2: Mission Rejection (Unsuitable Agent)
        // ---------------------------------------------------------------------
        System.out.println("\n--- SCENARIO 2: Mission Rejection (Unsuitable Agent) ---");
        ExplorationMission invalidMission = new ExplorationMission(
                colony, "Soil Analysis", "Jezero Crater",
                engineer, rover, 20.0, spectrometer,
                new ResourceType[]{ResourceType.BATTERY_POWER}
        );

        if (!colony.submitMission(invalidMission)) {
            System.out.println("Mission cannot be assigned.");
            System.out.println("Reason: " + invalidMission.getFailureReason());
            System.out.println("Status: " + invalidMission.getStatus() + " | Active Mission: " + colony.getActiveMission());
        }

        // ---------------------------------------------------------------------
        // SCENARIO 3: Resource Shortage
        // ---------------------------------------------------------------------
        System.out.println("\n--- SCENARIO 3: Resource Shortage ---");
        Colony starvedColony = Colony.setupColony();
        starvedColony.addAgent(scientist);
        starvedColony.addAgent(rover);

        for (Facility f : starvedColony.getFacilities()) {
            f.deductResource(ResourceType.BATTERY_POWER, f.getResourceAmount(ResourceType.BATTERY_POWER));
        }

        ExplorationMission resourceFailMission = new ExplorationMission(
                starvedColony, "Deep Valley Recon", "Valles Marineris",
                scientist, rover, 15.0, spectrometer,
                new ResourceType[]{ResourceType.BATTERY_POWER, ResourceType.OXYGEN}
        );

        if (!resourceFailMission.startMission()) {
            System.out.println("Mission Rejected! Status: " + resourceFailMission.getStatus());
            System.out.println("Reason: " + resourceFailMission.getFailureReason());
            System.out.println("Remaining Battery: " + starvedColony.getTotalResource(ResourceType.BATTERY_POWER)
                    + " | Remaining Oxygen: " + starvedColony.getTotalResource(ResourceType.OXYGEN));
        }

        // ---------------------------------------------------------------------
        // SCENARIO 4: Emergency Handling (Oxygen Leak)
        // ---------------------------------------------------------------------
        System.out.println("\n--- SCENARIO 4: Emergency Handling (Oxygen Leak) ---");
        Habitat targetHabitat = (Habitat) colony.getFacilities().get(0);
        System.out.println("Oxygen in " + targetHabitat.getLocation() + " before leak: "
                + targetHabitat.getResourceAmount(ResourceType.OXYGEN));

        OxygenLeak leak = new OxygenLeak(targetHabitat);
        boolean resolved = colony.triggerEmergency(leak);

        System.out.println("Emergency State: " + leak.getEmergencyState() + " (Resolved: " + resolved + ")");
        System.out.println("Oxygen in " + targetHabitat.getLocation() + " after leak: "
                + targetHabitat.getResourceAmount(ResourceType.OXYGEN));

        // ---------------------------------------------------------------------
        // SCENARIO 5: Environmental Effect (Dust Storm)
        // ---------------------------------------------------------------------
        System.out.println("\n--- SCENARIO 5: Environmental Effect (Dust Storm) ---");
        colony.getEnvironment().setCondition(EnvironmentCondition.DUST_STORM);

        ExplorationMission stormMission = new ExplorationMission(
                colony, "Surface Mineral Scan", "Elysium Planitia",
                scientist, rover, 20.0, spectrometer,
                new ResourceType[]{ResourceType.BATTERY_POWER}
        );

        if (!stormMission.startMission()) {
            System.out.println("Exploration Mission cannot start.");
            System.out.println("Reason: " + stormMission.getFailureReason());
            System.out.println("Status: " + stormMission.getStatus());
        }
    }
}