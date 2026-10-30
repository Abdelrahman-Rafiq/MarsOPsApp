package Colony;

import Agent.*;
import Emergency.Emergency;
import Environment.Environment;
import Facility.*;
import Helper.*;
import Mission.*;
import java.util.*;

public class Colony {
    private final List<Agent> agents;
    private final List<Facility> facilities;
    private Mission activeMission;
    private final Environment environment;
    private int currentTime;
    private final List<Emergency> emergencyHistory;
    private final List<Mission> missionHistory;

    private Colony() {
        this.agents = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.activeMission = null;
        this.environment = new Environment();
        this.currentTime = 0;
        this.emergencyHistory = new ArrayList<>();
        this.missionHistory = new ArrayList<>();
    }

    public static Colony setupColony() {
        Colony colony = new Colony();

        Habitat habitat1 = new Habitat();
        Habitat habitat2 = new Habitat();
        Laboratory lab = new Laboratory();
        PowerStation power = new PowerStation();
        StorageFacilities storage = new StorageFacilities();
        CommunicationStation comms = new CommunicationStation();

        colony.addFacility(habitat1);
        colony.addFacility(habitat2);
        colony.addFacility(lab);
        colony.addFacility(power);
        colony.addFacility(storage);
        colony.addFacility(comms);

        return colony;
    }

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
        for(Facility facility : facilities)
            if(facility != null&& facility.getCurrentOccupancy()<facility.getCapacity()){
                facility.addOccupiers();
                agent.setLocation(facility.getLocation());
                break;
            }
    }

    public boolean removeAgent(Agent agent) {
        return agents.remove(agent);
    }

    public List<Agent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    public List<Agent> getAvailableAgents() {
        List<Agent> available = new ArrayList<>();
        for (Agent a : agents) {
            if (a.isAvailable() && !a.isOnMission()) {
                available.add(a);
            }
        }
        return Collections.unmodifiableList(available);
    }

    public void addFacility(Facility facility) {
        if (facility != null && !facilities.contains(facility)) {
            facilities.add(facility);
        }
    }

    public List<Facility> getFacilities() {
        return Collections.unmodifiableList(facilities);
    }

    public List<Facility> getOperationalFacilities() {
        List<Facility> operational = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.isOperational()) {
                operational.add(f);
            }
        }
        return Collections.unmodifiableList(operational);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getTotalResource(ResourceType type) {
        if (type == null) return 0;
        int total = 0;
        for (Facility f : facilities) {
            if (f != null && f.isOperational()) {
                total += f.getResourceAmount(type);
            }
        }
        return total;
    }

    public boolean checkAvailableResources(ResourceType[] types, int requiredPerType) {
        if (types == null || types.length == 0) return false;
        for (ResourceType type : types) {
            if (getTotalResource(type) < requiredPerType) {
                return false;
            }
        }
        return true;
    }

    public boolean checkAvailableResources(Map<ResourceType, Integer> required) {
        if (required == null || required.isEmpty()) return true;
        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            if (getTotalResource(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // Rule 2 & 3: Multi-source, all-or-nothing consumption
    // Rule 6: Resource allocation policy: draw from StorageFacilities first, then other operational facilities
    public boolean deductResource(ResourceType type, int amount) {
        if (type == null || amount <= 0) return false;
        if (getTotalResource(type) < amount) {
            return false; // All-or-nothing: do not deduct partial!
        }

        int remaining = amount;

        for (Facility f : facilities) {
            if (f instanceof StorageFacilities && f.isOperational()) {
                int deducted = f.deductResource(type, remaining);
                remaining -= deducted;
                if (remaining == 0) return true;
            }
        }

        for (Facility f : facilities) {
            if (!(f instanceof StorageFacilities) && f != null && f.isOperational()) {
                int deducted = f.deductResource(type, remaining);
                remaining -= deducted;
                if (remaining == 0) return true;
            }
        }

        return true;
    }

    public boolean deductResource(ResourceType[] types, int amountPerType) {
        if (types == null || types.length == 0) return true;
        // Verify all-or-nothing
        for (ResourceType type : types) {
            if (getTotalResource(type) < amountPerType) {
                return false;
            }
        }
        // Deduct
        for (ResourceType type : types) {
            deductResource(type, amountPerType);
        }
        return true;
    }

    // Equipment Check
    public boolean checkAvailableEquipment(Equipment equipment) {
        if (equipment == null || equipment.isDamaged() || !equipment.isAvailable())
            return false;

        // Check in facilities
        for (Facility f : facilities)
            if (f != null && f.isOperational() && f.getEquipments().contains(equipment))
                return true;

        // Check on vehicles/rovers
        for (Agent a : agents)
            if (a instanceof ExplorationRover)
                if (((ExplorationRover) a).getCarriedEquipment().contains(equipment))
                    return true;
        return false;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public void setActiveMission(Mission mission) {
        this.activeMission = mission;
    }

    public void addMissionToHistory(Mission mission) {
        if (mission != null && !missionHistory.contains(mission)) {
            missionHistory.add(mission);
        }
    }

    public List<Mission> getMissionHistory() {
        return Collections.unmodifiableList(missionHistory);
    }

    public void removeActiveMission() {
        if (this.activeMission != null) {
            addMissionToHistory(this.activeMission);
            this.activeMission = null;
        }
    }

    public boolean submitMission(Mission mission) {
        if (mission == null) {
            return false;
        }

        // Rule 1: One active mission at a time
        if (this.activeMission != null) {
            mission.setStatus(MissionState.FAILED);
            mission.setFailureReason("Another mission is active in the colony right now.");
            addMissionToHistory(mission);
            return false;
        }

        // Perform mission-specific validation
        if (!mission.submitMission()) {
            mission.setStatus(MissionState.FAILED);
            addMissionToHistory(mission);
            return false;
        }

        // Accepted: becomes ASSIGNED and remains active mission
        this.activeMission = mission;
        mission.setStatus(MissionState.ASSIGNED);
        return true;
    }

    public boolean startActiveMission() {
        if (this.activeMission == null || this.activeMission.getStatus() != MissionState.ASSIGNED) {
            return false;
        }
        boolean started = this.activeMission.startMission();
        if (!started) {
            this.activeMission.setStatus(MissionState.FAILED);
            removeActiveMission();
        }
        return started;
    }

    // Simulation & Time Management (Section 10)
    public void advanceTime(int timeUnits) {
        if (timeUnits <= 0) return;

        for (int t = 0; t < timeUnits; t++) {
            this.currentTime++;

            for(Agent a : agents) {
                if (a.isHuman()&&!a.isOnMission()) {
                    ((CrewMember)a).rest(timeUnits);
                }
            }
            // 1. Advance Active Mission (if IN_PROGRESS)
            if (activeMission != null && activeMission.getStatus() == MissionState.IN_PROGRESS) {
                // Dynamic Environmental disruption check
                if (activeMission instanceof ExplorationMission && Environment.getState() == EnvironmentCondition.DUST_STORM) {
                    activeMission.fail("Active rover exploration mission failed due to Martian dust storm.");
                    removeActiveMission();
                } else if (activeMission instanceof RescueMission && Environment.getState() == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
                    activeMission.fail("Active rescue mission failed due to severe communication disruption.");
                    removeActiveMission();
                } else {
                    activeMission.progress(1);

                    // Moderate fatigue and battery consumption for assigned agents
                    for (Agent agent : activeMission.getAssignedAgents()) {
                        if (agent instanceof RoboticUnit) {
                            ((RoboticUnit) agent).discharge(5);
                        } else if (agent instanceof CrewMember) {
                            ((CrewMember) agent).increaseFatigue(5);
                        }
                    }
                    if (activeMission.isCompleted()) {
                        activeMission.complete();
                        removeActiveMission();
                    }
                }
            }

            // 2. Facilities upkeep and agent recovery
            for (Facility facility : facilities) {
                if (facility != null && facility.isOperational())
                    facility.useResources();
                for (Agent a : agents)
                    if (a != null && a.isHuman() && a.getLocation().equals(facility.getLocation()))
                        facility.deductResource(ResourceType.OXYGEN, 2);
                if (facility instanceof Habitat)
                    for (Agent a : agents)
                        if (a != null && a.isHuman() && a.getLocation().equals(facility.getLocation())) {
                            ((CrewMember) a).eat((Habitat) facility, 5);
                            ((CrewMember) a).drink((Habitat) facility, 5);
                        }
                if (facility instanceof PowerStation)
                    for (Agent a : agents)
                        if (a != null && a.isRobot() && a.getLocation().equals(facility.getLocation()))
                            ((RoboticUnit) a).charge((PowerStation) facility, 5);
            }
        }
    }

    // Emergency Management (Section 9)
    public boolean triggerEmergency(Emergency emergency) {
        if (emergency == null) return false;
        emergencyHistory.add(emergency);
        return emergency.handle(this);
    }

    public List<Emergency> getEmergencyHistory() {
        return Collections.unmodifiableList(emergencyHistory);
    }

    // Colony Status Report (Section 2, Section 5.1 #5, Section 11.6)
    public String generateStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append(String.format("                      MARS-OPS COLONY STATUS REPORT (Sol / Time: %d)\n", currentTime));
        sb.append("========================================================================================\n");
        sb.append(String.format("Environment Condition: %s\n", Environment.getState()));
        sb.append(String.format("Active Mission:        %s\n", (activeMission != null ? activeMission.getMissionId() + " [" + activeMission.getStatus() + "]" : "NONE")));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append("COLONY-WIDE RESOURCE TOTALS (Operational Facilities Only):\n");
        for (ResourceType type : ResourceType.values()) {
            sb.append(String.format("  %-18s: %4d units\n", type.name(), getTotalResource(type)));
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("FACILITIES (%d registered):\n", facilities.size()));
        for (Facility f : facilities) {
            sb.append("  ").append(f.toString()).append("\n");
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("PERSONNEL & ROBOTS (%d registered):\n", agents.size()));
        for (Agent a : agents) {
            sb.append("  ").append(a.toString()).append("\n");
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("Emergency History (%d Emergencies):\n", emergencyHistory.size()));
        if (!emergencyHistory.isEmpty()) {
            for (Emergency emergency : emergencyHistory) {
                if (emergency != null) {
                    sb.append("  ").append(emergency.toString()).append("\n");
                }
            }
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("Mission History (%d Missions):\n", missionHistory.size()));
        if (!missionHistory.isEmpty()) {
            for (Mission m : missionHistory) {
                if (m != null) {
                    sb.append("  ").append(m.toString()).append("\n");
                }
            }
        }
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    public void printStatusReport() {
        System.out.print(generateStatusReport());
    }
}