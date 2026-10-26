package Environment;

import Helper.EnvironmentCondition;

import java.util.Random;

public class Environment {
    private EnvironmentCondition environmentCondition;
    protected Environment() {
        environmentCondition = EnvironmentCondition.NORMAL;
    }
    protected void changeState(){
        Random random= new Random();
        int number = random.nextInt(100);
        if (number <= 84) {
            //occurrence percent of normal condition in a day is 84% 0 ->84
            environmentCondition = EnvironmentCondition.NORMAL;
        }
        else if (number <= 91) {
            //occurrence percent of Dust Storm in a day is 7% 85 ->91
            environmentCondition = EnvironmentCondition.DUST_STORM;
        }
        else{
            //occurrence percent of COMMUNICATION_DISRUPTION in a day is 7% 92 ->99
            environmentCondition = EnvironmentCondition.COMMUNICATION_DISRUPTION;
        }
    }
    protected EnvironmentCondition getState(){
        return environmentCondition;
    }
    protected String getStateString(){
        return environmentCondition.toString();
    }
}
