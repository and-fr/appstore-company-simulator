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

    private Double money;
    private Boolean hasOffice;
    private Integer officeRentMonthlyPayDayNumber;
    private final List<Project> projects;
    private final List<Project> returnedProjects;
    private final List<Employee> employees;
    private final List<Transaction> transactionsIn;
    private final List<Transaction> transactionsOut;


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


    public Double getMoney() { return money; }
    public Boolean hasOffice() { return hasOffice; }
    public List<Project> getProjects(){ return projects; }
    public List<Employee> getEmployees(){ return employees; }


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
            if (transaction.isApproved() && !transaction.isPayed()) transactions.add(transaction);
        return transactions;
    }


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


    public String processContractorsDailyWork(){

        StringBuilder info = new StringBuilder();

        for (Project prj:projects)
            for (Technology tech:prj.getTechnologies())
                if (tech.isContractorAssigned()){

                    // PROGRAMMING
                    if (tech.getCodeDaysDone() < tech.getCodeDaysNeeded()){
                        // for contractor who has a trait "not finishes on time"
                        // theres a chance that one of the days won't be productive and no code will be provided
                        if (!tech.getContractor().isFinishOnTime())
                            if (Tool.randInt(1,100) <= Conf.CONTRACTORS_CODE_DAY_FAILURE_CHANCE_PERCENT){
                                info.append("Today, contractor ").append(tech.getContractor().getName());
                                info.append(" hasn't provided any CODE on ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project.\n");
                                continue;
                            }

                        tech.setCodeDaysDonePlus(1);
                        tech.setContractorCodeDaysPlus(1);
                        info.append("Contractor ").append(tech.getContractor().getName()).append(" worked with CODE on ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project.\n");
                        continue;
                    }

                    // TESTS
                    if (tech.getTestDaysDone() < tech.getCodeDaysDone()){

                        // if sum of contactor test days and failure days equals total of code days needed for the project
                        // then it means the contractor won't work on that tech anymore and waits for a payment for the work done even when not complete
                        if ((tech.getContractorTestDays() + tech.getContractorTestFailureDays()) >= tech.getCodeDaysNeeded()){
                            info.append("Contractor ").append(tech.getContractor().getName());
                            info.append(" has finished work for ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project. Though tests are not fully complete for it.\n");
                            tech.setIsContractorWorkFinished(true);
                            continue;
                        }

                        // if contractor has trait to not return working (tested) code in full
                        if (!tech.getContractor().isNoErrors()){
                            if (Tool.randInt(1,100) <= Conf.CONTRACTORS_TEST_DAY_FAILURE_CHANCE_PERCENT){
                                tech.setContractorTestFailureDaysPlus(1);
                                info.append("Today, contractor ").append(tech.getContractor().getName()).append(" hasn't provided any TESTS for ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project.\n");
                                continue;
                            }
                        }

                        tech.setTestDaysDonePlus(1);
                        tech.setContractorTestDaysPlus(1);
                        info.append("Contractor ").append(tech.getContractor().getName()).append(" worked with TESTS on ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project.\n");
                        continue;
                    }

                    // FINISHED CODE AND TESTS FOR TECH
                    info.append("Work on the ").append(tech.getName()).append(" technology for ").append(prj.getName()).append(" project is finished. Contractor ").append(tech.getContractor().getName()).append(" won't work on this any further.\n");
                    tech.setIsContractorWorkFinished(true);
                }

        return info.toString();
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


    public String processSellersDailyWork(List<Project> projects){

        StringBuilder info = new StringBuilder();

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
                if (projects.size() >= Conf.MAX_AVAILABLE_PROJECTS)
                    projects.remove(0);
                projects.add(project);
                info.append("Seller, ").append(seller.getName()).append(" has negotiated a new potential project. Check 'New projects' option.");
            }
        }

        return info.toString();
    }


    public String processTestersDailyWork(){

        StringBuilder info = new StringBuilder();

        for (Project project:getProjectsWithTesters()){

            // if project is completed no tests are needed
            if (project.isFinished()) {
                project.removeTester();
                info.append("Project ").append(project.getName()).append(" is completed. Tester has been unassigned from it.");
                break;
            }

            // sick testers don't work
            if (project.getTester().isSick())
                break;

            for (Technology technology:project.getTechnologies()){
                if (technology.getTestDaysDone() < technology.getCodeDaysDone()){
                    technology.setTestDaysDonePlus(1);
                    info.append("Tester, ").append(project.getTester().getName()).append(" has worked on ").append(technology.getName()).append(" technology for ").append(project.getName()).append(" project.\n");
                } else
                    continue;

                // testers have a chance to work on additional tests for other technologies at the same day
                if (Tool.randInt(1,100) > Conf.TESTER_ADDITIONAL_TESTS_CHANCE)
                    break;
            }
        }

        return info.toString();
    }


    public String processProgrammersDailyWork(){

        StringBuilder info = new StringBuilder();

        Technology technology;

        for (Project project:getProjectsWithProgrammers())
            for(Employee programmer:project.getProgrammers()) {

                // if project is completed no coding or tests are needed
                if (project.isFinished()) {
                    project.removeAllProgrammers();
                    info.append("Project ").append(project.getName()).append(" is completed. All programmers have been unassigned from it.");
                    return info.toString();
                }

                // sick programmers don't work
                if (programmer.isSick())
                    continue;

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
                                info.append("Programmer, ").append(programmer.getName()).append(" has not provided any code or tests today.\n");
                                break;
                            }

                        if (technology.getCodeDaysDone() < technology.getCodeDaysNeeded()) {
                            technology.setCodeDaysDonePlus(1);
                            info.append("Programmer, ").append(programmer.getName()).append(" has CODED ").append(technology.getName()).append(" technology for ").append(project.getName()).append(" project.\n");
                            break;
                        }

                        if (technology.getTestDaysDone() < technology.getCodeDaysDone()) {
                            technology.setTestDaysDonePlus(1);
                            info.append("Programmer, ").append(programmer.getName()).append(" has TESTED ").append(technology.getName()).append(" technology for ").append(project.getName()).append(" project.\n");
                            break;
                        }
                    }
                }
            }

        return info.toString();
    }


    public void processEmployeesPayments(LocalDate currentDate){
        // payment is always for past month

        int employeeWorkDays;
        double salary;
        int salaryYear = currentDate.minusMonths(1).getYear();
        int salaryMonth = currentDate.minusMonths(1).getMonthValue();
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

            Transaction transaction = new Transaction(salary, currentDate.plusDays(5), desc);
            transaction.setAsSalary();
            transaction.setEmployee(employee);
            transactionsOut.add(transaction);
        }
    }


    public void processEmployeeCurrentMonthPayment(Employee employee, LocalDate currentDate){
        // calculate the number of days which need to be compensated
        int days = currentDate.getDayOfMonth();
        if (employee.getHireDate().getYear() == currentDate.getYear() && employee.getHireDate().getMonthValue() == currentDate.getMonthValue())
            days = currentDate.getDayOfMonth() - employee.getHireDate().getDayOfMonth();

        Transaction tr = new Transaction(
                days * 8.0 * employee.getPayForHour(),
                currentDate.plusDays(7),
                "Salary " + currentDate.getYear() + "/" + currentDate.getMonthValue() + " " + employee.getName() + ", " + employee.getEmployeeRole()
        );
        tr.setAsSalary();
        tr.setEmployee(employee);
        transactionsOut.add(tr);
    }


    public void processEmployeesCosts(){
        List<Transaction> trCosts = new ArrayList<>();

        for(Transaction tr:transactionsOut)
            if(tr.isSalary() && !tr.isCostGenerated()){
                trCosts.add(new Transaction(
                        (tr.getMoney() / 100.0) * Conf.EMPLOYEE_WORK_COST_PERCENT,
                        tr.getProcessDate(),
                        tr.getDescription() + " (COSTS)"
                ));
                tr.setAsCostGenerated();
            }

        for(Transaction trc:trCosts){
            trc.setAsMandatoryCost();
            transactionsOut.add(trc);
        }
    }


    public Integer countUnpaidCostsPastMonth(LocalDate currentDate){
        int count = 0;
        for(Transaction tr:transactionsOut)
            if (tr.isMandatoryCost() && !tr.isApproved() && tr.getProcessDate().getMonthValue() < currentDate.getMonthValue())
                count++;
        return count;
    }


    public String processTransactionsOut(LocalDate currentDate){

        StringBuilder info = new StringBuilder();

        for(Transaction tr:transactionsOut)
            if (!tr.isPayed() && tr.isApproved())
                if (tr.getProcessDate().equals(currentDate) || tr.getProcessDate().isBefore(currentDate)){
                    money -= tr.getMoney();
                    tr.setAsPayed();
                    info.append("Transaction OUT: ").append(tr.getDescription()).append(" (").append(tr.getMoney()).append(")\n");
                }

        return info.toString();
    }


    public String processTransactionsIn(LocalDate currentDate){

        StringBuilder info = new StringBuilder();

        for(Transaction tr:transactionsIn)
            if (!tr.isPayed())
                if (tr.getProcessDate().equals(currentDate) || tr.getProcessDate().isBefore(currentDate)){
                    money += tr.getMoney();
                    tr.setAsPayed();
                    info.append("Transaction IN: ").append(tr.getDescription()).append(" (").append(tr.getMoney()).append(")\n");
                }

        return info.toString();
    }


    public List<Transaction> getTransactionsInPayed(){
        List<Transaction> transactions = new ArrayList<>();
        for(Transaction tr:transactionsIn)
            if (tr.isPayed()) transactions.add(tr);
        return transactions;
    }


    public List<Transaction> getUnpaidSalaries(LocalDate currentDate){
        // returns employee salaries which haven't been approved last month
        // and haven't been payed, thus those employees will be leaving the company
        List<Transaction> unpaidSalaries = new ArrayList<>();
        for(Transaction tr:transactionsOut){
            if (!tr.isSalary()) continue;
            if (tr.isApproved()) continue;
            if (tr.getProcessDate().getYear() == currentDate.minusMonths(1).getYear() && tr.getProcessDate().getMonthValue() == currentDate.minusMonths(1).getMonthValue())
                unpaidSalaries.add(tr);
        }
        return unpaidSalaries;
    }


    public void processTaxes(LocalDate currentDate){
        LocalDate previousMonth = currentDate.minusMonths(1);
        double taxes;
        double incomePreviousMonth = 0.0;
        for(Transaction tr:getTransactionsInPayed())
            if (tr.getProcessDate().getYear() == previousMonth.getYear() && tr.getProcessDate().getMonthValue() == previousMonth.getMonthValue())
                incomePreviousMonth += tr.getMoney();
        taxes = incomePreviousMonth / Conf.TAX_FROM_INCOME_MONTHLY_PERCENT;

        if (taxes > 0.0){
            Transaction tr = new Transaction(
                    taxes,
                    currentDate,
                    "Taxes for " + previousMonth.getYear() + "/" + previousMonth.getMonthValue()
            );
            tr.setAsMandatoryCost();
            transactionsOut.add(tr);
        }
    }


    public void processReturnedProjectsPayments(){
        for(Project project:returnedProjects){

            if (project.getTransaction() == null) continue;
            if (project.getTransaction().isPayed()) continue;
            if (transactionsIn.contains(project.getTransaction())) continue;

            transactionsIn.add(project.getTransaction());
        }
    }


    public List<Project> getValidComplexProjectsPayed(){
        // valid means projects that meet winning conditions
        // where player was not involved in coding or testing
        List<Project> projects = new ArrayList<>();
        for(Project project:returnedProjects) {
            if (project.getTechnologies().size() < Conf.VALID_PROJECTS_TO_WIN_MIN_TECHS) continue;
            if (project.isPlayerInvolved()) continue;
            if (project.getTransaction() == null) continue;
            if (project.getTransaction().isPayed())
                projects.add(project);
        }
        return projects;
    }


    public List<Project> getValidComplexProjectsWithSeller(){
        // for player to win the game
        // there must be at least one valid complex project negotiated by a seller
        List<Project> projects = new ArrayList<>();
        for(Project project:getValidComplexProjectsPayed()) {
            if (project.getTransaction() == null) continue;
            if (project.getTransaction().isPayed())
                projects.add(project);
        }
        return projects;
    }


    public Integer countWinningProjects(){

        // winning projects is a group of projects
        // where player has not been involved in a coding or testing
        // and there must by at least one project negotiated by a seller
        // if there are many payed valid complex projects
        // but none of those was negotiated by a seller
        // then this method should return 0
        // otherwise it should return a count of those valid projects

        return (getValidComplexProjectsWithSeller().size() > 0)
                ? getValidComplexProjectsPayed().size()
                : 0;
    }
}
