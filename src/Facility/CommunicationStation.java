package Facility;

import Helper.EnvironmentCondition;
import Helper.Equipment;
import Environment.*;
import Helper.ResourceType;

public class CommunicationStation extends Facility {
    private static int nextID = 1;
    private static int maxCapacity = 200;

    public CommunicationStation() {
        super("Communication Station" + (nextID++), 2, 0, maxCapacity,
                new Equipment[]{Equipment.createRepairToolkit(), Equipment.createAntennaArray(), Equipment.createDroneControlTerminal()});
        addResource(ResourceType.BATTERY_POWER,maxCapacity);
        addResource(ResourceType.OXYGEN,maxCapacity);
    }

    public void changeOperationalStatus(Environment environment) {
        if (Environment.getState() == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
            super.setOperationalStatus(false);
        }
    }

    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (!hasEnoughResource(ResourceType.BATTERY_POWER, POWER_COST) ||
                !hasEnoughResource(ResourceType.OXYGEN, OXYGEN_COST * getCurrentOccupancy())) {
                super.setOperationalStatus(false);
                return false;
            }

            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            deductResource(ResourceType.OXYGEN, OXYGEN_COST * getCurrentOccupancy());
            return true;
        }
        return false;
    }
}
