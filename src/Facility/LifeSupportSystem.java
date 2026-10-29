package Facility;

public class LifeSupportSystem {
    private final int filtrationRate;
    private boolean isFunctional;
    
    public LifeSupportSystem(int filtrationRate)
    {
        this.filtrationRate = filtrationRate;
        this.isFunctional = true;
    }
    public int getFiltrationRate() {
        return filtrationRate;
    }
    public boolean isFunctional() { 
        return isFunctional; 
    }
    public void setFunctional(boolean functional) { 
        isFunctional = functional; 
    }
    @Override
    public String toString() {
        return String.format("Filtration Rate: %d | Status: %-7s",filtrationRate,(isFunctional ? "ONLINE" : "OFFLINE"));
    }
}
