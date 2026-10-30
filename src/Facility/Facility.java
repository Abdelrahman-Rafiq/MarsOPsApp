package Facility;


import Helper.Capability;
import Helper.Equipment;
import Helper.ResourceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Facility
{
  private final String location;
  private int currentOccupancy;
  private final int capacity;
  private boolean isOperational;
  private final List<Equipment> equipments;
  protected Map<ResourceType,Integer> resources;
  protected final int maxResources;
  protected static final int POWER_COST = 10;
  protected static final int OXYGEN_COST = 5;


  public Facility(String name, int capacity, int currentOccupancy, int maxResources, Equipment[] equipment)
  {
      this.location = name;
      this.capacity = capacity;
      this.currentOccupancy = Math.max(0, Math.min(currentOccupancy, capacity));
      this.isOperational = true;
      this.maxResources = maxResources;
      this.resources = new HashMap<>();
      for(ResourceType type : ResourceType.values())
          this.resources.put(type, 0);
      this.equipments = new ArrayList<>();
      if (equipment != null) {
          this.equipments.addAll(Arrays.asList(equipment));
      }
  }
  public String getLocation() {
      return location;
  }
  public int getCapacity() {
      return capacity;
  }
  public int getCurrentOccupancy() {
    return currentOccupancy;
  }
  public boolean  addOccupiers() {
    if(currentOccupancy==getCapacity())
        return false;
    currentOccupancy++;
    return true;
  }
  public boolean  removeOccupiers() {
    if(currentOccupancy==0)
        return false;
    currentOccupancy--;
    return true;
  }
  public List<Equipment> getEquipments() {
      return equipments;
  }
  public int getEquipmentIndex(Capability capability) {
      for(int i=0;i<equipments.size();i++)
          if(equipments.get(i).getProvidedCapability().equals(capability))
              return i;
        return -1;
  }
  public int getMaxResources() {
      return maxResources;
  }
  public boolean isOperational() {
      return isOperational;
  }
  public void setOperationalStatus(boolean status)
  {
          this.isOperational = status;
  }

  public boolean addResource(ResourceType name, int amount)
  {
      if (amount < 0 || !resources.containsKey(name)) {
          return false;
      }
      int current = resources.get(name);
      if (current + amount <= maxResources) {
          resources.put(name, current + amount);
          return true;
      }
      return false;
  }
  public int deductResource(ResourceType name, int amount) {
      if (amount <= 0 || !resources.containsKey(name)) {
          return 0;
      }
      int currentAvailable = resources.get(name);
      int actualDeducted = Math.min(currentAvailable, amount);
      resources.put(name, currentAvailable - actualDeducted);
      return actualDeducted;
  }
  public int getResourceAmount(ResourceType type) {
    return resources.getOrDefault(type, 0);
  }
    protected boolean hasEnoughResource(ResourceType type, int requiredAmount) {
        return getResourceAmount(type) >= requiredAmount;
    }
  public abstract boolean useResources();
  @Override
  public String toString() {
      StringBuilder resBuilder = new StringBuilder();
      for (ResourceType type : ResourceType.values()) {
            if (resBuilder.length() > 0) resBuilder.append(", ");
            resBuilder.append(type.name()).append(": ").append(resources.getOrDefault(type, 0));
      }
      String eqStr = equipments.isEmpty() ? "None" : equipments.toString();
      return String.format("[%s] %-18s | Status: %-7s | MaxRes: %4d | Resources: [%s] | Equipment: %s | Capacity: %2d/%2d",
              getClass().getSimpleName(),
              location,
              (isOperational ? "ONLINE" : "OFFLINE"),
              maxResources,
              resBuilder,
              eqStr,
              currentOccupancy,
              capacity);
  }
  public boolean transferResourceTo(Facility target, ResourceType type, int amount) {
      if (target == null || target == this || type == null || amount <= 0) {
            return false;
      }
      if (!this.hasEnoughResource(type, amount)) {
            return false;
      }
      if (target.addResource(type, amount)) {
            this.deductResource(type, amount);
            return true;
      }
      return false;
  }

}
