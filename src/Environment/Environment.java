package Environment;

import Helper.EnvironmentCondition;
import java.util.Random;

public class Environment {
    private static EnvironmentCondition environmentCondition = EnvironmentCondition.NORMAL;

    public Environment() {
        environmentCondition = EnvironmentCondition.NORMAL;
    }

    public void setCondition(EnvironmentCondition condition) {
        environmentCondition = condition;
    }

    public static void setState(EnvironmentCondition condition) {
        environmentCondition = condition;
    }

    public void changeState() {
        Random random = new Random();
        int number = random.nextInt(100);
        if (number <= 84) {
            // occurrence percent of normal condition in a day is 84% 0 -> 84
            environmentCondition = EnvironmentCondition.NORMAL;
        } else if (number <= 91) {
            // occurrence percent of Dust Storm in a day is 7% 85 -> 91
            environmentCondition = EnvironmentCondition.DUST_STORM;
        } else {
            // occurrence percent of COMMUNICATION_DISRUPTION in a day is 7% 92 -> 99
            environmentCondition = EnvironmentCondition.COMMUNICATION_DISRUPTION;
        }
    }

    public static EnvironmentCondition getState() {
        return environmentCondition;
    }

    public String getStateString() {
        return environmentCondition.toString();
    }

    @Override
    public String toString() {
        return String.format("Environment{Condition=%s}", environmentCondition);
    }
}
