package com.company.things;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Contractor;
import com.company.people.Employee;


public class Technology {

    // FIELDS

    private final String name;
    private final Integer codeDaysNeeded;
    private Integer codeDaysDone;
    private Integer testDaysDone;
    private Contractor contractor;
    private Integer contractorCodeDays;
    private Integer contractorTestDays;
    private Integer contractorTestFailureDays;
    private Boolean isContractorWorkFinished;


    // CONSTRUCTORS

    public Technology(String name){

        this.name = name;
        codeDaysDone = 0;
        testDaysDone = 0;
        contractor = null;
        contractorCodeDays = 0;
        contractorTestDays = 0;
        contractorTestFailureDays = 0;
        isContractorWorkFinished = false;

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


    // GETTERS

    public String getName(){
        return name;
    }
    public Integer getCodeDaysNeeded() {
        return codeDaysNeeded;
    }
    public Integer getCodeDaysDone() {
        return codeDaysDone;
    }
    public Integer getTestDaysDone() { return testDaysDone; }
    public Contractor getContractor() { return contractor; }
    public Integer getContractorCodeDays() { return contractorCodeDays; }
    public Integer getContractorTestDays() { return contractorTestDays; }
    public Integer getContractorTestFailureDays() { return contractorTestFailureDays; }
    public Boolean isContractorAssigned() { return contractor != null; }
    public Boolean isContractorWorkFinished() { return isContractorWorkFinished; }
    public Boolean isCodeCompleted() { return codeDaysDone >= codeDaysNeeded; }
    public Boolean isTestCompleted() { return testDaysDone >= codeDaysNeeded; }
    public Boolean isFinished() { return (codeDaysDone >= codeDaysNeeded) && (testDaysDone >= codeDaysNeeded); }
    public Double getContractorCost() { return (double) (contractorCodeDays + contractorTestDays) * 8.0 * contractor.getPayForHour(); }
    public Integer getContractorWorkDays() { return contractorCodeDays + contractorTestDays; }


    // SETTERS

    public void setCodeDaysDonePlus(int days){ codeDaysDone += days; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }
    public void setContractorCodeDaysPlus(int days){ this.contractorCodeDays += days; }
    public void setContractorTestDaysPlus(int days){ this.contractorTestDays += days; }
    public void setContractorTestFailureDaysPlus(int days){ this.contractorTestFailureDays += days; }
    public void setIsContractorWorkFinished(boolean isFinished) { this.isContractorWorkFinished = isFinished; }
    public void removeContractor() { contractor = null; }

    public void setTestDaysDonePlus(int days){
        if (testDaysDone < codeDaysDone)
            testDaysDone += days;
    }


    // OTHER METHODS

    public void setLuckyTestDayForPlayer(){
        // player has a percent chance of having a "lucky coding day"
        // when code is so good that one test day for that tech is received for free
        if (Tool.randInt(1, 100) <= Conf.PLAYER_LUCKY_TEST_DAY_CHANCE_PERCENT){
            setTestDaysDonePlus(1);
            System.out.println("(INFO) You had your LUCKY programming day. Your code was so good that one free test day was added.\n");
        }
    }

}
