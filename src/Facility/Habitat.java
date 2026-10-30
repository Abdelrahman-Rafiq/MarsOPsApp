package Facility;

import Helper.Equipment;
import Helper.ResourceType;

public class Habitat extends Facility
{
    private int id;
    private String location;
    private final LifeSupportSystem lifeSupport;
    private static int nextID = 1;
    private static final int maxCapacity=4;
    private static final int maxResources=300;

    public Habitat() {
        super("Habitat"+(nextID++), maxCapacity, 0, maxResources, new Equipment[]{Equipment.createRepairToolkit(),Equipment.createAntennaArray(),Equipment.createSurgicalKit(),Equipment.createMedicalScanner()});
        this.lifeSupport = new LifeSupportSystem(90);
        addResource(ResourceType.BATTERY_POWER,maxResources);
        addResource(ResourceType.OXYGEN,maxResources);
        addResource(ResourceType.FOOD,maxResources);
        addResource(ResourceType.WATER,maxResources);
    }

    @Override
    public boolean useResources() {
        if (isOperational()) {
            int requiredPower = POWER_COST;
            int requiredOxygen = OXYGEN_COST * getCurrentOccupancy();

            if (!hasEnoughResource(ResourceType.BATTERY_POWER, requiredPower) ||
                    !hasEnoughResource(ResourceType.OXYGEN, requiredOxygen)) {
                setOperationalStatus(false);
                return false;
            }

            deductResource(ResourceType.BATTERY_POWER, requiredPower);
            deductResource(ResourceType.OXYGEN, requiredOxygen);

            if (lifeSupport.isFunctional()) {
                addResource(ResourceType.OXYGEN, lifeSupport.getFiltrationRate());
            }

            return true;
        }
        return false;
    }
    public LifeSupportSystem getLifeSupport() {
        return lifeSupport;
    }
    @Override
    public String toString() {
        return String.format("%s | Life Support System: {%s}",
                super.toString(),
                lifeSupport.toString());
    }
}
