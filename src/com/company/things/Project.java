package com.company.things;

import com.company.assets.Tool;
import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.people.Client;
import com.company.people.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class Project {

    public final Client client;
    public final String name = generateProjectName();
    public final List<Technology> technologies = generateTechnologies();
    public final Integer paymentDelayDays;
    public final Boolean isPaymentNever;
    public final Boolean isPenaltyAvoidedWithinWeekOfDelay;
    public final Boolean isProblemFromNotWorkingProject;
    public final Integer totalWorkDaysNeeded;
    private final Double price;
    public final Double paymentAdvance;
    public Boolean isPlayerInvolved = false;
    public LocalDate startDate;
    public Employee seller = null;
    public Employee tester = null;
    public final List<Employee> programmers = new ArrayList<>();
    public Double priceBonus = 0.0;
    public Transaction transaction = null;


    public Project(Client client){
        this.client = client;
        totalWorkDaysNeeded = calculateTotalWorkDaysNeeded();
        paymentDelayDays = calculatePaymentDelayDays();
        isPaymentNever = calculatePaymentNever();
        isPenaltyAvoidedWithinWeekOfDelay = calculatePenaltyAvoidedWithinWeekOfDelay();
        isProblemFromNotWorkingProject = calculateProblemFromNotWorkingProject();
        price = generatePrice();
        paymentAdvance = calculatePaymentAdvance();
    }


    public Boolean isNegotiatedBySeller() { return seller != null; }
    public Double getPrice() { return price + priceBonus; }
    public LocalDate getDeadline() { return startDate.plusDays((long)totalWorkDaysNeeded); }
    public Boolean isTesterAssigned() { return tester != null; }
    public Integer getCodeCompletionPercent(){ return (int) (((double)getCodeDaysDone() / (double)getCodeDaysNeeded()) * 100.0); }
    public Integer getTestCompletionPercent(){ return (int) (((double)getTestDaysDone() / (double)getCodeDaysNeeded()) * 100.0); }
    public Integer getCompletionPercent(){ return (getCodeCompletionPercent() + getTestCompletionPercent()) / 2; }
    public Integer getDaysOfDelay(LocalDate gameDate) { return (int) ChronoUnit.DAYS.between(getDeadline(), gameDate); }


    public void addProgrammer(Employee programmer) { programmers.add(programmer); }
    public void removeProgrammer(Employee programmer) { programmers.remove(programmer); }


    public Double generatePrice(){
        // calculates price for project (code days & test days for all techs * 8 hours * pay4hour)

        double payForWorkDay = Conf.PAY_FOR_HOUR * 8.0;
        double cost = (double)getCodeDaysNeeded() * 2.0 * payForWorkDay;
        cost = (double) (Math.round(cost * 100.0) / 100);

        // for project with totalDaysNeeded < codeDaysNeeded * 2
        // its price depends on the number of days of difference between the two
        int daysDiff = (getCodeDaysNeeded() * 2) - totalWorkDaysNeeded;
        cost += (double) daysDiff * payForWorkDay;

        return cost;
    }


    public Integer getCodeDaysNeeded(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.codeDaysNeeded;
        return days;
    }


    public Integer getCodeDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.codeDaysDone;
        return days;
    }


    public Integer getTestDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.testDaysDone;
        return days;
    }


    public Double getPenaltyPrice() {
        // penalty for not completing project on time
        // by default penalty is 10% of project's price
        return getPrice() * Conf.PRICE_PENALTY_MULTIPLIER;
    }


    public Integer getPaymentDaysDue(){
        // a number of days when client will pay after project is completed
        // by default payment due is dependent on complexity of project (its number of techs)
        // 1 tech - 7 days, 2,3 - 14 days, more - 21 days
        switch(technologies.size()){
            case 1: return 7;
            case 2, 3: return 14;
            default: return 21;
        }
    }


    public Boolean isFinished(){
        for (Technology tech:technologies){
            if (tech.codeDaysDone < tech.codeDaysNeeded)
                return false;
            if (tech.testDaysDone < tech.codeDaysNeeded)
                return false;
        }
        return true;
    }



    public Boolean isProgrammerAssigned(Employee programmer) {
        for(Employee employee:programmers)
            if (employee.equals(programmer)) return true;
        return false;
    }


    public Boolean hasTech(String name){
        for(Technology tech:technologies)
            if (tech.name.equals(name))
                return true;
        return false;
    }


    public Technology getTechWithName(String name){
        for(Technology technology:technologies)
            if (technology.name.equals(name))
                return technology;
        return null;
    }


    private String generateProjectName(){
        return Lang.projectNames[Tool.randInt(0, Lang.projectNames.length - 1)];
    }


    private List<Technology> generateTechnologies(){
        // generates random number of random but unique techs for project
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
        if (client.paymentDelayWeekChance > 0)
            if (Tool.randInt(1,100) <= client.paymentDelayWeekChance)
                delay += 7;

        if (client.paymentDelayMonthChance > 0)
            if (Tool.randInt(1,100) <= client.paymentDelayMonthChance)
                delay += 30;

        return delay;
    }


    private Boolean calculatePaymentNever(){
        // client will never pay for the project
        // this chance depends on client's trait and its calculation for each and every project separately
        if (client.paymentNeverChance > 0)
            return Tool.randInt(1, 100) <= client.paymentNeverChance;
        return false;
    }


    private Boolean calculatePenaltyAvoidedWithinWeekOfDelay(){
        // chance to avoid penalty if project will be delayed by no more than one week
        if (client.delayWeekPenaltyAvoidChance > 0)
            return Tool.randInt(1,100) <= client.delayWeekPenaltyAvoidChance;
        return false;
    }


    private Boolean calculateProblemFromNotWorkingProject(){
        // chance to avoid problems (e.g. canceling the contract) when you deliver unfinished project to client
        if (client.problemsFromNotWorkingProjectChance > 0)
            return Tool.randInt(1,100) <= client.problemsFromNotWorkingProjectChance;
        return false;
    }


    public Double calculatePaymentAdvance(){
        double advance = 0.0;
        if (technologies.size() >= Conf.PROJECT_PAYMENT_ADVANCE_FOR_AT_LEAST_TECHS && totalWorkDaysNeeded >= Conf.PROJECT_PAYMENT_ADVANCE_FOR_AT_LEAST_DAYS)
            advance = (getPrice() / 100) * Conf.PROJECT_PAYMENT_ADVANCE_PERCENT;
        return advance;
    }


    public Integer calculateTotalWorkDaysNeeded(){
        int numTechs = technologies.size();
        int days = 0;
        int maxDaysOfTech = 0;

        // for 1 tech: codeDaysNeeded * 2
        if (numTechs == 1){
            for (Technology tech:technologies)
                days += tech.codeDaysNeeded;
            days *= 2;
        }

        // for 2 techs and more:
        // sum of codeDaysNeeded for each tech
        // + random number between days of tech with max days and that sum
        if (numTechs > 1) {
            for (Technology tech:technologies) {
                days += tech.codeDaysNeeded;
                if (maxDaysOfTech < tech.codeDaysNeeded)
                    maxDaysOfTech = tech.codeDaysNeeded;
            }
            days += Tool.randInt(maxDaysOfTech, days);
        }

        return days;
    }


    public void negotiatePriceBonus(){
        // if seller found a project, the seller is able to negotiate a better price for it
        if (seller != null){
            int min = 0;
            int max = new BigDecimal(String.valueOf(seller.payForHourBonus)).intValue();
            int percent = Tool.randInt(min, max);
            int priceOfOnePercent = (int) (price / 100.0);
            priceBonus += (double) (priceOfOnePercent * percent);
        }
    }

}
