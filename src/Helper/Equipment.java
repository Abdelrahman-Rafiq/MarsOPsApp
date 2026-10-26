package Helper;

public class Equipment {
    private final String name;
    private final Capability providedCapability;
    private boolean damaged;
    private int usageNumber;
    public Equipment(String name, Capability providedCapability, boolean damaged) {
        this.name = name;
        this.providedCapability = providedCapability;
        this.damaged = damaged;
        this.usageNumber = 0;
    }
    public String getName() {return name;}
    public Capability getProvidedCapability() {return providedCapability;}
    public boolean isDamaged(){return damaged;}
    public boolean useIt(){
        if(damaged){
            return false;
        }
        usageNumber++;
        if(usageNumber <= 7) damaged = true;
        return true;
    }
    public void fixIt(){
        usageNumber=0;
        damaged = false;
    }
}
