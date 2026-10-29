package Facility;

import Helper.EnvironmentCondition;
import Helper.Equipment;
import Environment.*;
import Helper.ResourceType;

public class CommunicationStation extends Facility {
    private static int nextID=1;
    public CommunicationStation(int capacity, int currentOccupancy, int maxResources) {
        super("Communication Station"+(nextID++), capacity, currentOccupancy, maxResources,new Equipment[]{Equipment.createRepairToolkit(),Equipment.createAntennaArray(),Equipment.createDroneControlTerminal()});
    }
    public void changeOperationalStatus(Environment environment)
    {
        if(environment.getState()== EnvironmentCondition.COMMUNICATION_DISRUPTION)
            super.setOperationalStatus(false);
    }
    @Override
    public boolean useResources() {
        if (isOperational()) {
            if (hasEnoughResource(ResourceType.BATTERY_POWER,POWER_COST)||
                     hasEnoughResource(ResourceType.OXYGEN,OXYGEN_COST*getCurrentOccupancy())) {
                super.setOperationalStatus(false);
                return false;
            }

            deductResource(ResourceType.BATTERY_POWER, POWER_COST);
            deductResource(ResourceType.OXYGEN, OXYGEN_COST*getCurrentOccupancy());
            return true;
        }
        return false;
    }

}
