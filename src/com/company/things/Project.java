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

    // FIELDS

    private final Client client;
    private final String name;
    private final List<Technology> technologies;
    private final Integer paymentDelayDays;
    private final Boolean isPaymentNever;
    private final Boolean isPenaltyAvoidedWithinWeekOfDelay;
    private final Boolean isProblemFromNotWorkingProject;
    private Boolean isPlayerInvolved;
    private LocalDate startDate;
    private LocalDate returnDate;
    private final Integer totalWorkDaysNeeded;
    private Employee seller;
    private Employee tester;
    private List<Employee> programmers;
    private Double price;
    private Double priceBonus;
    private Transaction transaction;


    // CONSTRUCTORS

    public Project(Client client){
        this.client = client;
        name = generateProjectName();
        technologies = generateTechnologies();
        totalWorkDaysNeeded = calculateTotalWorkDaysNeeded();
        paymentDelayDays = calculatePaymentDelayDays();
        isPaymentNever = calculatePaymentNever();
        isPenaltyAvoidedWithinWeekOfDelay = calculatePenaltyAvoidedWithinWeekOfDelay();
        isProblemFromNotWorkingProject = calculateProblemFromNotWorkingProject();
        seller = null;
        tester = null;
        programmers = new ArrayList<>();
        isPlayerInvolved = false;
        price = generatePrice();
        priceBonus = 0.0;
        transaction = null;
    }


    // GETTERS

    public Client getClient() { return client; }
    public Employee getSeller() { return seller; }
    public Employee getTester() { return tester; }
    public List<Employee> getProgrammers() { return programmers; }
    public String getName() { return name; }
    public List<Technology> getTechnologies(){ return technologies; }
    public Integer getPaymentDelayDays() { return paymentDelayDays; }
    public Boolean isPaymentNever() { return isPaymentNever; }
    public Boolean isPenaltyAvoidedWithinWeekOfDelay() { return isPenaltyAvoidedWithinWeekOfDelay; }
    public Boolean isProblemFromNotWorkingProject() { return isProblemFromNotWorkingProject; }
    public LocalDate getStartDate() { return startDate; }
    public Integer getTotalWorkDaysNeeded() { return totalWorkDaysNeeded; }
    public Boolean isNegotiatedBySeller() { return seller != null; }
    public Boolean isPlayerInvolved() { return isPlayerInvolved; }
    public Transaction getTransaction() { return transaction; }

    public LocalDate getDeadline() {
        // deadline date is calculated from start date
        // by adding totalWorkDaysNeeded for the project
        return startDate.plusDays((long)totalWorkDaysNeeded);
    }


    public Double getPrice() { return price + priceBonus; }
    public Double getPriceBonus() { return priceBonus; }


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
            days += tech.getCodeDaysNeeded();
        return days;
    }


    public Integer getCodeDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.getCodeDaysDone();
        return days;
    }


    public Integer getTestDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.getTestDaysDone();
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


    public Integer getTotalWorkDaysDone(){
        int days = 0;
        for (Technology tech:technologies)
            days += tech.getCodeDaysDone();
        return days;
    }


    public Boolean isCodeCompleted(){
        for (Technology tech:technologies)
            if (tech.getCodeDaysDone() < tech.getCodeDaysNeeded())
                return false;
        return true;
    }


    public Boolean isTestCompleted(){
        for (Technology tech:technologies)
            if (tech.getTestDaysDone() < tech.getCodeDaysDone())
                return false;
        return true;
    }


    public Boolean isFinished(){
        for (Technology tech:technologies){
            if (tech.getCodeDaysDone() < tech.getCodeDaysNeeded())
                return false;
            if (tech.getTestDaysDone() < tech.getCodeDaysNeeded())
                return false;
        }
        return true;
    }


    public Boolean hasMobileTech(){
        for (Technology tech:technologies)
            if (tech.getName().equals("Mobile"))
                return true;
        return false;
    }


    public Boolean isTesterAssigned() {
        return tester != null;
    }

    public Boolean isProgrammerAssigned(Employee programmer) {
        for(Employee employee:programmers)
            if (employee.equals(programmer)) return true;
        return false;
    }


    public Integer getCodeCompletionPercent(){
        return (int) (((double)getCodeDaysDone() / (double)getCodeDaysNeeded()) * 100.0);
    }


    public Integer getTestCompletionPercent(){
        return (int) (((double)getTestDaysDone() / (double)getCodeDaysNeeded()) * 100.0);
    }


    public Integer getCompletionPercent(){
        return (getCodeCompletionPercent() + getTestCompletionPercent()) / 2;
    }


    public Integer getDaysOfDelay(LocalDate gameDate) {
        return (int) ChronoUnit.DAYS.between(getDeadline(), gameDate);
    }


    public Boolean hasTech(String name){
        for(Technology tech:technologies)
            if (tech.getName().equals(name))
                return true;
        return false;
    }


    public Technology getTechWithName(String name){
        for(Technology technology:technologies)
            if (technology.getName().equals(name))
                return technology;
        return null;
    }


    // SETTERS

    public void addProgrammer(Employee programmer) { programmers.add(programmer); }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setSeller(Employee seller) { this.seller = seller; }
    public void setTester(Employee tester) { this.tester = tester; }
    public void setPlayerAsInvolved() { isPlayerInvolved = true; }
    public void removeTester() { tester = null; }
    public void removeProgrammer(Employee programmer) { programmers.remove(programmer); }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }


    // OTHER METHODS

    private String generateProjectName(){
        int index = Tool.randInt(0, Lang.projectNames.length - 1);
        return Lang.projectNames[index];
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
        if (client.getPaymentDelayWeekChance() > 0)
            if (Tool.randInt(1,100) <= client.getPaymentDelayWeekChance())
                delay += 7;

        if (client.getPaymentDelayMonthChance() > 0)
            if (Tool.randInt(1,100) <= client.getPaymentDelayMonthChance())
                delay += 30;

        return delay;
    }


    private Boolean calculatePaymentNever(){
        // client will never pay for the project
        // this chance depends on client's trait and its calculation for each and every project separately
        if (client.getPaymentNeverChance() > 0)
            return Tool.randInt(1, 100) <= client.getPaymentNeverChance();
        return false;
    }


    private Boolean calculatePenaltyAvoidedWithinWeekOfDelay(){
        // chance to avoid penalty if project will be delayed by no more than one week
        if (client.getDelayWeekPenaltyAvoidChance() > 0)
            return Tool.randInt(1,100) <= client.getDelayWeekPenaltyAvoidChance();
        return false;
    }


    private Boolean calculateProblemFromNotWorkingProject(){
        // chance to avoid problems (e.g. canceling the contract) when you deliver unfinished project to client
        if (client.getProblemsFromNotWorkingProjectChance() > 0)
            return Tool.randInt(1,100) <= client.getProblemsFromNotWorkingProjectChance();
        return false;
    }


    public Integer calculateTotalWorkDaysNeeded(){
        int numTechs = technologies.size();
        int days = 0;
        int maxDaysOfTech = 0;

        // for 1 tech: codeDaysNeeded * 2
        if (numTechs == 1){
            for (Technology tech:technologies)
                days += tech.getCodeDaysNeeded();
            days *= 2;
        }

        // for 2 techs and more:
        // sum of codeDaysNeeded for each tech
        // + random number between days of tech with max days and that sum
        if (numTechs > 1) {
            for (Technology tech:technologies) {
                days += tech.getCodeDaysNeeded();
                if (maxDaysOfTech < tech.getCodeDaysNeeded())
                    maxDaysOfTech = tech.getCodeDaysNeeded();
            }
            days += Tool.randInt(maxDaysOfTech, days);
        }

        return days;
    }


    public void negotiatePriceBonus(){
        // if seller found a project, the seller is able to negotiate a better price for it
        if (seller != null){
            int min = 0;
            int max = new BigDecimal(String.valueOf(seller.getPayForHourBonus())).intValue();
            int percent = Tool.randInt(min, max);
            int priceOfOnePercent = (int) (price / 100.0);
            priceBonus += (double) (priceOfOnePercent * percent);
        }
    }

}
