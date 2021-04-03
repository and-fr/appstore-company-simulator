package com.company.things;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Contractor;
import com.company.people.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Company {

    // FIELDS

    private Double money;
    private Boolean hasOffice;
    private final List<Project> projects = new ArrayList<>();
    private final List<Project> returnedProjects = new ArrayList<>();
    private final List<Person> employees = new ArrayList<>();
    private final List<Transaction> transactionsIn = new ArrayList<>();
    private final List<Transaction> transactionsOut = new ArrayList<>();


    // CONSTRUCTORS

    public Company(){
        money = Conf.START_MONEY;
        hasOffice = Conf.START_COMPANY_HAS_OFFICE;
    }


    // GETTERS

    public Double getMoney() { return money; }
    public Boolean getHasOffice() { return hasOffice; }
    public List<Project> getProjects(){ return projects; }
    public List<Person> getEmployees(){ return employees; }
    public List<Transaction> getTransactionsIn(){ return transactionsIn; }
    public List<Transaction> getTransactionsOut(){ return transactionsOut; }


    // SETTERS

    public void addMoney(double money) { this.money += money; }
    public void addProject(Project project){ projects.add(project); }
    public void removeProject(Project project){ projects.remove(project); }
    public void addToReturnedProjects(Project project){ returnedProjects.add(project); }
    public void addEmployee(Person employee){ employees.add(employee); }
    public void setHasOffice(Boolean hasOffice) { this.hasOffice = hasOffice; }
    public void addTransactionIn(Transaction transaction) { this.transactionsIn.add(transaction); }
    public void addTransactionOut(Transaction transaction) { this.transactionsOut.add(transaction); }


    // OTHER METHODS

    public void showAllProjects(){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Project prj:projects) {
            sb.append("\t").append(++count).append(".");
            sb.append(" | ").append(prj.getName()).append(" for ").append(prj.getClient().getName());
            sb.append(" | price: ").append(prj.getPrice()).append(" | deadline: ").append(prj.getDeadline());
            sb.append("\n\t\t techs: ");
            for (Technology tech : prj.getTechnologies()) {
                sb.append(tech.getName()).append(" (code ").append(tech.getCodeDaysDone()).append("/");
                sb.append(tech.getCodeDaysNeeded()).append(", tests: ").append(tech.getTestDaysDone()).append("/");
                sb.append(tech.getCodeDaysDone()).append((tech.isContractorAssigned() ? ", contractor" : "")).append("); ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }


    public Integer getContractorsCount(){
        int count = 0;
        for (Project prj:projects)
            for (Technology tech:prj.getTechnologies())
                if (tech.getContractor() != null) count++;
        return count;
    }


    public void processContractorsDailyWork(){
        for (Project prj:projects)
            for (Technology tech:prj.getTechnologies())
                if (tech.isContractorAssigned()){

                    // PROGRAMMING
                    if (tech.getCodeDaysDone() < tech.getCodeDaysNeeded()){
                        // for contractor who has a trait "not finishes on time"
                        // theres a chance that one of the days won't be productive and no code will be provided
                        if (tech.getContractor().isFinishOnTime() == false)
                            if (Tool.randInt(1,100) <= Conf.CONTRACTORS_CODE_DAY_FAILURE_CHANCE_PERCENT){
                                System.out.println("(INFO) Today, contractor " + tech.getContractor().getName() + " hasn't provided any CODE on "
                                        + tech.getName() + " technology for " + prj.getName() + " project.\n");
                                continue;
                            }

                        tech.setCodeDaysDonePlus(1);
                        tech.setContractorCodeDaysPlus(1);
                        System.out.println("(INFO) Contractor " + tech.getContractor().getName() + " worked with CODE on "
                            + tech.getName() + " technology for " + prj.getName() + " project.\n");
                        continue;
                    }

                    // TESTS
                    if (tech.getTestDaysDone() < tech.getCodeDaysDone()){

                        // if sum of contactor test days and failure days equals total of code days needed for the project
                        // then it means the contractor won't work on that tech anymore and waits for a payment for the work done even when not complete
                        if ((tech.getContractorTestDays() + tech.getContractorTestFailureDays()) >= tech.getCodeDaysNeeded()){
                            System.out.println("(INFO) Contractor " + tech.getContractor().getName() + " has finished work for " + tech.getName() + " technology for "
                                    + prj.getName() + " project. Though tests are not fully complete for it.\n");
                            tech.setIsContractorWorkFinished(true);
                            continue;
                        }

                        // if contractor has trait to not return working (tested) code in full
                        if (tech.getContractor().isNoErrors() == false){
                            if (Tool.randInt(1,100) <= Conf.CONTRACTORS_TEST_DAY_FAILURE_CHANCE_PERCENT){
                                tech.setContractorTestFailureDaysPlus(1);
                                System.out.println("(INFO) Today, contractor " + tech.getContractor().getName() + " hasn't provided any TESTS for "
                                        + tech.getName() + " technology for " + prj.getName() + " project.\n");
                                continue;
                            }
                        }

                        tech.setTestDaysDonePlus(1);
                        tech.setContractorTestDaysPlus(1);
                        System.out.println("(INFO) Contractor " + tech.getContractor().getName() + " worked with TESTS on "
                            + tech.getName() + " technology for " + prj.getName() + " project.\n");
                        continue;
                    }

                    // FINISHED CODE AND TESTS FOR TECH
                    System.out.println("(INFO) Work on the " + tech.getName() + " technology for " + prj.getName()
                            + " project is finished. Contractor " + tech.getContractor().getName() + " won't work on this any further.\n");
                    tech.setIsContractorWorkFinished(true);
                }
    }


    public void processContractorsFinishedWork(LocalDate currentDate, List<Contractor> contractors){
        for (Project prj:projects)
            for (Technology tech:prj.getTechnologies())
                if (tech.isContractorAssigned()){
                    if (tech.isContractorWorkFinished()) {
                        Contractor contractor = tech.getContractor();

                        // prepare payment for contractor
                        int workDays = tech.getContractorCodeDays() + tech.getContractorTestDays();
                        double payment = (double) workDays * 8.0 * contractor.getPayForHour();
                        String description = "Payment for " + contractor.getName() + ". Tech: " + tech.getName()
                                + ". Project: " + prj.getName() + ". Work days: " + workDays + ".";
                        transactionsOut.add(new Transaction(payment, currentDate.plusDays(Conf.CONTRACTORS_PAY_AFTER_DAYS), description));

                        // add contractor to available contractors global group
                        // and remove contractor from current tech
                        contractors.add(contractor);
                        tech.removeContractor();
                    }
                }
    }
}
