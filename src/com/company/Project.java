package com.company;

import com.company.people.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Project {

    String name;
    Client client;
    List<Technology> technologies = new ArrayList<>();
    Double price;
    Double penalty;
    Boolean penaltyAvoidWithinWeekOfDelay = false;
    Integer paymentDue;
    Integer paymentDelayDays = 0;
    Boolean paymentNever = false;
    Double payForHour = 100.0;

    String[] projectNames = {
            "E-Commerce Solution", "Company Web Page", "Company Web Page Integration", "Software Upgrade",
            "Software Update", "Simulation Software", "Scientific Research Project", "Infrastructure Upgrade",
            "Infrastructure Update", "API Implementation", "Security Solutions"
    };

    public Project(Client client){

        name = generateProjectName();
        this.client = client;

        // adds random number of random but unique techs to project
        Collections.shuffle(Arrays.asList(Technology.technologyNames));
        for(int i = 0; i <= Game.randInt(0,4); i++)
            technologies.add(new Technology(Technology.technologyNames[i]));

        // calculates price for project (num days * 8 hours * pay4hour + 10%)
        price = (Double.valueOf(getTotalWorkDaysNeeded()) * 8 * payForHour) * 1.1;

        // by default payment delay is dependent on complexity of project and its number of techs
        // 1 tech - 7 days, 2,3 - 14 days, more - 21 days
        switch(getNumberOfTechs()){
            case 1:
                paymentDue = 7;
                break;
            case 2:
            case 3:
                paymentDue = 14;
                break;
            default:
                paymentDue = 21;
        }

        // payment delay depends on the client's personal traits (chances)
        if (client.paymentDelayWeekChance > 0)
            if (Game.randInt(1,100) <= client.paymentDelayWeekChance)
                paymentDelayDays += 7;

        if (client.paymentDelayMonthChance > 0)
            if (Game.randInt(1,100) <= client.paymentDelayMonthChance)
                paymentDelayDays += 30;

        // client will never pay for the project
        if (client.paymentNeverChance > 0)
            if (Game.randInt(1,100) <= client.paymentNeverChance)
                paymentNever = true;

        // penalty for not completing project on time is 10% of total project's price
        penalty = price * 0.1;

        // avoid penalty if project will be delayed by no more than one week
        if (client.delayWeekPenaltyAvoidChance > 0)
            if (Game.randInt(1,100) <= client.delayWeekPenaltyAvoidChance)
                penaltyAvoidWithinWeekOfDelay = true;
    }


    String generateProjectName(){
        return projectNames[Game.randInt(0,projectNames.length-1)];
    }


    Integer getTotalWorkDaysNeeded(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.workDaysNeeded;
        return days;
    }


    Integer getNumberOfTechs(){
        return technologies.size();
    }
}
