package Emergency;

import Agent.Agent;
import Agent.CrewMember;
import Colony.Colony;
import Facility.Habitat;
import Helper.Capability;
import Helper.EmergencyState;
import Helper.ResourceType;

public class MedicalEmergency extends Emergency {
    private final CrewMember patient;
    private final Habitat medicalHabitat;
    public static final int REQUIRED_MEDICAL_SUPPLIES = 5;

    public MedicalEmergency(CrewMember patient, Habitat habitat) {
        super("Medical emergency for " + patient.getName(),
                habitat.getLocation() ,
                new Capability[]{Capability.MEDICAL_CARE});
        this.patient = patient;
        this.medicalHabitat = habitat;
    }

    @Override
    public boolean handle(Colony colony) {
        this.emergencyState = EmergencyState.TRIGGERED;

        if (!medicalHabitat.isOperational()) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Medical facility/Habitat is not operational.";
            return false;
        }

        Agent doctor = null;
        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isAvailable() && !agent.isOnMission() && agent != patient && agent.canHandle(Capability.MEDICAL_CARE)) {
                doctor = agent;
                break;
            }
        }

        if (doctor == null) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "No available medical officer to attend to the patient.";
            return false;
        }

        if (colony.getTotalResource(ResourceType.MEDICAL_SUPPLIES) < REQUIRED_MEDICAL_SUPPLIES) {
            this.emergencyState = EmergencyState.UNRESOLVED;
            this.failureReason = "Insufficient medical supplies in the colony.";
            return false;
        }

        doctor.setLocation(medicalHabitat.getLocation());
        patient.setLocation(medicalHabitat.getLocation());

        colony.deductResource(ResourceType.MEDICAL_SUPPLIES, REQUIRED_MEDICAL_SUPPLIES);

        patient.restoreHealth(35);
        this.emergencyState = EmergencyState.RESOLVED;
        this.failureReason = "";
        return true;
    }

    public CrewMember getPatient() {
        return patient;
    }

    public Habitat getMedicalHabitat() {
        return medicalHabitat;
    }
}
