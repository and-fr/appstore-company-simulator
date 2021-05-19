package com.company.people;

import com.company.assets.Tool;


public class Client extends Person {

    public Integer paymentDelayWeekChance = 0;
    public Integer paymentDelayMonthChance = 0;
    public Integer paymentNeverChance = 0;
    public Integer delayWeekPenaltyAvoidChance = 0;
    public Integer problemsFromNotWorkingProjectChance = 0;


    public Client(){
        // specific values
        // client types: 1 (easy going), 2 (demanding), 3 (unpredictable)
        switch(Tool.randInt(1,3)){
            case 1:
                paymentDelayWeekChance = 30;
                delayWeekPenaltyAvoidChance = 20;
                break;
            case 2:
                problemsFromNotWorkingProjectChance = 50;
                break;
            case 3:
                paymentDelayWeekChance = 30;
                paymentDelayMonthChance = 5;
                paymentNeverChance = 3;
                problemsFromNotWorkingProjectChance = 100;
                break;
        }
    }
}
