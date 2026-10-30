# MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator

## Alexandria University — Faculty of Engineering
**Computer and Communication Engineering | Specialized Scientific Programs**  
**Course:** Programming II (Computer and Communications) — Fall 2026  
**Lab Assignment 5**

---

## 1. System Overview & Architecture

MARS-OPS is a comprehensive, production-grade object-oriented Java simulation system modeling humanity's first self-sustaining research outpost on the Martian surface. The colony operates in an unforgiving extraterrestrial environment under strict resource and environmental constraints. It coordinates human personnel, autonomous robotic units, vital habitats and industrial facilities, complex scientific and repair missions, Martian weather dynamics, and critical emergencies.

The system is designed around the four fundamental pillars of Object-Oriented Programming:
1. **Encapsulation:** Internal agent fatigue, battery levels, equipment health, facility life-support cycles, and operational statuses are strictly encapsulated via private/protected fields with validated mutators and domain methods.
2. **Abstraction:** High-level behaviors are modeled through abstract base classes (`Agent`, `CrewMember`, `RoboticUnit`, `Vehicle`, `Facility`, `Mission`, `Emergency`). Client code interacts with polymorphic abstractions rather than concrete classes.
3. **Inheritance:** Rich "is-a" class hierarchies model distinct capabilities without code duplication (e.g., `ExplorationRover` extends `Vehicle` extends `RoboticUnit` extends `Agent`).
4. **Polymorphism:** The colony queries entity capabilities dynamically (`agent.canPerform(mission)`, `agent.canHandle(capability)`) and executes specialized behavior polymorphically across various mission types and emergencies.

### Complete Package & File Structure
```
MarsOpsApp/
├── src/
│   ├── Agent/                         # Autonomous agents and human personnel
│   │   ├── Agent.java                 # Abstract root entity (health, availability, location, capabilities)
│   │   ├── CrewMember.java            # Abstract human personnel (fatigue tracking, eat, drink, rest)
│   │   ├── RoboticUnit.java           # Abstract robotic unit (battery level tracking, charge, discharge)
│   │   ├── Vehicle.java               # Abstract motorized unit (carries equipment)
│   │   ├── Scientist.java             # Scientific research & exploration
│   │   ├── Engineer.java              # Equipment/facility repair & fire response
│   │   ├── MedicalOfficer.java        # Medical triage & patient recovery
│   │   ├── RepairRobot.java           # Autonomous hardware repair
│   │   ├── ExplorationRover.java      # Surface traverse & cargo transport
│   │   └── Drone.java                 # Aerial reconnaissance & remote relay
│   ├── Facility/                      # Colony infrastructure and life support
│   │   ├── Facility.java              # Abstract facility (occupancy, operational status, resources)
│   │   ├── Habitat.java               # Living quarters with integrated LifeSupportSystem
│   │   ├── LifeSupportSystem.java     # Oxygen filtration (composition with Habitat)
│   │   ├── Laboratory.java            # Scientific research lab (fire hazard handling)
│   │   ├── PowerStation.java          # Energy generator & battery recharging hub
│   │   ├── StorageFacilities.java     # Central resource stockpile & warehouse
│   │   └── CommunicationStation.java  # Deep-space antenna & drone terminal
│   ├── Mission/                       # Mission lifecycle management
│   │   ├── Mission.java               # Abstract mission (duration, assignment, states)
│   │   ├── ExplorationMission.java    # Surface analysis with Scientist and Rover
│   │   ├── RepairMission.java         # Facility/equipment restoration (Engineer + Robot)
│   │   ├── ResearchMission.java       # Laboratory experiments (Scientist + Lab)
│   │   └── RescueMission.java         # Stranded entity retrieval (Vehicle + Personnel)
│   ├── Emergency/                     # Colony-wide critical events
│   │   ├── Emergency.java             # Abstract emergency base
│   │   ├── OxygenLeak.java            # Habitat breach with double-loss penalty
│   │   ├── Fire.java                  # Laboratory fire outbreak
│   │   ├── RoverBreakdown.java        # Surface vehicle breakdown & repair
│   │   └── MedicalEmergency.java      # Crew trauma & medical treatment
│   ├── Environment/                   # Martian weather and atmospheric conditions
│   │   └── Environment.java           # NORMAL, DUST_STORM, COMMUNICATION_DISRUPTION
│   ├── Helper/                        # Enums, value objects, and equipment
│   │   ├── Capability.java            # Capability enum (8 capabilities)
│   │   ├── ResourceType.java          # Colony resource types enum (7 resources)
│   │   ├── MissionState.java          # ASSIGNED, IN_PROGRESS, COMPLETED, FAILED
│   │   ├── EmergencyState.java        # TRIGGERED, RESOLVED, UNRESOLVED
│   │   ├── EnvironmentCondition.java  # NORMAL, DUST_STORM, COMMUNICATION_DISRUPTION
│   │   └── Equipment.java             # Tools and specialized equipment
│   └── Main.java                      # End-to-end execution of all 5 required demonstration scenarios
├── uml/                               # Complete UML Diagrams (Draw.io & vector SVG)
│   ├── MarsOps_AllDiagrams.drawio     # Master Draw.io file containing all 7 diagram tabs
│   ├── ClassDiagram.drawio & .svg     # Complete 35-class/enum static structure diagram
│   ├── ObjectDiagram.drawio & .svg    # System snapshot at T=2 with runtime values & links
│   ├── StateMachineDiagram.drawio & .svg # Mission lifecycle state machine
│   ├── SequenceDiagrams.drawio        # Multi-tab sequence diagrams (4A, 4B, 4C)
│   ├── SequenceDiagram_Mission.svg    # 4A: Mission submission & execution
│   ├── SequenceDiagram_OxygenLeak.svg # 4B: Oxygen leak emergency handling
│   ├── SequenceDiagram_Environment.svg# 4C: Dust storm environmental restriction
│   └── UseCaseDiagram.drawio & .svg   # Actors, boundary, use cases, include/extend
└── README.md                          # Comprehensive project documentation
```

---

## 2. Important Design Decisions & Assumptions

### A. Simulation Time Management Strictly in Colony
- **Design Rule:** In accordance with the prompt ("advance time put it in colony and just colony, don't overcomplicate stuff"), simulation time progression (`colony.advanceTime(int timeUnits)`) is located **strictly and exclusively** inside the `Colony` class.
- **Rationale:** Missions, agents, and facilities do not maintain independent clocks. The colony coordinates all global state updates:
  - Advancing the active mission's elapsed time.
  - Applying battery discharge to deployed robotic units and vehicles.
  - Applying fatigue accumulation to deployed human crew members.
  - Performing facility upkeep and resource consumption.
  - Checking environmental state changes and evaluating active mission feasibility.
  - Transitioning missions to `COMPLETED` or `FAILED` and releasing agents upon mission end.
- **Mission Progress Delegation:** `Mission` exposes `progress(int timeUnits)` and query method `isCompleted()`. This allows `Colony` to advance the mission tick-by-tick without having missions independently manipulate simulation time.

### B. Polymorphic Capability Checking (Sections 6.1 & 9.1)
- **Design Rule:** Concrete type checks (`instanceof`) are avoided during mission assignment and emergency triage.
- **Rationale:** The system queries entities dynamically:
  ```java
  if (agent.canPerform(mission)) {
      mission.assign(agent);
  }
  ```
  and for emergency triage:
  ```java
  if (agent.canHandle(Capability.EQUIPMENT_REPAIR) && agent.isAvailable()) {
      // dispatch responder
  }
  ```
  This enables multi-role capabilities:
  - Both `Engineer` and `RepairRobot` possess `EQUIPMENT_REPAIR` and `FIRE_RESPONSE`.
  - Both `Scientist` and `ExplorationRover` possess `SURFACE_EXPLORATION`.
  - A `Vehicle` carrying specialized equipment can perform tasks requiring that equipment's capability.

---

## 3. Object Relationships & Design Justification (Section 11.1 & 11.2)

Per Page 24 of the lab specification, the system implements and justifies all four fundamental UML relationship types:

### 1. Association
- **Why Chosen:** Used when two classes collaborate or interact without whole-part ownership, where both objects can exist independently.
- **MARS-OPS Examples:**
  - `Mission` interacts with `Agent`: `Mission` holds a reference `List<Agent> assignedAgents`. An engineer or scientist participates in a mission, but the agent exists before and after the mission, and the mission exists as a plan independently of the specific agents assigned.
  - `Colony` references `activeMission`: `Colony` holds a reference to a single `Mission activeMission`.
  - `Vehicle` references `Equipment`: A vehicle mounts an `Equipment` instance (`carriedEquipment`) during an operation and releases it afterward.
- **UML Representation:** A solid line connecting the classes (with open navigation arrow `->` or bidirectional link).
- **Java Representation:** A field reference without ownership or cascading lifecycle:
  ```java
  public abstract class Mission {
      protected List<Agent> assignedAgents;
      protected Equipment equipment;
      // ...
  }
  ```

### 2. Aggregation (Hollow Diamond)
- **Why Chosen:** Represents a "whole-part" relationship where the parts can exist independently of the whole. If the whole is destroyed or shut down, the parts survive.
- **MARS-OPS Examples:**
  - `Colony` aggregates `Agent`: The colony manages human personnel and robotic units. If the colony were decommissioned or evacuated, crew members and robots remain valid entities that can be reassigned or relocated.
  - `Colony` aggregates `Facility`: Buildings exist as modular units within the colony.
  - `Colony` tracks `Emergency`: The colony records `emergencyHistory`.
- **UML Representation:** A solid line with a hollow diamond (`◇`) at the whole/owner end (`Colony`).
- **Java Representation:** Collections populated via constructors or adder/remover methods without tied destruction:
  ```java
  public class Colony {
      private List<Agent> agents;
      private List<Facility> facilities;
      
      public Colony(List<Agent> agents, List<Facility> facilities) {
          this.agents = agents != null ? agents : new ArrayList<>();
          this.facilities = facilities != null ? facilities : new ArrayList<>();
      }
      public void addAgent(Agent agent) { this.agents.add(agent); }
      public boolean removeAgent(Agent agent) { return this.agents.remove(agent); }
  }
  ```

### 3. Composition (Solid Diamond)
- **Why Chosen:** Represents a strong whole-part ownership where the part's lifecycle is strictly tied to the whole. The part cannot exist without the whole; if the whole is destroyed, the part is destroyed with it.
- **MARS-OPS Example:**
  - `Habitat` strictly owns `LifeSupportSystem`: Each habitat instantiates and permanently owns its internal life-support system. If the habitat is breached or destroyed, its internal air filtration and oxygen recycling system is destroyed with it. No other habitat shares this life support system.
- **UML Representation:** A solid line with a filled diamond (`◆`) at the owner end (`Habitat`).
- **Java Representation:** The part is instantiated directly by the whole and held via a `final` field, with no external reassignment:
  ```java
  public class Habitat extends Facility {
      private final LifeSupportSystem lifeSupport;
      
      public Habitat() {
          super("Habitat", 4, 300);
          this.lifeSupport = new LifeSupportSystem(); // Strongly owned
      }
  }
  ```

### 4. Generalization / Inheritance (Hollow Triangle)
- **Why Chosen:** Represents an "is-a" taxonomic hierarchy where subclasses inherit common attributes and methods while providing specialized polymorphic implementations.
- **MARS-OPS Examples:**
  - `Scientist`, `Engineer`, `MedicalOfficer` extend `CrewMember` which extends `Agent`.
  - `RepairRobot`, `Vehicle` extend `RoboticUnit` which extends `Agent`.
  - `ExplorationRover`, `Drone` extend `Vehicle`.
  - `Habitat`, `Laboratory`, `PowerStation`, `StorageFacilities`, `CommunicationStation` extend `Facility`.
  - `ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission` extend `Mission`.
  - `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency` extend `Emergency`.
- **UML Representation:** A solid line with a hollow triangle (`▷`) pointing to the superclass.
- **Java Representation:** The `extends` keyword with `@Override` method polymorphism:
  ```java
  public abstract class Agent {
      public abstract boolean isHuman();
      public abstract boolean isRobot();
  }
  public abstract class CrewMember extends Agent {
      @Override public boolean isHuman() { return true; }
      @Override public boolean isRobot() { return false; }
  }
  public class Scientist extends CrewMember {
      @Override
      public boolean canPerform(Mission mission) {
          return mission instanceof ExplorationMission || mission instanceof ResearchMission;
      }
  }
  ```

---

## 4. Resource Allocation Rules (Section 5 & 5.1)

1. **Colony-Wide Totals (Operational Facilities Only):**
   - The colony's total quantity of any resource is the sum of quantities held across all currently *operational* facilities (`facility.isOperational() == true`).
   - Resources stored inside an offline or destroyed facility (e.g., an offline laboratory due to fire) are strictly inaccessible and **do not count** toward colony-wide available totals.
2. **Multiple Sources:**
   - A mission or emergency operation may draw required resources from more than one facility.
3. **All-or-Nothing Consumption:**
   - Before deducting any resources, the colony checks if the total across all operational facilities satisfies the full requested amount for every resource type.
   - If even one required resource is insufficient, **zero resources are consumed**, no negative numbers are permitted, and the operation fails immediately with an explicit error reason.
4. **Resource Allocation Policy (Documented Assumption):**
   - When drawing resources, `Colony.deductResource(ResourceType, amount)` prioritizes drawing from dedicated `StorageFacilities` first, preserving critical local facility stocks (such as habitat life support oxygen and food).
   - If storage facilities are exhausted, remaining quantities are drawn in order from other operational facilities.
5. **Local Emergency Impacts:**
   - Resource losses from local emergencies (e.g., habitat oxygen leaks) are deducted directly from the affected facility, which automatically and accurately reduces the colony-wide total.

---

## 5. Emergency Rules & Consequences (Section 9)

Emergencies represent urgent, unscripted incidents. Per Section 6.2, emergency handling is **not subject to the one-active-mission limit**, provided the responding agents are available and not deployed on the active mission.

### Complete Emergency Requirements Table
| Emergency Type | Affected Location | Required Responders | Required Resources | Resolved Consequences | Unresolved Consequences |
|---|---|---|---|---|---|
| **Oxygen Leak** | `Habitat` | 1 Human (`Engineer`) + 1 Robot (`RepairRobot`) with `EQUIPMENT_REPAIR` | 2 Spare Parts, 5 Battery Power | Habitat online; total oxygen lost = initial deduction (e.g. 40 units). | State: `UNRESOLVED`; 0 response resources consumed; **initial oxygen deducted again (double loss = 80 units)!** |
| **Fire** | `Laboratory` | 1 Agent with `FIRE_RESPONSE` | 10 Water | Fire extinguished; laboratory restored to online status. | Laboratory remains offline (`hasFire = true`); equipment damaged. |
| **Rover Breakdown** | Martian Surface | 1 Agent with `EQUIPMENT_REPAIR` | 3 Spare Parts, 10 Battery Power | Rover health restored to 100%; rover available. | Rover damaged in the field; if on mission, mission fails. |
| **Medical Emergency** | `Habitat` | 1 Agent with `MEDICAL_CARE` (`MedicalOfficer`) | 5 Medical Supplies | Patient health restored (+35%); medical supplies consumed. | Patient remains untreated; emergency unresolved. |

### Oxygen Leak Double-Loss Rule
- Upon detection, the specified leak quantity (e.g., 40 units) is immediately deducted from the affected habitat.
- If repaired, this is the total loss.
- If unresolved (responders unavailable or insufficient spare parts/battery), the same amount is deducted again. Therefore, an unresolved leak costs **twice as much oxygen** as a repaired leak.
- Oxygen levels are mathematically clamped so they **never fall below zero**.

---

## 6. Environmental Dynamics (Section 8)

The colony tracks atmospheric and space weather through the `Environment` class with three conditions:
1. `NORMAL`: Standard operating environment.
2. `DUST_STORM`: Violent Martian dust storms blocking surface visibility and solar generation.
3. `COMMUNICATION_DISRUPTION`: Atmospheric electromagnetic interference severing wireless links.

### Effect on Missions: Starting vs. In-Progress (Documented Requirement)
- **Missions Attempting to Start:**
  - `ExplorationMission` cannot start during a `DUST_STORM`. Submission fails immediately:  
    `Exploration Mission cannot start. Reason: Surface operations are not allowed during a dust storm.`
  - `RescueMission` (dependent on remote drones/comms) cannot start during `COMMUNICATION_DISRUPTION`. Submission fails immediately.
- **Missions Already In Progress (During Simulation Time Advance):**
  - If a `DUST_STORM` develops while an exploration rover is in the field, the active mission **fails immediately**, status transitions to `FAILED`, the active mission is cleared from the colony, and assigned agents are recovered/freed.
  - If a `COMMUNICATION_DISRUPTION` occurs during an active rescue mission, the remote vehicle loses connection and the mission **fails immediately**.

---

## 7. Mission Lifecycle & State Machine (Section 7 & 11.4)

Each mission transitions through a deterministic four-state lifecycle:

```
           [submitMission() && Requirements Valid]
   [*] ───────────────────────────────────────────────────► [ASSIGNED]
    │                                                           │
    │ [submitMission() fails / Rejected]                        │ startMission()
    ▼                                                           ▼
[FAILED] ◄─────────────────────────────────────────────── [IN_PROGRESS]
    ▲       [Runtime Disruption: Dust storm / Depletion]        │
    │                                                           │ advanceTime(t)
    │                                                           │ [timeElapsed >= duration]
    │                                                           ▼
    └───────────────────────────────────────────────────── [COMPLETED]
```

### State Definitions & Rules
1. **`ASSIGNED`:** Mission passed all checks (no other mission active, agents available and capable, colony resources sufficient, environment compatible). Colony sets this mission as `activeMission`.
2. **`IN_PROGRESS`:** Execution begins (`startMission()`). Assigned agents have `onMission = true`, equipment marked unavailable, resources deducted. Duration starts counting.
3. **`COMPLETED`:** Required time units reached (`timeElapsed >= requiredDuration`). Agents are released (`onMission = false`), equipment released, `activeMission` cleared.
4. **`FAILED`:** Terminal state.
   - **Immediate Rejection:** If submitted while another mission is active, or if an agent lacks capability, or if resources are insufficient, or if weather forbids it, the mission transitions immediately to `FAILED` and **never becomes active**.
   - **Non-Retriable:** Per Section 6.2 #6, failed missions cannot be retried; a new mission instance must be created.

---

## 8. Implementation Decisions Left Open by the Specification

1. **Colony Multi-Resource Consumption:**
   - In our implementation, standard missions specify an array of required `ResourceType`. Default resource quotas (5 units per resource type) are verified and deducted upon mission start.
2. **Agent Energy and Fatigue Kinetics:**
   - When simulation time advances while an active mission is `IN_PROGRESS`, deployed human crew members incur +5 fatigue per time unit, while robotic units discharge 5 battery units per time unit.
   - Non-deployed crew members can eat food and drink water inside operational habitats to restore fatigue and health.
3. **Equipment Durability:**
   - Equipment tracks operational wear (`usageNumber`). After 7 uses without maintenance, equipment becomes damaged and unavailable until repaired by an `Engineer` or `RepairRobot`.

---

## 9. Instructions for Compiling and Running

The project requires **Java JDK 17 or higher** (tested and verified with OpenJDK 25).

### Cross-Platform Compilation Commands

#### Option A: PowerShell (Windows)
From the project root folder (`MarsOpsApp/`):
```powershell
# Create bin directory
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }

# Compile all source files across all packages
javac -d bin (Get-ChildItem -Recurse src/*.java | ForEach-Object { $_.FullName })
```

#### Option B: Windows Command Prompt (cmd.exe)
```cmd
mkdir bin
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt
```

#### Option C: Linux / macOS (Bash)
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

### Execution
Run the compiled `Main` class:
```powershell
java -cp bin Main
```

---

## 10. Verification of Required Demonstration Scenarios

`Main.java` executes and confirms all 5 required lab scenarios:

1. **Scenario 1: Successful Exploration**
   - Creates a `Scientist` (Dr. Mark Watney), an `ExplorationRover` (Curiosity-2), a `Mass Spectrometer` equipment, and an `ExplorationMission`.
   - Displays full colony status report before execution.
   - Submits and starts the mission (becomes `ASSIGNED` -> `IN_PROGRESS`).
   - Advances colony time by 7 units.
   - Rover battery discharges proportionally, scientist fatigue increases.
   - Mission transitions to `COMPLETED`, agents are released, active mission cleared.
   - Displays full colony status report after execution.

2. **Scenario 2: Mission Rejection (Unsuitable Agent)**
   - Submits an exploration mission with an `Engineer` (lacking `SCIENTIFIC_RESEARCH` capability).
   - System queries `agent.canPerform(mission)`, which returns `false`.
   - Mission fails immediately with output:
     ```
     Mission cannot be assigned.
     Reason: Agent does not possess the required capability.
     ```
   - Confirms mission status is `FAILED` and active mission in colony remains `NONE`.

3. **Scenario 3: Resource Shortage**
   - Simulates depleted battery power (0 units) across all operational facilities.
   - Submits a mission requiring battery power.
   - Rejection occurs without negative numbers or partial deduction. Output:
     ```
     Operation Rejected!
     Reason: There aren't available sufficient resources in the colony.
     ```
   - Confirms colony resources remain untouched (all-or-nothing consumption).

4. **Scenario 4: Emergency Handling (Oxygen Leak)**
   - Simulates a 40-unit oxygen leak in `Habitat1`.
   - **Step 1:** Emergency detected.
   - **Step 2:** Affected habitat located. Initial 40 units deducted.
   - **Step 3:** Identifies suitable available responders (`Engineer` and `RepairRobot`).
   - **Step 4:** Dispatches responders to habitat.
   - **Step 5:** Consumes 2 spare parts and 5 battery power.
   - **Step 6:** Habitat state restored to online, leak resolved.
   - **Alternative Flow Demonstration:** Triggers leak in an unstaffed habitat. Responders unavailable -> marked `UNRESOLVED`, 0 response resources consumed, **initial oxygen deducted again (double loss = 80 units)!**

5. **Scenario 5: Environmental Effect (Dust Storm)**
   - Sets environment to `DUST_STORM`.
   - Submits surface exploration mission.
   - System blocks mission from starting:
     ```
     Exploration Mission cannot start.
     Reason: Surface operations are not allowed during a dust storm.
     ```
   - Mission status is `FAILED`; never becomes active.

---

## 11. UML Diagrams Reference

All diagrams are located in the `uml/` directory in both editable `.drawio` format and standalone vector `.svg` format:

| Diagram Type | Draw.io File | Vector SVG File | Description |
|---|---|---|---|
| **Master All-in-One** | `MarsOps_AllDiagrams.drawio` | N/A | Contains all 7 diagram tabs in one unified Draw.io file. |
| **Class Diagram** | `ClassDiagram.drawio` | `ClassDiagram.svg` | All 35 classes/enums, attributes, methods, visibilities, and connectors. |
| **Object Diagram** | `ObjectDiagram.drawio` | `ObjectDiagram.svg` | Snapshot at T=2 with runtime values and relationship links. |
| **State Machine** | `StateMachineDiagram.drawio` | `StateMachineDiagram.svg` | Mission states (ASSIGNED, IN_PROGRESS, COMPLETED, FAILED), rejection, and failure transitions. |
| **Sequence Diagram 4A**| `SequenceDiagrams.drawio` (Tab 1)| `SequenceDiagram_Mission.svg` | Mission submission & execution with rejection alt flow. |
| **Sequence Diagram 4B**| `SequenceDiagrams.drawio` (Tab 2)| `SequenceDiagram_OxygenLeak.svg`| Oxygen leak emergency handling with double-loss alt flow. |
| **Sequence Diagram 4C**| `SequenceDiagrams.drawio` (Tab 3)| `SequenceDiagram_Environment.svg`| Dust storm environmental restriction on exploration. |
| **Use Case Diagram** | `UseCaseDiagram.drawio` | `UseCaseDiagram.svg` | System boundary, 3 actors, 9 use cases, `<<include>>`, and `<<extend>>`. |

> **How to View & Edit Draw.io Files:**  
> Open [app.diagrams.net](https://app.diagrams.net) or use the VS Code Draw.io extension and open `MarsOps_AllDiagrams.drawio` to access all diagrams in separate tabs.
