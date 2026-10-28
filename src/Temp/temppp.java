package Temp;

import Emergency.Emergency;
import Helper.*;

import java.util.*;


class LifeSupportSystem {
    private boolean functional;
    private int filtrationEfficiency;

    public LifeSupportSystem() {
        this.functional = true;
        this.filtrationEfficiency = 100;
    }

    public boolean isFunctional() { return functional; }
    public void setFunctional(boolean functional) {
        this.functional = functional;
        this.filtrationEfficiency = functional ? 100 : 40;
    }
    public int getFiltrationEfficiency() { return filtrationEfficiency; }
}
// ==========================================
// 3. FACILITIES HIERARCHY
// ==========================================

abstract class Facility {
    protected String name;
    protected String location;
    protected int capacity;
    protected boolean operational;
    protected Map<ResourceType, Integer> resources;
    protected List<Equipment> equipmentList; // Aggregation

    public Facility(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.operational = true;
        this.resources = new EnumMap<>(ResourceType.class);
        this.equipmentList = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational) { this.operational = operational; }
    public List<Equipment> getEquipmentList() { return equipmentList; }

    public void addEquipment(Equipment eq) {
        equipmentList.add(eq);
    }

    public void addResource(ResourceType type, int amount) {
        if (amount < 0) return;
        resources.put(type, getResourceQuantity(type) + amount);
    }

    public int getResourceQuantity(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    // Deducts up to available amount (never drops below zero)
    public int deductResourceUpTo(ResourceType type, int amount) {
        int current = getResourceQuantity(type);
        int deducted = Math.min(current, Math.max(0, amount));
        resources.put(type, current - deducted);
        return deducted;
    }

    public Map<ResourceType, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Operational: %s | Resources: %s | Equipment: %s",
                name, getClass().getSimpleName(), operational, resources, equipmentList);
    }
}

class Habitat extends Facility {
    // Composition: Habitat owns LifeSupportSystem lifecycle
    private final LifeSupportSystem lifeSupport;

    public Habitat(String name, String location, int capacity) {
        super(name, location, capacity);
        this.lifeSupport = new LifeSupportSystem();
    }

    public LifeSupportSystem getLifeSupport() { return lifeSupport; }
}

class Laboratory extends Facility {
    public Laboratory(String name, String location, int capacity) {
        super(name, location, capacity);
    }
}

class StorageFacility extends Facility {
    public StorageFacility(String name, String location, int capacity) {
        super(name, location, capacity);
    }
}

class PowerStation extends Facility {
    public PowerStation(String name, String location, int capacity) {
        super(name, location, capacity);
    }
}

class CommunicationStation extends Facility {
    public CommunicationStation(String name, String location, int capacity) {
        super(name, location, capacity);
    }
}
// ==========================================
// 4. AGENTS HIERARCHY (HUMAN & ROBOTIC)
// ==========================================

abstract class Agent {
    protected String id;
    protected String name;
    protected String currentLocation;
    protected int health; // 0 to 100
    protected boolean available;
    protected Set<Capability> capabilities;

    public Agent(String id, String name, String currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.health = 100;
        this.available = true;
        this.capabilities = EnumSet.noneOf(Capability.class);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, Math.min(100, health)); }
    public boolean isAvailable() { return available && isUsable(); }
    public void setAvailable(boolean available) { this.available = available; }
    public Set<Capability> getCapabilities() { return Collections.unmodifiableSet(capabilities); }

    public void addCapability(Capability cap) { capabilities.add(cap); }
    public boolean hasCapability(Capability cap) { return capabilities.contains(cap); }

    public abstract boolean isUsable();
    public abstract boolean isHuman();
    public abstract void onTimeStep(boolean onMission);

    // Polymorphic capability check for Missions (no instanceof checks)
    public boolean canPerform(Mission mission) {
        if (!isAvailable()) return false;
        for (Capability req : mission.getRequiredCapabilities()) {
            if (this.hasCapability(req)) {
                return hasSufficientEnduranceFor(mission);
            }
        }
        return false;
    }

    protected abstract boolean hasSufficientEnduranceFor(Mission mission);

    // Polymorphic capability check for Emergencies (no instanceof checks)
    public boolean canRespondTo(Emergency emergency) {
        if (!isAvailable()) return false;
        for (Capability req : emergency.getRequiredCapabilities()) {
            if (this.hasCapability(req)) {
                return true;
            }
        }
        return false;
    }

    public boolean affectedByEnvironment(EnvironmentCondition condition) {
        return false;
    }
}

abstract class CrewMember extends Agent {
    protected int fatigue; // 0 to 100 (100 = exhausted)

    public CrewMember(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        this.fatigue = 0;
    }

    public int getFatigue() { return fatigue; }
    public void setFatigue(int fatigue) { this.fatigue = Math.max(0, Math.min(100, fatigue)); }

    @Override
    public boolean isUsable() {
        return health > 25 && fatigue < 90;
    }

    @Override
    public boolean isHuman() { return true; }

    @Override
    protected boolean hasSufficientEnduranceFor(Mission mission) {
        return (fatigue + mission.getDuration() * 10) <= 100;
    }

    @Override
    public void onTimeStep(boolean onMission) {
        if (onMission) {
            setFatigue(fatigue + 10);
        } else {
            setFatigue(fatigue - 5);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s - %s] | Loc: %s | Health: %d | Fatigue: %d | Avail: %s | Caps: %s",
                name, id, getClass().getSimpleName(), currentLocation, health, fatigue, isAvailable(), capabilities);
    }
}

class Scientist extends CrewMember {
    public Scientist(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.SCIENTIFIC_RESEARCH);
    }
}

class Engineer extends CrewMember {
    public Engineer(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.EQUIPMENT_REPAIR);
        addCapability(Capability.FIRE_RESPONSE);
    }
}

class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.MEDICAL_CARE);
        addCapability(Capability.FIRE_RESPONSE);
    }
}

abstract class Robot extends Agent {
    protected int batteryLevel; // 0 to 100
    protected boolean damaged;

    public Robot(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        this.batteryLevel = 100;
        this.damaged = false;
    }

    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
    }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
        if (damaged) this.health = Math.min(this.health, 30);
        else this.health = 100;
    }

    @Override
    public boolean isUsable() {
        return !damaged && batteryLevel >= 15 && health > 0;
    }

    @Override
    public boolean isHuman() { return false; }

    @Override
    protected boolean hasSufficientEnduranceFor(Mission mission) {
        return batteryLevel >= (mission.getDuration() * 15);
    }

    @Override
    public void onTimeStep(boolean onMission) {
        if (onMission) {
            setBatteryLevel(batteryLevel - 15);
        } else if (!damaged) {
            setBatteryLevel(batteryLevel + 10);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s - %s] | Loc: %s | Battery: %d%% | Damaged: %s | Avail: %s | Caps: %s",
                name, id, getClass().getSimpleName(), currentLocation, batteryLevel, damaged, isAvailable(), capabilities);
    }
}

class ExplorationRover extends Robot {
    public ExplorationRover(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.SURFACE_EXPLORATION);
        addCapability(Capability.COMMUNICATION);
    }

    @Override
    public boolean affectedByEnvironment(EnvironmentCondition condition) {
        return condition == EnvironmentCondition.DUST_STORM;
    }
}

class RepairRobot extends Robot {
    public RepairRobot(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.EQUIPMENT_REPAIR);
        addCapability(Capability.FIRE_RESPONSE);
    }
}

class Drone extends Robot {
    public Drone(String id, String name, String currentLocation) {
        super(id, name, currentLocation);
        addCapability(Capability.AERIAL_RECON);
        addCapability(Capability.COMMUNICATION);
    }

    @Override
    public boolean affectedByEnvironment(EnvironmentCondition condition) {
        return condition == EnvironmentCondition.COMMUNICATION_DISRUPTION
                || condition == EnvironmentCondition.DUST_STORM;
    }
}
// ==========================================
// 5. MISSIONS HIERARCHY
// ==========================================

abstract class Mission {
    protected String id;
    protected String name;
    protected int duration;
    protected int elapsedTime;
    protected MissionState state;
    protected String failureReason;
    protected Map<ResourceType, Integer> requiredResources;
    protected Set<Capability> requiredCapabilities;
    protected List<Agent> assignedAgents; // Association
    protected Facility targetFacility;    // Association (optional)

    public Mission(String id, String name, int duration, Facility targetFacility) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.elapsedTime = 0;
        this.targetFacility = targetFacility;
        this.requiredResources = new EnumMap<>(ResourceType.class);
        this.requiredCapabilities = EnumSet.noneOf(Capability.class);
        this.assignedAgents = new ArrayList<>();
        this.failureReason = "None";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public int getElapsedTime() { return elapsedTime; }
    public MissionState getState() { return state; }
    public String getFailureReason() { return failureReason; }
    public Map<ResourceType, Integer> getRequiredResources() { return requiredResources; }
    public Set<Capability> getRequiredCapabilities() { return requiredCapabilities; }
    public List<Agent> getAssignedAgents() { return assignedAgents; }
    public Facility getTargetFacility() { return targetFacility; }

    public void addRequiredResource(ResourceType type, int amount) {
        requiredResources.put(type, amount);
    }

    public abstract String checkEnvironmentRestriction(EnvironmentCondition env);
    public abstract String validateTargetFacility();
    public abstract void onMissionStart();
    public abstract void onMissionComplete();

    public void markAssigned(List<Agent> agents) {
        this.assignedAgents.clear();
        this.assignedAgents.addAll(agents);
        for (Agent a : agents) {
            a.setAvailable(false);
        }
        this.state = MissionState.ASSIGNED;
        onMissionStart();
    }

    public void markFailed(String reason) {
        this.state = MissionState.FAILED;
        this.failureReason = reason;
        releaseAgents();
    }

    public void advanceOneStep(EnvironmentCondition env) {
        if (state == MissionState.ASSIGNED) {
            state = MissionState.IN_PROGRESS;
        }
        if (state != MissionState.IN_PROGRESS) return;

        // Check environmental impact during active execution
        String envError = checkEnvironmentRestriction(env);
        if (envError != null) {
            markFailed(envError);
            return;
        }

        // Check agent health, damage, and battery during mission
        for (Agent a : assignedAgents) {
            a.onTimeStep(true);
            if (a.affectedByEnvironment(env)) {
                markFailed("Agent " + a.getName() + " cannot operate under " + env);
                return;
            }
            if (!a.isUsable()) {
                markFailed("Agent " + a.getName() + " became unusable (low battery/damaged/fatigued) during execution.");
                return;
            }
        }

        elapsedTime++;
        if (elapsedTime >= duration) {
            state = MissionState.COMPLETED;
            onMissionComplete();
            releaseAgents();
        }
    }

    private void releaseAgents() {
        for (Agent a : assignedAgents) {
            a.setAvailable(true);
        }
    }

    @Override
    public String toString() {
        return String.format("Mission [%s: %s] | State: %s | Progress: %d/%d | Reason: %s",
                id, name, state, elapsedTime, duration, failureReason);
    }
}

class ExplorationMission extends Mission {
    private String region;

    public ExplorationMission(String id, String name, String region, int duration) {
        super(id, name, duration, null);
        this.region = region;
        requiredCapabilities.add(Capability.SCIENTIFIC_RESEARCH);
        requiredCapabilities.add(Capability.SURFACE_EXPLORATION);
    }

    @Override
    public String checkEnvironmentRestriction(EnvironmentCondition env) {
        if (env == EnvironmentCondition.DUST_STORM) {
            return "Surface operations are not allowed during a dust storm.";
        }
        return null;
    }

    @Override
    public String validateTargetFacility() { return null; }

    @Override
    public void onMissionStart() {
        for (Agent a : assignedAgents) {
            a.setCurrentLocation("Martian Surface (" + region + ")");
        }
    }

    @Override
    public void onMissionComplete() {
        for (Agent a : assignedAgents) {
            a.setCurrentLocation("Base Camp");
        }
    }
}

class RepairMission extends Mission {
    private Equipment targetEquipment;

    public RepairMission(String id, String name, int duration, Facility targetFacility, Equipment targetEquipment) {
        super(id, name, duration, targetFacility);
        this.targetEquipment = targetEquipment;
        requiredCapabilities.add(Capability.EQUIPMENT_REPAIR);
    }

    @Override
    public String checkEnvironmentRestriction(EnvironmentCondition env) { return null; }

    @Override
    public String validateTargetFacility() {
        if (targetFacility == null) return "Repair mission requires a target facility.";
        return null;
    }

    @Override
    public void onMissionStart() {
        for (Agent a : assignedAgents) {
            a.setCurrentLocation(targetFacility.getName());
        }
    }

    @Override
    public void onMissionComplete() {
        targetFacility.setOperational(true);
        if (targetEquipment != null) {
            targetEquipment.useIt();
        }
    }
}

class ResearchMission extends Mission {
    public ResearchMission(String id, String name, int duration, Laboratory lab) {
        super(id, name, duration, lab);
        requiredCapabilities.add(Capability.SCIENTIFIC_RESEARCH);
    }

    @Override
    public String checkEnvironmentRestriction(EnvironmentCondition env) { return null; }

    @Override
    public String validateTargetFacility() {
        if (targetFacility == null || !targetFacility.isOperational()) {
            return "Target laboratory is unavailable or non-operational.";
        }
        boolean hasWorkingSciEquipment = false;
        for (Equipment eq : targetFacility.getEquipmentList()) {
            if (eq.getProvidedCapability() == Capability.SCIENTIFIC_RESEARCH && !eq.isDamaged()) {
                hasWorkingSciEquipment = true;
                break;
            }
        }
        if (!hasWorkingSciEquipment) {
            return "Laboratory lacks working scientific equipment.";
        }
        return null;
    }

    @Override
    public void onMissionStart() {
        for (Agent a : assignedAgents) {
            a.setCurrentLocation(targetFacility.getName());
        }
    }

    @Override
    public void onMissionComplete() {}
}

class RescueMission extends Mission {
    private Agent strandedEntity;

    public RescueMission(String id, String name, int duration, Agent strandedEntity) {
        super(id, name, duration, null);
        this.strandedEntity = strandedEntity;
        requiredCapabilities.add(Capability.SURFACE_EXPLORATION);
        requiredCapabilities.add(Capability.COMMUNICATION);
    }

    @Override
    public String checkEnvironmentRestriction(EnvironmentCondition env) {
        if (env == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
            return "Rescue mission requires active communication and cannot run during communication disruption.";
        }
        if (env == EnvironmentCondition.DUST_STORM) {
            return "Surface rescue operations cannot start during a dust storm.";
        }
        return null;
    }

    @Override
    public String validateTargetFacility() { return null; }

    @Override
    public void onMissionStart() {
        for (Agent a : assignedAgents) {
            a.setCurrentLocation(strandedEntity.getCurrentLocation());
        }
    }

    @Override
    public void onMissionComplete() {
        strandedEntity.setCurrentLocation("Habitat Alpha");
        for (Agent a : assignedAgents) {
            a.setCurrentLocation("Habitat Alpha");
        }
    }
}

// ==========================================
// 7. MARS COLONY & SIMULATION CONTROLLER
// ==========================================

public class MarsColony {
    private String colonyName;
    private int currentTime;
    private EnvironmentCondition environment;
    private List<Agent> agents;          // Aggregation
    private List<Facility> facilities;   // Aggregation
    private List<Mission> missionHistory;
    private List<Emergency> emergencyHistory;
    private Mission activeMission;

    public MarsColony(String colonyName, List<Agent> agents, List<Facility> facilities) {
        this.colonyName = colonyName;
        this.currentTime = 0;
        this.environment = EnvironmentCondition.NORMAL;
        this.agents = new ArrayList<>(agents);
        this.facilities = new ArrayList<>(facilities);
        this.missionHistory = new ArrayList<>();
        this.emergencyHistory = new ArrayList<>();
        this.activeMission = null;
    }

    public List<Agent> getAgents() { return agents; }
    public List<Facility> getFacilities() { return facilities; }
    public Mission getActiveMission() { return activeMission; }
    public void clearActiveMission() { this.activeMission = null; }
    public EnvironmentCondition getEnvironment() { return environment; }

    public void setEnvironment(EnvironmentCondition newCondition) {
        System.out.println("[ENVIRONMENT UPDATE] Condition changed from " + this.environment + " to " + newCondition);
        this.environment = newCondition;
        if (activeMission != null) {
            String restriction = activeMission.checkEnvironmentRestriction(newCondition);
            if (restriction != null) {
                System.out.println("[MISSION ABORT] Active mission failed due to environment: " + restriction);
                activeMission.markFailed(restriction);
                activeMission = null;
            }
        }
    }

    // Rule 5.1.1: Sum across all currently operational facilities only
    public int getTotalResource(ResourceType type) {
        int total = 0;
        for (Facility f : facilities) {
            if (f.isOperational()) {
                total += f.getResourceQuantity(type);
            }
        }
        return total;
    }

    public boolean hasSufficientResources(Map<ResourceType, Integer> required) {
        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            if (getTotalResource(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // Rule 5.1.2 & 5.1.3 & 5.1.6: All-or-nothing consumption across multiple operational facilities
    // Policy: Draw from StorageFacility instances first, then other operational facilities in list order
    public boolean consumeResources(Map<ResourceType, Integer> required) {
        if (!hasSufficientResources(required)) {
            return false;
        }

        List<Facility> orderedFacilities = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.isOperational() && f instanceof StorageFacility) orderedFacilities.add(f);
        }
        for (Facility f : facilities) {
            if (f.isOperational() && !(f instanceof StorageFacility)) orderedFacilities.add(f);
        }

        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            ResourceType type = entry.getKey();
            int remainingToDeduct = entry.getValue();
            for (Facility f : orderedFacilities) {
                if (remainingToDeduct <= 0) break;
                int deducted = f.deductResourceUpTo(type, remainingToDeduct);
                remainingToDeduct -= deducted;
            }
        }
        return true;
    }

    // Section 6.2: Mission Submission and Validation
    public boolean submitMission(Mission mission, List<Agent> requestedAgents) {
        missionHistory.add(mission);
        System.out.println("\n--- Submitting Mission: " + mission.getName() + " ---");

        // 1. Check if another mission is active
        if (activeMission != null) {
            String reason = "Another mission (" + activeMission.getName() + ") is already active.";
            mission.markFailed(reason);
            System.out.println("Mission cannot be assigned. Reason: " + reason);
            return false;
        }

        // 2. Check environmental conditions
        String envError = mission.checkEnvironmentRestriction(environment);
        if (envError != null) {
            mission.markFailed(envError);
            System.out.println(mission.getName() + " cannot start.\nReason: " + envError);
            return false;
        }

        // 3. Check target facility readiness
        String facilityError = mission.validateTargetFacility();
        if (facilityError != null) {
            mission.markFailed(facilityError);
            System.out.println("Mission cannot be assigned. Reason: " + facilityError);
            return false;
        }

        // 4. Check agent availability and capabilities via agent.canPerform(mission)
        if (requestedAgents == null || requestedAgents.isEmpty()) {
            mission.markFailed("No agents provided for mission.");
            System.out.println("Mission cannot be assigned. Reason: No agents provided.");
            return false;
        }

        Set<Capability> coveredCapabilities = EnumSet.noneOf(Capability.class);
        for (Agent agent : requestedAgents) {
            if (!agent.isAvailable()) {
                String reason = "Agent " + agent.getName() + " is currently unavailable.";
                mission.markFailed(reason);
                System.out.println("Mission cannot be assigned. Reason: " + reason);
                return false;
            }
            if (!agent.canPerform(mission)) {
                String reason = "Agent " + agent.getName() + " does not possess the required capability.";
                mission.markFailed(reason);
                System.out.println("Mission cannot be assigned.\nReason: " + reason);
                return false;
            }
            for (Capability cap : mission.getRequiredCapabilities()) {
                if (agent.hasCapability(cap)) {
                    coveredCapabilities.add(cap);
                }
            }
        }

        if (!coveredCapabilities.containsAll(mission.getRequiredCapabilities())) {
            String reason = "Assigned team does not collectively cover all required mission capabilities: "
                    + mission.getRequiredCapabilities();
            mission.markFailed(reason);
            System.out.println("Mission cannot be assigned. Reason: " + reason);
            return false;
        }

        // 5. Check colony-wide resources (All-or-Nothing)
        if (!hasSufficientResources(mission.getRequiredResources())) {
            String reason = "Insufficient colony-wide resources for mission requirements: " + mission.getRequiredResources();
            mission.markFailed(reason);
            System.out.println("Mission cannot be assigned.\nReason: " + reason);
            return false;
        }

        // 6. Accept Mission: consume resources, assign agents, set active
        consumeResources(mission.getRequiredResources());
        mission.markAssigned(requestedAgents);
        this.activeMission = mission;
        System.out.println("Mission ACCEPTED and set to ASSIGNED: " + mission.getName());
        return true;
    }

    public void triggerEmergency(Emergency emergency) {
        emergencyHistory.add(emergency);
        emergency.handle(this);
    }

    public void advanceTime(int units) {
        for (int i = 0; i < units; i++) {
            currentTime++;
            System.out.println("\n[TIME ADVANCE] Colony Time = " + currentTime + " | Environment = " + environment);

            // Progress active mission if present
            if (activeMission != null) {
                activeMission.advanceOneStep(environment);
                System.out.println("  Active Mission Update -> " + activeMission);
                if (activeMission.getState() == MissionState.COMPLETED
                        || activeMission.getState() == MissionState.FAILED) {
                    activeMission = null;
                }
            }

            // Update idle agents (rest/recharge)
            for (Agent a : agents) {
                boolean onActiveMission = (activeMission != null && activeMission.getAssignedAgents().contains(a));
                if (!onActiveMission) {
                    a.onTimeStep(false);
                }
            }
        }
    }
}