package Facility;

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
  private final int capacity;
  private boolean isOperational;
  private List<Equipment> equipments;
  protected Map<ResourceType,Integer> resources;
  protected final int maxResources;
  protected static final int oxygenUsage=5;

  public Facility(String name, int capacity, int maxResources, Equipment[] equipment)
  {
      this.location = name;
      this.capacity = capacity;
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
  public List<Equipment> getEquipments() {
      return equipments;
  }
  public void addEquipment(Equipment equipment) {
        this.equipments.add(equipment);
  }
  public int getMaxResources() {
      return maxResources;
  }
  public boolean isOperational() {
      return isOperational;
  }
  public void setOperationalStatus(boolean Status)
  {
          this.isOperational = Status;
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
  @Override
  public String toString() {
      StringBuilder resBuilder = new StringBuilder();
      for (ResourceType type : ResourceType.values()) {
            if (resBuilder.length() > 0) resBuilder.append(", ");
            resBuilder.append(type.name()).append(": ").append(resources.getOrDefault(type, 0));
      }
      String eqStr = equipments.isEmpty() ? "None" : equipments.toString();
      return String.format("[%s] %-18s | Status: %-7s | Cap: %2d | MaxRes: %4d | Resources: [%s] | Equipment: %s",
              getClass().getSimpleName(),
              location,
              (isOperational ? "ONLINE" : "OFFLINE"),
              capacity,
              maxResources,
              resBuilder,
              eqStr);
  }
}
