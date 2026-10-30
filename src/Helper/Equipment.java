package Helper;

public class Equipment {
    private final String name;
    private final Capability providedCapability;
    private boolean damaged;
    private int usageNumber;
    private boolean available;

    public Equipment(String name, Capability providedCapability) {
        this.name = name;
        this.providedCapability = providedCapability;
        this.damaged = false;
        this.usageNumber = 0;
        this.available = true;
    }

    // Scientific tools
    public static Equipment createSpectrometer() {
        return new Equipment("Mass Spectrometer", Capability.SCIENTIFIC_RESEARCH);
    }

    public static Equipment createMicroscope() {
        return new Equipment("Electron Microscope", Capability.SCIENTIFIC_RESEARCH);
    }

    // Repair tools
    public static Equipment createRepairToolkit() {
        return new Equipment("Advanced Repair Toolkit", Capability.EQUIPMENT_REPAIR);
    }

    public static Equipment createWeldingTorch() {
        return new Equipment("Plasma Welding Torch", Capability.EQUIPMENT_REPAIR);
    }

    // Medical tools
    public static Equipment createMedicalScanner() {
        return new Equipment("Bio-Medical Diagnostic Scanner", Capability.MEDICAL_CARE);
    }

    public static Equipment createSurgicalKit() {
        return new Equipment("Emergency Surgical Kit", Capability.MEDICAL_CARE);
    }

    // Communication tools
    public static Equipment createAntennaArray() {
        return new Equipment("Deep Space Antenna Array", Capability.COMMUNICATION);
    }

    public static Equipment createDroneControlTerminal() {
        return new Equipment("Remote Drone Control Terminal", Capability.COMMUNICATION);
    }

    // Additional tools
    public static Equipment createFireExtinguisher() {
        return new Equipment("Chemical Fire Suppressor", Capability.FIRE_RESPONSE);
    }

    public String getName() {
        return name;
    }

    public Capability getProvidedCapability() {
        return providedCapability;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public boolean isAvailable() {
        return available && !damaged;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setAvailability(boolean available) {
        this.available = available;
    }

    public boolean useIt() {
        if (damaged) {
            return false;
        }
        usageNumber++;
        if (usageNumber >= 7) {
            damaged = true;
            available = false;
        }
        return true;
    }

    public void fixIt() {
        usageNumber = 0;
        damaged = false;
        available = true;
    }

    @Override
    public String toString() {
        return String.format("Equipment{Name='%s', Capability=%s, Status=%s, Available=%b, UsageCount=%d}",
                name,
                providedCapability,
                (damaged ? "DAMAGED" : "OK"),
                available,
                usageNumber);
    }
}
