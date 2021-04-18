package com.company.things;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Client;
import com.company.people.Contractor;
import com.company.people.Employee;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Company {

    // FIELDS

    private Double money;
    private Boolean hasOffice;
    private Integer officeRentMonthlyPayDayNumber;
    private final List<Project> projects;
    private final List<Project> returnedProjects;
    private final List<Employee> employees;
    private final List<Transaction> transactionsIn;
    private final List<Transaction> transactionsOut;


    // CONSTRUCTORS

    public Company(){
        money = Conf.START_MONEY;
        hasOffice = Conf.START_COMPANY_HAS_OFFICE;
        officeRentMonthlyPayDayNumber = 0;
        projects = new ArrayList<>();
        returnedProjects = new ArrayList<>();
        employees = new ArrayList<>();
        transactionsIn = new ArrayList<>();
        transactionsOut = new ArrayList<>();
    }


    // GETTERS

    public Double getMoney() { return money; }
    public Boolean hasOffice() { return hasOffice; }
    public List<Project> getProjects(){ return projects; }
    public List<Employee> getEmployees(){ return employees; }
    public List<Transaction> getTransactionsIn(){ return transactionsIn; }
    public List<Transaction> getTransactionsOut(){ return transactionsOut; }

    public Integer getOfficeRentMonthlyPayDayNumber() {
        // to simplify calculations in the code:
        // if payDay was set to 29,30,31 the value of 28 is returned
        // this avoids situations where transaction won't be generated for e.g. 30th of february, etc.
        return Math.min(officeRentMonthlyPayDayNumber, 28);
    }

    public List<Employee> getTesters(){
        List<Employee> testers = new ArrayList<>();
        for (Employee employee:employees)
            if (employee.isTester()) testers.add(employee);
        return testers;
    }

    public List<Employee> getSellers(){
        List<Employee> sellers = new ArrayList<>();
        for (Employee employee:employees)
            if (employee.isSeller()) sellers.add(employee);
        return sellers;
    }


    public List<Employee> getProgrammers(){
        List<Employee> programmers = new ArrayList<>();
        for (Employee employee:employees)
            if (employee.isProgrammer()) programmers.add(employee);
        return programmers;
    }


    private List<Project> getProjectsWithTesters(){
        List<Project> projects = new ArrayList<>();
        for (Project project:this.projects)
            if (project.isTesterAssigned())
                projects.add(project);
        return projects;
    }


    private List<Project> getProjectsWithProgrammers(){
        List<Project> projects = new ArrayList<>();
        for (Project project:this.projects)
            if (project.getProgrammers().size() > 0)
                projects.add(project);
        return projects;
    }


    public Project getProjectTesterIsAssignedTo(Employee tester){
        for(Project project:projects)
            if (project.getTester() != null)
                if (project.getTester().equals(tester)) return project;
        return null;
    }


    public Project getProjectProgrammerIsAssignedTo(Employee programmer){
        for(Project project:projects)
            if (project.getProgrammers().size() > 0)
                for(Employee employee:project.getProgrammers())
                    if (employee.equals(programmer)) return project;
        return null;
    }


    public List<Transaction> getUnapprovedTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        for(Transaction transaction:transactionsOut)
            if (!transaction.isApproved()) transactions.add(transaction);
        return transactions;
    }


    public List<Transaction> getApprovedTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        for(Transaction transaction:transactionsOut)
            if (transaction.isApproved()) transactions.add(transaction);
        return transactions;
    }


    // SETTERS

    public void addMoney(double money) { this.money += money; }
    public void addProject(Project project){ projects.add(project); }
    public void removeProject(Project project){ projects.remove(project); }
    public void addToReturnedProjects(Project project){ returnedProjects.add(project); }
    public void addEmployee(Employee employee){ employees.add(employee); }
    public void setHasOffice(Boolean hasOffice) { this.hasOffice = hasOffice; }
    public void addTransactionIn(Transaction transaction) { this.transactionsIn.add(transaction); }
    public void addTransactionOut(Transaction transaction) { this.transactionsOut.add(transaction); }
    public void removeMoney(double money) { this.money -= money; }
    public void removeEmployee(Employee employee) { employees.remove(employee); }
    public void setOfficeRentMonthlyPayDayNumber(int day) { officeRentMonthlyPayDayNumber = day; }


    // OTHER METHODS

    public void removeTesterFromAnyProjects(Employee tester){
        for(Project project:projects)
            if (project.getTester() != null)
                if (project.getTester().equals(tester))
                    project.removeTester();
    }


    public void removeProgrammerFromAnyProjects(Employee programmer){
        for(Project project:projects)
            for(Employee employee:project.getProgrammers())
                if (employee.equals(programmer))
                    project.removeProgrammer(programmer);
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
                        if (!tech.getContractor().isFinishOnTime())
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
                        if (!tech.getContractor().isNoErrors()){
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
                        String description = "Payment for " + contractor.getName() + ". Tech: " + tech.getName()
                                + ". Project: " + prj.getName() + ". Work days: " + tech.getContractorWorkDays() + ".";
                        transactionsOut.add(new Transaction(tech.getContractorCost(), currentDate.plusDays(Conf.CONTRACTORS_PAY_AFTER_DAYS), description));

                        // add contractor to available contractors global group
                        // and remove contractor from current tech
                        contractors.add(contractor);
                        tech.removeContractor();
                    }
                }
    }


    public void processSellersDailyWork(List<Project> projects, List<Client> clients){
        for (Employee seller:getSellers()){

            // sick sellers don't work
            if (seller.isSick())
                break;

            seller.setSearchDaysForClientsPlus(1);
            if (seller.getSearchDaysForClients() >= 5){
                seller.resetSearchDays();
                Project project = new Project(new Client());
                project.setSeller(seller);
                project.negotiatePriceBonus();
                projects.add(project);
                System.out.println("(INFO) Seller, " + seller.getName() + " has negotiated a new potential project. Check 'New projects' option.\n");
            }
        }
    }


    public void processTestersDailyWork(){
        for (Project project:getProjectsWithTesters()){

            // sick testers don't work
            if (project.getTester().isSick())
                break;

            for (Technology technology:project.getTechnologies()){
                if (technology.getTestDaysDone() < technology.getCodeDaysDone()){
                    technology.setTestDaysDonePlus(1);
                    System.out.println("(INFO) Tester, " + project.getTester().getName() +
                            " has worked on " + technology.getName() + " technology for " + project.getName() + " project.\n");
                }
                // testers have a chance to work on additional tests for other technologies at the same day
                if (Tool.randInt(1,100) > Conf.TESTER_ADDITIONAL_TESTS_CHANCE)
                    break;
            }
        }
    }


    public void processProgrammersDailyWork(){
        Technology technology;

        for (Project project:getProjectsWithProgrammers())
            for(Employee programmer:project.getProgrammers()) {

                // sick programmers don't work
                if (programmer.isSick())
                    break;

                for (String skill : programmer.getSkills()) {
                    if (project.hasTech(skill)) {
                        technology = project.getTechWithName(skill);
                        if (technology == null) break;

                        // if tech is worked by a contractor then programmers won't work on it
                        if (technology.isContractorAssigned())
                            break;

                        // there is a chance programmer won't work at all this day
                        if (programmer.getSkipDayPercentChance() > 0)
                            if (Tool.randInt(1, 100) <= programmer.getSkipDayPercentChance()) {
                                System.out.println("(INFO) Programmer, " + programmer.getName() + " has not provided any code or tests today.\n");
                                break;
                            }

                        if (technology.getCodeDaysDone() < technology.getCodeDaysNeeded()) {
                            technology.setCodeDaysDonePlus(1);
                            System.out.println("(INFO) Programmer, " + programmer.getName()
                                    + " has CODED " + technology.getName() + " technology for "
                                    + project.getName() + " project.\n");
                            break;
                        }

                        if (technology.getTestDaysDone() < technology.getCodeDaysDone()) {
                            technology.setTestDaysDonePlus(1);
                            System.out.println("(INFO) Programmer, " + programmer.getName()
                                    + " has TESTED " + technology.getName() + " technology for "
                                    + project.getName() + " project.\n");
                            break;
                        }
                    }
                }
            }
    }


    public void processEmployeesPayments(LocalDate currentDate){
        // payment is always for past month

        int employeeWorkDays;
        double salary;
        int salaryYear = currentDate.plusMonths(-1).getYear();
        int salaryMonth = currentDate.plusMonths(-1).getMonthValue();
        String desc;

        for(Employee employee:employees){

            // for employees that were hired in current month payments are not processed
            if (currentDate.getMonthValue() == employee.getHireDate().getMonthValue())
                break;

            // employee who was hired earlier than 20 days has full monthly salary
            // otherwise salary is calculated: pay4hour * 8.0 * number of days
            employeeWorkDays = (int) ChronoUnit.DAYS.between(employee.getHireDate(), currentDate) + 1;

            if (employeeWorkDays > 20)
                salary = employee.getMonthlySalary();
            else
                salary = employee.getPayForHour() * 8.0 * (double) employeeWorkDays;

            desc = "Salary " + salaryYear + "/" + salaryMonth + " for " + employee.getName() + ", " + employee.getEmployeeRole();

            transactionsOut.add(new Transaction(salary, currentDate.plusDays(5), desc));
        }
    }


}
