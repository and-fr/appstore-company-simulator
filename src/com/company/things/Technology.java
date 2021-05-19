package com.company.things;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Contractor;


public class Technology {

    public final String name;
    public final Integer codeDaysNeeded;
    public Integer codeDaysDone = 0;
    public Integer testDaysDone = 0;
    public Contractor contractor = null;
    public Integer contractorCodeDays = 0;
    public Integer contractorTestDays = 0;
    public Integer contractorTestFailureDays = 0;
    public Boolean isContractorWorkFinished = false;


    public Technology(String name){
        this.name = name;

        switch (this.name){
            case "Front-End":
                codeDaysNeeded = Tool.randInt(5,15);
                break;
            case "Backend":
                codeDaysNeeded = Tool.randInt(5,21);
                break;
            case "Database":
                codeDaysNeeded = Tool.randInt(5,20);
                break;
            case "Mobile":
                codeDaysNeeded = Tool.randInt(5,14);
                break;
            case "Wordpress":
                codeDaysNeeded = Tool.randInt(1,7);
                break;
            case "Prestashop":
                codeDaysNeeded = Tool.randInt(3,10);
                break;
            default:
                codeDaysNeeded = 0;
        }
    }


    public Boolean isContractorAssigned() { return contractor != null; }
    public Boolean isCodeCompleted() { return codeDaysDone >= codeDaysNeeded; }
    public Boolean isFinished() { return (codeDaysDone >= codeDaysNeeded) && (testDaysDone >= codeDaysNeeded); }
    public Double getContractorCost() { return (double) (contractorCodeDays + contractorTestDays) * 8.0 * contractor.payForHour; }
    public Integer getContractorWorkDays() { return contractorCodeDays + contractorTestDays; }


    public String setLuckyTestDayForPlayer(){
        // player has a percent chance of having a "lucky coding day"
        // when code is so good that one test day for that tech is received for free

        String info = "";
        if (Tool.randInt(1, 100) <= Conf.PLAYER_LUCKY_TEST_DAY_CHANCE_PERCENT){
            if (testDaysDone < codeDaysDone) {
                testDaysDone += 1;
                info = "You had your LUCKY programming day. Your code was so good that one free test day was added.";
            }
        }
        return info;
    }

}
