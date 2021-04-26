package com.company.people;

import com.company.assets.Tool;


public class Client extends Person {

    private Integer paymentDelayWeekChance;
    private Integer paymentDelayMonthChance;
    private Integer paymentNeverChance;
    private Integer delayWeekPenaltyAvoidChance;
    private Integer problemsFromNotWorkingProjectChance;


    public Client(){
        // default values
        paymentDelayWeekChance = 0;
        paymentDelayMonthChance = 0;
        paymentNeverChance = 0;
        delayWeekPenaltyAvoidChance = 0;
        problemsFromNotWorkingProjectChance = 0;

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


    public Integer getPaymentDelayWeekChance() { return paymentDelayWeekChance; }
    public Integer getPaymentDelayMonthChance() { return paymentDelayMonthChance; }
    public Integer getPaymentNeverChance() { return paymentNeverChance; }
    public Integer getDelayWeekPenaltyAvoidChance() { return delayWeekPenaltyAvoidChance; }
    public Integer getProblemsFromNotWorkingProjectChance() { return problemsFromNotWorkingProjectChance; }

}
