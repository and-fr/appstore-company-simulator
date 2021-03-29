package com.company;

public class Technology {

    String name;
    Integer workDaysNeeded = 0;
    Integer workDaysDone = 0;
    Integer testDaysDone = 0;

    public static String[] technologyNames = { "Front-End", "Backend", "Database", "Mobile", "Wordpress", "Prestashop" };



    public Technology(String name){

        this.name = name;

        switch (this.name){
            case "Front-End":
                workDaysNeeded = Game.randInt(7,15);
                break;
            case "Backend":
                workDaysNeeded = Game.randInt(7,21);
                break;
            case "Database":
                workDaysNeeded = Game.randInt(7,20);
                break;
            case "Mobile":
                workDaysNeeded = Game.randInt(7,14);
                break;
            case "Wordpress":
                workDaysNeeded = Game.randInt(3,7);
                break;
            case "Prestashop":
                workDaysNeeded = Game.randInt(5,10);
                break;
        }
    }

}
