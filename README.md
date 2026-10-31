# MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator

**Course:** Computer and Communications Programming II (Fall 2026) — Lab 5  
**Institution:** Alexandria University, Faculty of Engineering (SSP)

---

## 1. System Overview & Main Components

**MARS-OPS** is an Object-Oriented Java simulation system designed to manage a research colony on Mars. Rather than storing passive data, the system models autonomous collaboration between human personnel, robotic units, colony facilities, finite life-support resources, scheduled missions, dynamic Martian weather conditions, and emergency responses.

The codebase is modularized into **six packages** plus the main simulation driver:

1. **`Colony` (`Colony`)**: The central orchestrator that tracks registered agents, facilities, colony-wide resource totals, the single active mission, simulation time (`currentTime`), environmental state, and historical logs (`missionHistory`, `emergencyHistory`).
2. **`Agent` (`Agent`, `CrewMember`, `RoboticUnit`, `Vehicle`, `Scientist`, `Engineer`, `MedicalOfficer`, `ExplorationRover`, `RepairRobot`, `Drone`)**: Models all human personnel and autonomous robotic units, their health, location, availability, fatigue/battery states, and capability sets.
3. **`Facility` (`Facility`, `Habitat`, `LifeSupportSystem`, `Laboratory`, `PowerStation`, `StorageFacilities`, `CommunicationStation`)**: Models physical colony structures, local resource storage maps, equipment inventories, occupancy capacities, and per-tick resource consumption/generation.
4. **`Mission` (`Mission`, `ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission`)**: Encapsulates mission lifecycles, pre-submission validation rules, duration tracking, and agent/equipment assignment.
5. **`Emergency` (`Emergency`, `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency`)**: Encapsulates crisis events, polymorphic responder dispatch, resource consumption, and failure penalties.
6. **`Environment` (`Environment`) & `Helper` (`Equipment`, `Capability`, `ResourceType`, `MissionState`, `EmergencyState`, `EnvironmentCondition`)**: Provides Martian weather state transitions, equipment durability tracking, and domain enumerations.

---

## 2. Important Design Decisions & OOP Relationships

### 2.1 Capability-Based Polymorphism (No Concrete Type Checking)
In compliance with **Sections 6.1 and 9.1**, missions and emergencies never inspect concrete agent classes (`instanceof Scientist` or `instanceof Engineer`). Instead:
* Every `Agent` maintains a `Set<Capability> capabilities`.
* Missions query `agent.canPerform(mission)`, which verifies `isAvailable()` and checks `hasCapability(mission.getRequiredCapability())`.
* Emergencies query `agent.canHandle(Capability)` alongside polymorphic `agent.isHuman()` and `agent.isRobot()` methods.

### 2.2 The Four UML Object Relationships (Section 11.1)
1. **Inheritance (`is-a`)**:
   * `CrewMember` and `RoboticUnit` extend the abstract `Agent` class, overriding `updateAvailability()`, `isHuman()`, and `isRobot()`.
   * `Scientist`, `Engineer`, and `MedicalOfficer` extend `CrewMember`.
   * `RepairRobot` and `Vehicle` extend `RoboticUnit`, while `ExplorationRover` and `Drone` extend `Vehicle`.
   * `Habitat`, `Laboratory`, `PowerStation`, `StorageFacilities`, and `CommunicationStation` extend `Facility`.
   * Concrete missions extend `Mission`; concrete emergencies extend `Emergency`.
2. **Composition (`strong lifecycle ownership`)**:
   * `Habitat` owns a `private final LifeSupportSystem lifeSupport` instantiated inside the `Habitat()` constructor. If a `Habitat` instance is destroyed, its internal `LifeSupportSystem` ceases to exist with it.
   * `Colony` instantiates and owns its internal `Environment` instance.
3. **Aggregation (`whole-part, independent lifecycle`)**:
   * `Colony` aggregates `List<Agent>` and `List<Facility>`. Agents and facilities are created externally and passed to the colony; they can exist independently of the `Colony` instance.
   * `ExplorationRover` aggregates `List<Equipment> carriedEquipment`, and `Facility` aggregates `List<Equipment> equipments`. Equipment can be loaded, unloaded, and transferred independently.
4. **Association (`uses / collaborates`)**:
   * `Mission` associates with assigned `Agent` instances, target `Facility` instances, and `Equipment`.
   * `Emergency` subclasses associate with the affected `Facility`, `CrewMember`, or `ExplorationRover`.

---

## 3. Resource Management & Allocation Policy (Section 5.1)

Resources (`OXYGEN`, `WATER`, `FOOD`, `FUEL`, `BATTERY_POWER`, `MEDICAL_SUPPLIES`, `SPARE_PARTS`) are stored locally inside individual `Facility` instances (`Map<ResourceType, Integer> resources`) and managed globally by `Colony`:

1. **Colony-Wide Totals**: `Colony.getTotalResource(ResourceType)` sums the resource across all **currently operational** (`isOperational() == true`) facilities. Offline facilities (`isOperational() == false`) are excluded from available totals.
2. **Multi-Source & All-or-Nothing Consumption**: Before deducting any resource, `Colony.deductResource(ResourceType, int)` verifies that `getTotalResource(type) >= amount`. If the total across all operational facilities is insufficient, **zero units are deducted** and the operation returns `false`.
3. **Chosen Resource Allocation Policy**:
   * **Priority 1 — `StorageFacilities` First**: When `Colony.deductResource(type, amount)` executes, the colony first iterates through all operational `StorageFacilities` (which serve as the colony's primary central warehouse with a capacity of $600$ units per resource) and deducts as much of the required amount as possible.
   * **Priority 2 — Other Operational Facilities**: If a remaining balance still needs to be satisfied after depleting `StorageFacilities`, the colony iterates through the remaining operational facilities in registration order (`Habitat`, `Laboratory`, `PowerStation`, `CommunicationStation`) until the exact amount is fulfilled.

---

## 4. Mission States & Lifecycle Rules (Sections 6 & 7)

All missions follow the `MissionState` enumeration lifecycle:
* **`ASSIGNED`**: Set when a mission passes all validation checks in `submitMission()` (no other active mission, environment allows the mission, assigned agents are available and possess the required capability, required equipment is intact and available, and colony-wide resources meet the threshold).
* **`IN_PROGRESS`**: Set when `startMission()` is invoked. Assigned agents are marked `onMission = true` (making them unavailable for other tasks or emergencies), equipment availability is locked (`available = false`), and `timeElapsed` is initialized to $0$.
* **`COMPLETED`**: Reached when `Colony.advanceTime()` increments `timeElapsed` to `requiredDuration` ($7$ ticks for `ExplorationMission`, $5$ for `RepairMission`, $10$ for `ResearchMission`, $8$ for `RescueMission`). All assigned agents and equipment are released (`onMission = false`, `available = true`), any repair targets are restored to operational status, and the colony clears `activeMission` into `missionHistory`.
* **`FAILED`**:
  * **Immediate Rejection**: If submission validation fails (e.g., another mission is already active, an agent lacks capability, resources are insufficient, or a dust storm blocks surface exploration), the mission immediately transitions to `FAILED` with a descriptive `failureReason` and **never becomes the active mission**.
  * **Mid-Execution Failure**: If an environmental hazard or `RoverBreakdown` occurs while `IN_PROGRESS`, the active mission immediately transitions to `FAILED`, releases its agents/equipment, records the `failureReason`, and is moved to `missionHistory`. Failed missions cannot be retried.

---

## 5. Environmental Effects (Section 8)

The Martian environment (`Environment`) can be in one of three states (`EnvironmentCondition`):
1. **`NORMAL` ($85\%$ daily probability)**: All colony facilities and surface/communication missions operate normally.
2. **`DUST_STORM` ($7\%$ daily probability)**:
   * **Pre-Mission Effect**: `ExplorationMission.submitMission()` immediately rejects new surface exploration missions with the reason `"Surface operations are not allowed during a dust storm."`
   * **In-Progress Mission Effect**: During `Colony.advanceTime()`, if the environment changes to `DUST_STORM` while an `ExplorationMission` is `IN_PROGRESS`, the active mission **immediately fails** (`activeMission.fail("Active rover exploration mission failed due to Martian dust storm.")`), unassigns its crew and rover, and clears `activeMission`.
3. **`COMMUNICATION_DISRUPTION` ($8\%$ daily probability)**:
   * **Facility Effect**: `CommunicationStation.changeOperationalStatus()` sets the communication station offline.
   * **Pre-Mission Effect**: `RescueMission.submitMission()` rejects new rescue missions.
   * **In-Progress Mission Effect**: During `Colony.advanceTime()`, if `COMMUNICATION_DISRUPTION` occurs while a `RescueMission` is `IN_PROGRESS`, the active rescue mission **immediately fails** (`activeMission.fail("Active rescue mission failed due to severe communication disruption.")`) and releases its assigned agents.

---

## 6. Emergency Handling Rules (Section 9)

Emergencies do not interrupt the active mission unless the emergency directly damages an assigned rover (`RoverBreakdown`), and responders must be available (`!isOnMission()` and healthy/charged).

| Emergency Type | Target | Responder Requirements | Resource & Equipment Requirements | Consequences & Resolution Rules |
| :--- | :--- | :--- | :--- | :--- |
| **`OxygenLeak`** | `Habitat` | $1$ Human (`isHuman()`) **and** $1$ Robot (`isRobot()`) with `EQUIPMENT_REPAIR` | $2$ `SPARE_PARTS`, $5$ `BATTERY_POWER` | Immediately deducts $50$ `OXYGEN` from affected habitat. If resolved, habitat stays `ONLINE`. If unresolved (missing responders or resources), no repair resources are consumed and an **additional $50$ `OXYGEN` is deducted ($100$ total loss)**. |
| **`Fire`** | `Laboratory` | $1$ available Agent with `FIRE_RESPONSE` | $10$ `WATER` + functional `Chemical Fire Suppressor` in Lab | Sets lab `OFFLINE` (`hasFire = true`), deducts $30$ `OXYGEN`, damages lab research equipment, and inflicts $20$ health damage to human agents inside the lab. If resolved, consumes $10$ `WATER` and restores lab to `ONLINE`. |
| **`RoverBreakdown`** | `ExplorationRover` (on active mission) | $1$ available Agent (other than the damaged rover) with `EQUIPMENT_REPAIR` | $3$ `SPARE_PARTS`, $10$ `BATTERY_POWER` | Inflicts $50$ damage to the rover and **immediately fails the active mission**. If a responder and resources are available, dispatches responder to breakdown location and restores $50$ rover health. |
| **`MedicalEmergency`** | `CrewMember` & `Habitat` | $1$ available Agent (other than patient) with `MEDICAL_CARE` | $5$ `MEDICAL_SUPPLIES`, operational `Habitat` | Moves doctor and patient to the medical habitat, consumes $5$ `MEDICAL_SUPPLIES`, and restores $35$ health points to the patient. |

---

## 7. Other Implementation Decisions & Assumptions

* **Equipment Durability**: Each `Equipment` item can be used up to $7$ times (`usageNumber >= 7`) before becoming `damaged = true` and `available = false` until repaired via `fixIt()` or a `RepairMission`.
* **Facility Upkeep & Power Generation**: Every simulation tick (`advanceTime(1)`), each operational facility consumes $10$ units of `BATTERY_POWER` (`POWER_COST = 10`). If a facility lacks power, it transitions to `OFFLINE`. Operational `Habitat` instances filter and add $2$ units of `OXYGEN` per tick via their `LifeSupportSystem`, while operational `PowerStation` instances consume $5$ `FUEL` to generate $100$ `BATTERY_POWER`.
* **Agent Recovery**: During each time unit in `advanceTime()`, idle crew members inside operational habitats rest and consume food/water to recover fatigue and health, while robotic units at operational power stations recharge their batteries.

---

## 8. Instructions for Compiling and Running

### Prerequisites
* **Java Development Kit (JDK):** Version 8 or higher (JDK 17 or 21 recommended).

### Directory Structure
Ensure the source files are organized in their respective package folders inside your project root (`StudentID-Prog-II-Lab5/src/`):
```text
StudentID-Prog-II-Lab5/
├── README.md
├── UML_Diagrams.md
└── src/
    ├── Main.java
    ├── Agent/
    │   ├── Agent.java
    │   ├── CrewMember.java
    │   ├── RoboticUnit.java
    │   ├── Vehicle.java
    │   ├── Scientist.java
    │   ├── Engineer.java
    │   ├── MedicalOfficer.java
    │   ├── ExplorationRover.java
    │   ├── RepairRobot.java
    │   └── Drone.java
    ├── Colony/
    │   └── Colony.java
    ├── Emergency/
    │   ├── Emergency.java
    │   ├── OxygenLeak.java
    │   ├── Fire.java
    │   ├── RoverBreakdown.java
    │   └── MedicalEmergency.java
    ├── Environment/
    │   └── Environment.java
    ├── Facility/
    │   ├── Facility.java
    │   ├── Habitat.java
    │   ├── LifeSupportSystem.java
    │   ├── Laboratory.java
    │   ├── PowerStation.java
    │   ├── StorageFacilities.java
    │   └── CommunicationStation.java
    ├── Helper/
    │   ├── Capability.java
    │   ├── EmergencyState.java
    │   ├── EnvironmentCondition.java
    │   ├── Equipment.java
    │   ├── MissionState.java
    │   └── ResourceType.java
    └── Mission/
        ├── Mission.java
        ├── ExplorationMission.java
        ├── RepairMission.java
        ├── ResearchMission.java
        └── RescueMission.java
```

### Compiling the Project
Open a terminal in the `src/` directory and run:
```bash
javac Main.java
```

### Running the Simulation
From the same `src/` directory, execute:
```bash
java Main
```

---

## 9. Executing the 5 Required Demonstration Scenarios

Running `java Main` automatically executes all five required demonstration scenarios sequentially and prints their outputs to the console:

1. **Scenario 1 (Successful Exploration)**: Initializes a colony, creates `Scientist` ("Dr. Mark Watney"), `ExplorationRover` ("Curiosity-2"), and `Mass Spectrometer`, prints the initial `Colony Status Report`, starts the `ExplorationMission`, advances time by $7$ Sols (`colony.advanceTime(7)`), verifies mission completion (`COMPLETED`), and prints the final `Colony Status Report`.
2. **Scenario 2 (Mission Rejection — Unsuitable Agent)**: Submits an `ExplorationMission` with an `Engineer` instead of a crew member with `SURFACE_EXPLORATION` capability. Prints the immediate rejection (`FAILED`) and reason (`"Agent does not possess the required capability."`) while confirming `activeMission` remains `NONE`.
3. **Scenario 3 (Resource Shortage)**: Depletes colony `BATTERY_POWER` to $0$ and attempts to start an `ExplorationMission`. Confirms the mission is rejected with `"There aren't available sufficient resources in the colony."` and verifies that `OXYGEN` and `BATTERY_POWER` balances remain unchanged and non-negative.
4. **Scenario 4 (Emergency Handling — Oxygen Leak)**: Triggers an `OxygenLeak` in `Habitat1`. Dispatches an available `Engineer` and `RepairRobot`, deducts $50$ `OXYGEN` from `Habitat1` plus $2$ `SPARE_PARTS` and $5$ `BATTERY_POWER` from the colony, and marks the emergency `RESOLVED`.
5. **Scenario 5 (Environmental Effect — Dust Storm)**: Sets the Martian environment to `DUST_STORM` and attempts to start an `ExplorationMission`. Confirms the mission fails immediately with `"Surface operations are not allowed during a dust storm."`