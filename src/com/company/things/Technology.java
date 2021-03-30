package com.company.things;

import com.company.assets.Tool;


public class Technology {

    // FIELDS

    private final String name;
    private final Integer workDaysNeeded;
    private Integer workDaysDone;
    private Integer testDaysDone;


    // CONSTRUCTORS

    public Technology(String name){

        this.name = name;
        workDaysDone = 0;
        testDaysDone = 0;

        switch (this.name){
            case "Front-End":
                workDaysNeeded = Tool.randInt(7,15);
                break;
            case "Backend":
                workDaysNeeded = Tool.randInt(7,21);
                break;
            case "Database":
                workDaysNeeded = Tool.randInt(7,20);
                break;
            case "Mobile":
                workDaysNeeded = Tool.randInt(7,14);
                break;
            case "Wordpress":
                workDaysNeeded = Tool.randInt(3,7);
                break;
            case "Prestashop":
                workDaysNeeded = Tool.randInt(5,10);
                break;
            default:
                workDaysNeeded = 0;
        }
    }


    // GETTERS

    public String getName(){
        return name;
    }
    public Integer getWorkDaysNeeded() {
        return workDaysNeeded;
    }
    public Integer getWorkDaysDone() {
        return workDaysDone;
    }
    public Integer getTestDaysDone() { return testDaysDone; }


    // SETTERS

    public void setWorkDaysDonePlus(int days){ workDaysDone += days; }
    public void setTestDaysDonePlus(int days){ testDaysDone += days; }
}
