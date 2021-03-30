package com.company.things;

import com.company.assets.Tool;
import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.people.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Project {

    // FIELDS

    private final Client client;
    private final String name;
    private final List<Technology> technologies;
    private final Integer paymentDelayDays;
    private final Boolean isPaymentNever;
    private final Boolean isPenaltyAvoidedWithinWeekOfDelay;
    private final Boolean isProblemFromNotWorkingProject;


    // CONSTRUCTORS

    public Project(Client client){
        this.client = client;
        name = generateProjectName();
        technologies = generateTechnologies();
        paymentDelayDays = calculatePaymentDelayDays();
        isPaymentNever = isPaymentNever();
        isPenaltyAvoidedWithinWeekOfDelay = isPenaltyAvoidedWithinWeekOfDelay();
        isProblemFromNotWorkingProject = isProblemFromNotWorkingProject();
    }


    // GETTERS

    public Client getClient() { return client; }
    public String getName() { return name; }
    public List<Technology> getTechnologies(){ return technologies; }
    public Integer getPaymentDelayDays() { return paymentDelayDays; }
    public Boolean getIsPaymentNever() { return isPaymentNever; }
    public Boolean getIsPenaltyAvoidedWithinWeekOfDelay() { return isPenaltyAvoidedWithinWeekOfDelay; }
    public Boolean getIsProblemFromNotWorkingProject() { return isProblemFromNotWorkingProject; }


    // calculates price for project (num days for all techs * 8 hours * pay4hour + 10%)
    public Double getPrice(){
        double cost = ( (double)getTotalWorkDaysNeeded() * 8.0 * Conf.PAY_FOR_HOUR ) * 1.1;
        cost = (double) (Math.round(cost * 100.0) / 100);
        return cost;
    }


    // penalty for not completing project on time
    public Double getPenaltyPrice() {
        // by default penalty is 10% of project's price
        return getPrice() * Conf.PENALTY_MULTIPLIER;
    }


    // a number of days when client will pay after project is completed
    private Integer getPaymentDue(){
        // by default payment due is dependent on complexity of project (its number of techs)
        // 1 tech - 7 days, 2,3 - 14 days, more - 21 days
        switch(technologies.size()){
            case 1: return 7;
            case 2, 3: return 14;
            default: return 21;
        }
    }


    public Integer getTotalWorkDaysNeeded(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.getWorkDaysNeeded();
        return days;
    }


    public Integer getTotalWorkDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.getWorkDaysDone();
        return days;
    }


    public Boolean isCodeCompleted(){
        for (Technology tech:technologies)
            if (tech.getWorkDaysDone() < tech.getWorkDaysNeeded())
                return false;
        return true;
    }


    public Boolean isTestCompleted(){
        for (Technology tech:technologies)
            if (tech.getTestDaysDone() < tech.getWorkDaysDone())
                return false;
        return true;
    }


    public Boolean hasMobileTech(){
        for (Technology tech:technologies)
            if (tech.getName().equals("Mobile"))
                return true;
        return false;
    }


    // PRIVATE METHODS

    private String generateProjectName(){
        int index = Tool.randInt(0, Lang.projectNames.length - 1);
        return Lang.projectNames[index];
    }


    // generates random number of random but unique techs for project
    private List<Technology> generateTechnologies(){
        String[] techNames = Lang.technologyNames;
        Collections.shuffle(Arrays.asList(techNames));
        List<Technology> techs = new ArrayList<>();
        for(int i = 0; i <= Tool.randInt(0, Conf.MAX_TECHNOLOGIES_PER_PROJECT - 1); i++)
            techs.add(new Technology(techNames[i]));
        return techs;
    }


    private Integer calculatePaymentDelayDays(){
        int delay = 0;

        // payment delay depends on the client's personal traits (chances)
        if (client.getPaymentDelayWeekChance() > 0)
            if (Tool.randInt(1,100) <= client.getPaymentDelayWeekChance())
                delay += 7;

        if (client.getPaymentDelayMonthChance() > 0)
            if (Tool.randInt(1,100) <= client.getPaymentDelayMonthChance())
                delay += 30;

        return delay;
    }


    private Boolean isPaymentNever(){
        // client will never pay for the project
        if (client.getPaymentNeverChance() > 0)
            return Tool.randInt(1, 100) <= client.getPaymentNeverChance();
        return false;
    }


    private Boolean isPenaltyAvoidedWithinWeekOfDelay(){
        // chance to avoid penalty if project will be delayed by no more than one week
        if (client.getDelayWeekPenaltyAvoidChance() > 0)
            return Tool.randInt(1,100) <= client.getDelayWeekPenaltyAvoidChance();
        return false;
    }


    private Boolean isProblemFromNotWorkingProject(){
        // chance to avoid problems (penalty fee) when you deliver unfinished project to client
        if (client.getDelayWeekPenaltyAvoidChance() > 0)
            return Tool.randInt(1,100) <= client.getDelayWeekPenaltyAvoidChance();
        return false;
    }
}
