package com.company;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Contractor;
import com.company.people.Employee;
import com.company.things.Company;
import com.company.things.Project;
import com.company.things.Technology;
import com.company.people.Client;
import com.company.things.Transaction;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Game {

    public LocalDate currentDate = Conf.START_DATE;
    private final List<Client> clients = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<Contractor> contractors = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();
    public final Company company = new Company();
    private Integer searchDaysForClients = 0;
    private Integer searchDaysForEmployees = 0;
    private Boolean isPaymentTime = true;
    public Console console = new Console();


    public Game(){
        generateInitialClients();
        generateInitialProjects();
        generateInitialContractors();
        generateInitialEmployees();
        console.welcomeMessage();
    }


    private void addNewProject(){
        if (projects.size() >= Conf.MAX_AVAILABLE_PROJECTS)
            projects.remove(0);
        int clientNum = Tool.randInt(0, clients.size() - 1);
        projects.add(new Project(clients.get(clientNum)));
    }


    public void optionAvailableProjects(){

        if (company.projects.size() >= Conf.MAX_COMPANY_PROJECTS_AT_A_TIME){
            console.info(
                "Your company can handle only " + Conf.MAX_COMPANY_PROJECTS_AT_A_TIME +
                " projects at a time. Finish some current projects first."
            );
            return;
        }

        if (projects.size() == 0){
            console.info("There are no any projects available right now. Search for clients or hire a seller.");
            return;
        }

        console.menuProjectsAvailable(projects);

        int selectedProjectNum;
        char key;
        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedProjectNum = Character.getNumericValue(key);
        } while (selectedProjectNum <= 0 || selectedProjectNum > projects.size());

        // if player selects a valid project then the project is added to company's projects
        Project prj = projects.get(selectedProjectNum - 1);
        company.projects.add(prj);
        projects.remove(selectedProjectNum - 1);
        prj.startDate = currentDate;
        if (prj.paymentAdvance > 0.0){
            company.transactionsIn.add(new Transaction(
                    prj.paymentAdvance,
                    currentDate.plusDays(Conf.PROJECT_PAYMENT_ADVANCE_AFTER_DAYS),
                    "Payment advance, '" + prj.name + "' project from " + prj.client.getName()
            ));
        }
        console.info("You agreed to work on " + prj.name + " project.");
        advanceNextDay();
    }


    public void optionSearchForClients(){
        if (searchDaysForClients < 4){
            searchDaysForClients += 1;
            console.info("Since a new project was available you've searched for client for days: " + searchDaysForClients);
        } else {
            searchDaysForClients = 0;
            addNewProject();
            console.info("Your search has finally payed off. A new project is available.");
        }
        advanceNextDay();
    }


    public void optionProgramming(){

        if (company.projects.size() == 0){
            console.info("Your company has no any projects.");
            return;
        }

        Project project = null;
        Technology technology = null;
        String workType = null;
        int selectedNum;
        char key;

        // MENU: PROJECT LEVEL
        console.menuProjectsCurrent(company.projects);
        console.actionMessage("Type a number of a project you would like to CODE/TEST for");

        while (project == null){
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.projects.size())
                project = company.projects.get(selectedNum - 1);
        }

        // if all code and tests are completed there's no point to work on project anymore
        if (project.isFinished()){
            console.info(project.name + " project has all code and tests already completed.");
            return;
        }

        // MENU: TECHNOLOGY LEVEL
        console.projectTechnologies(project);
        console.actionMessage("Type a number of a technology you want to work with");

        while (technology == null) {
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= project.technologies.size())
                technology = project.technologies.get(selectedNum - 1);
        }

        // if all code and tests for technology are completed there's no point to work on it anymore
        if (technology.isFinished()){
            console.info(technology.name + " technology for this project has all code and tests already completed.");
            return;
        }

        // check if tech is Mobile - player can't work on this, need to hire someone else
        if (technology.name.equals("Mobile")){
            console.info("You can't work on Mobile technology. You need to get a contractor or hire a programmer.");
            return;
        }

        // MENU: WORK TYPE LEVEL
        console.menuWorkType(technology);
        while (workType == null){
            switch (Tool.getKey()){
                case '0': return;
                case '1': workType = "code"; break;
                case '2': workType = "test"; break;
            }
        }

        if (workType.equals("code")){

            // if code is completed there's no point to work on it anymore
            if (technology.isCodeCompleted()){
                console.info(project.name + " project has code for " + technology.name + " technology already completed.");
                return;
            }

            // check if tech is not assigned to contractor (player can't work on this)
            if (technology.isContractorAssigned()){
                console.info("There's a contractor who works on this technology. You can't code it.");
                return;
            }

            // if player can actually work on tech
            technology.codeDaysDone += 1;
            console.info("You spent one day on CODING for " + technology.name + " tech for " + project.name + " project.");

            // player has a chance of "lucky test day"
            // when the code is so good that free test day for that tech is received automatically
            console.info(technology.setLuckyTestDayForPlayer());

            // mark project as touched by the player
            // (this project won't be counted towards winning scenario)
            project.isPlayerInvolved = true;

            advanceNextDay();
            return;
        }

        if (workType.equals("test")){

            // if no code - no tests
            if (technology.codeDaysDone == 0){
                console.info("No code for " + technology.name + " technology was written. There's nothing to test for.");
                return;
            }

            // if days of tests are equal to code days
            // this indicates all written code has already been tested
            if (technology.testDaysDone >= technology.codeDaysDone){
                console.info("All code for " + technology.name + " technology has been already tested.");
                return;
            }

            technology.testDaysDone += 1;

            // mark project as touched by the player
            // (this project won't be counted towards winning scenario)
            project.isPlayerInvolved = true;

            console.info("You spent one day on TESTING the code for " + technology.name + " tech for " + project.name + " project.");
            advanceNextDay();
        }
    }


    public void optionReturnProject(){

        if (company.projects.size() == 0){
            console.info("Your company has no any projects.");
            return;
        }

        Project project = null;
        char key;
        int selectedNum;

        // MENU: PROJECT SELECTION LEVEL
        console.menuProjectsCurrent(company.projects);
        console.actionMessage("Type a number of a project you would like to RETURN to client");

        while (project == null) {
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (!(selectedNum >= 1 && selectedNum <= company.projects.size()))
                continue;

            project = company.projects.get(selectedNum - 1);
        }

        // MENU: CONFIRMATION LEVEL
        console.menuProjectReturnConfirmation(project, currentDate);
        do {
            key = Tool.getKey();
            if (key == 'n') return;
        } while (key != 'y');

        // HANDLE THINGS CAUSED BY RETURN OF THE PROJECT
        company.returnedProjects.add(project);
        company.projects.remove(project);

        // DEAL WITH CONTRACTORS (payment, release)

        for (Technology technology:project.technologies)
            if (technology.isContractorAssigned()){
                // payment for contractor
                String description = "Payment for " + technology.contractor.getName() + ". Tech: " + technology.name
                        + ". Project: " + project.name + ". Work days: " + technology.getContractorWorkDays() + ".";
                company.transactionsOut.add(new Transaction(technology.getContractorCost(), currentDate.plusDays(Conf.CONTRACTORS_PAY_AFTER_DAYS), description));

                // add contractor to available contractors global group
                // and remove contractor from current tech
                contractors.add(technology.contractor);
                technology.contractor = null;
            }


        // DEAL WITH CLIENTS
        boolean willPay = false;

        // PART 1/2: check whether client will pay

        // by default project needs to be completed at 75% or more
        // otherwise no client will ever pay for it, and the contract is lost
        if (project.getCompletionPercent() < Conf.PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD){
            console.info("Unfortunately, the returned project has been completed in less than 75%. Client will not pay for it. This contract is lost.");
            returnPaymentAdvanceForCancelledProject(project, currentDate);
            advanceNextDay();
            return;
        }

        // if project is at decent state (75% or more) but not fully finished
        // payment will depend on the chance generated by the start of the project
        if (project.getCompletionPercent() >= Conf.PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD && project.getCompletionPercent() < 100) {
            if (project.isProblemFromNotWorkingProject){
                console.info("Unfortunately, the client decided to cancel the contract, because project was not completed. There will be no any payment.");
                returnPaymentAdvanceForCancelledProject(project, currentDate);
                advanceNextDay();
                return;
            } else
                willPay = true;
        }

        // project is fully completed
        if (project.getCompletionPercent() >= 100) {
            if (project.isPaymentNever){
                // if clients has such trait as "will never pay" then this is an outcome for it
                // player is notified that the project is returned, but no payment (transaction) will be ever made
                console.info("Project '" + project.name + "' has been returned to client.");
                advanceNextDay();
                return;
            } else
                willPay = true;
        }

        // PART 2/2: calculate clients payment (date, project's price - penalties + bonuses if any)

        if (willPay){

            double money = project.getPrice();
            LocalDate date = currentDate.plusDays(project.getPaymentDaysDue() + project.paymentDelayDays);
            String desc = "Payment for '" + project.name + "' project from " + project.client.getName() + ".";

            // money to pay can change if project is delayed
            int delayDays = project.getDaysOfDelay(currentDate);
            if (delayDays > 0) {
                if (delayDays <= 7)
                    if (!project.isPenaltyAvoidedWithinWeekOfDelay)
                        money -= project.getPenaltyPrice();
                else
                    money -= project.getPenaltyPrice() * 2.0;
                console.info("Project has been delayed by days: " + delayDays + ". The price client will pay will be lower, as penalty for the delay will be deducted from it.");
            }

            // if payment advance for the project was payed
            // that payment advance is deducted from final project's payment
            if (project.paymentAdvance > 0.0)
                money -= project.paymentAdvance;
            project.transaction = new Transaction(money, date, desc);
            console.info("Project '" + project.name + "' has been returned to " + project.client.getName()
                    + ". Expected payment within " + project.getPaymentDaysDue() + " days: " + money);

            advanceNextDay();
        }
    }


    private void returnPaymentAdvanceForCancelledProject(Project project, LocalDate currentDate){
        // if payment advance for the project was payed
        // that payment advance is deducted from final project's payment
        if (project.paymentAdvance > 0.0){
            company.transactionsOut.add(new Transaction(
                    project.paymentAdvance,
                    currentDate.plusDays(Conf.PROJECT_PAYMENT_ADVANCE_AFTER_DAYS),
                    "Returned payment advance for cancelled '" + project.name + "' project"
            ));
        }
    }


    public void optionContractors(){

        // if company has no projects then contractors can't be hired
        if (company.projects.size() == 0){
            console.info("Your company has no projects for any contractor to work on.");
            return;
        }

        Contractor contractor = null;
        Project project = null;
        Technology technology = null;
        int selectedNum;
        char key;

        // MENU: CONTRACTORS LEVEL
        console.contractorsForHire(contractors);
        console.actionMessage("Type number of contractor you want to hire");
        while(contractor == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= contractors.size()) {
                contractor = contractors.get(selectedNum - 1);
            }
        }

        // MENU: PROJECTS LEVEL
        console.menuProjectsCurrent(company.projects);
        console.actionMessage("Type number of project you want contractor to work on");
        while(project == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.projects.size()) {
                project = company.projects.get(selectedNum - 1);
            }
        }

        // MENU: TECHNOLOGY LEVEL
        console.projectTechnologies(project);
        console.actionMessage("Type number of technology you want contractor to work on");
        while(technology == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= project.technologies.size()) {
                technology = project.technologies.get(selectedNum - 1);

                // can't hire contractor if tech was contracted previously
                if (technology.isContractorWorkFinished){
                    console.info("Contractor worked on this tech in the past. It can't be assigned to any contractor anymore.");
                    continue;
                }

                // check if other contractor works on tech
                if (technology.contractor != null){
                    console.info("Technology " + technology.name + " has already contractor assigned.");
                    continue;
                }

                // check if contractor has required skill
                if (!contractor.skills.contains(technology.name)){
                    console.info("Contractor " + contractor.getName() + " doesn't know " + technology.name + " technology.");
                    continue;
                }

                technology.contractor = contractor;
                contractors.remove(contractor);
                console.info("Contractor " + contractor.getName() + " has agreed to work on "
                        + technology.name + " technology for " + project.name + " project.");

                advanceNextDay();
            }
        }
    }


    public void optionEmployees(){
        char key;

        // MENU: MAIN EMPLOYEES LEVEL
        console.menuEmployees();
        int selectedNum;
        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
        } while (selectedNum < 1 || selectedNum > 6);

        switch(selectedNum){
            case 1 : optionEmployeesView(); break;
            case 2 : optionEmployeesHire(); break;
            case 3 : optionEmployeesFire(); break;
            case 4 : optionEmployeesSearch(); break;
            case 5 : optionEmployeesAssignTester(); break;
            case 6 : optionEmployeesAssignProgrammer(); break;
        }
    }


    private void optionEmployeesView(){
        if (company.employees.size() < 1)
            console.info("Your company has no employees.");
        else
            console.employeesStatus(company);
    }


    private void optionEmployeesHire() {

        if (!company.hasOffice) {
            console.info("Your company has no office. You can't hire people. Rent some office space first.");
            return;
        }

        if (company.employees.size() >= Conf.MAX_COMPANY_EMPLOYEES_AT_A_TIME) {
            console.info("Your company can't have more than " + Conf.MAX_COMPANY_EMPLOYEES_AT_A_TIME
                    + " employees. Fire one of your current employees if you want to hire a new person.");
            return;
        }

        if (employees.size() == 0) {
            console.info("There are no any people for hire right now. You need to search for employees.");
            return;
        }

        char key;
        int selectedNum;
        Employee employee;

        console.employeesForHire(employees);
        console.actionMessage("Type a number of person to hire.");

        while(true) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= employees.size()) {
                employee = employees.get(selectedNum - 1);
                break;
            }
        }

        // check for required amount of money as hiring process is costly
        if (company.money < Conf.EMPLOYEE_HIRE_COST){
            console.info("Your company has not enough of money to hire an employee.");
            return;
        }
        company.money -= Conf.EMPLOYEE_HIRE_COST;

        employee.hireDate = currentDate;
        company.employees.add(employee);
        employees.remove(employee);
        console.info("You hired a new " + employee.role.toLowerCase()
                + ", " + employee.getName() + ".");

        advanceNextDay();
    }


    private void optionEmployeesFire(){

        if (company.employees.size() < 1){
            console.info("Your company has no employees.");
            return;
        }

        char key;
        int selectedNum;
        Employee selectedEmployee;

        console.employeesForFire(company.employees);
        console.actionMessage("Type an employee number you want to fire.");

        while (true){
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.employees.size()) {
                selectedEmployee = company.employees.get(selectedNum - 1);
                break;
            }
        }

        // employee firing process
        if (selectedEmployee.isProgrammer())
            company.removeProgrammerFromAnyProjects(selectedEmployee);
        if (selectedEmployee.isTester())
            company.removeTesterFromAnyProjects(selectedEmployee);

        company.processEmployeeCurrentMonthPayment(selectedEmployee, currentDate);
        company.processEmployeesCosts();
        company.employees.remove(selectedEmployee);
        console.info("You fired a " + selectedEmployee.role.toLowerCase()
                + ", " + selectedEmployee.getName() + ".");
        advanceNextDay();
    }

    public void optionEmployeesSearch(){

        if (company.money < Conf.EMPLOYEE_SEARCH_COST){
            console.info("Your company has no money to search for an employee.");
            return;
        }

        searchDaysForEmployees += 1;
        company.money -= Conf.EMPLOYEE_SEARCH_COST;

        if (searchDaysForEmployees % 5 == 0) {
            addEmployee(new Employee());
            searchDaysForEmployees = 0;
            console.info("Your search for employees was successful. A potential candidate has been found. Check employee for hire option.");
        } else {
            console.info("You spent a day on employee search. No success so far.");
        }

        advanceNextDay();
    }


    public void optionEmployeesAssignTester(){

        if (company.getTesters().size() == 0){
            console.info("Your company has no any testers hired.");
            return;
        }

        if (company.projects.size() == 0){
            console.info("Your company has no any projects.");
            return;
        }

        // MENU: TESTERS CHOICE LEVEL
        console.testers(company);
        console.actionMessage("Type a number of a tester you want to choose");

        char key;
        int selectedNum;
        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
        } while (selectedNum < 1 || selectedNum > company.getTesters().size());

        Employee tester = company.getTesters().get(selectedNum - 1);

        // MENU: PROJECT SELECTION LEVEL
        console.menuProjectsCurrent(company.projects);
        console.actionMessage("Select a number of a project you want the tester to be assigned to");

        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
        } while (selectedNum < 1 || selectedNum > company.projects.size());

        Project project = company.projects.get(selectedNum - 1);

        if (project.tester != null)
            if (project.tester.equals(tester)){
                console.info("Tester, " + tester.getName() + " is already assigned to this project.");
                return;
            }

        company.removeTesterFromAnyProjects(tester);
        project.tester = tester;
        console.info("Tester, " + tester.getName() + " has been assigned to work on " + project.name + " project.");
        advanceNextDay();
    }


    public void optionEmployeesAssignProgrammer(){

        if (company.getProgrammers().size() == 0){
            console.info("Your company has no any programmers hired.");
            return;
        }

        if (company.projects.size() == 0){
            console.info("Your company has no any projects.");
            return;
        }

        // MENU: PROGRAMMERS CHOICE LEVEL
        console.programmers(company);
        console.actionMessage("Type the number of the programmer you want to choose");

        char key;
        int selectedNum;
        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
        } while (selectedNum < 1 || selectedNum > company.getProgrammers().size());

        Employee programmer = company.getProgrammers().get(selectedNum - 1);

        // MENU: PROJECT SELECTION LEVEL
        console.menuProjectsCurrent(company.projects);
        console.actionMessage("Select a number of a project you want programmer to focus on");

        do {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
        } while (selectedNum < 1 || selectedNum > company.projects.size());

        Project project = company.projects.get(selectedNum - 1);

        // PROCESS CHOICES

        if (project.isProgrammerAssigned(programmer)) {
            console.info("Programmer " + programmer.getName() + " already works on "
                + project.name + " project.");
            return;
        }

        company.removeProgrammerFromAnyProjects(programmer);
        project.addProgrammer(programmer);
        console.info("Programmer, " + programmer.getName() + " has been assigned to work on "
            + project.name + " project.");
        advanceNextDay();
    }


    public void optionCompanyTasks(){
        console.menuTasks(company);

        while(true){
            switch(Tool.getKey()){
                case '0': return;
                case '1': optionCompanyTaskApprovePayments(); return;
                case '2': console.menuPaymentsApproved(company); return;
                case '3': console.menuIncome(company.getTransactionsInPayed()); return;
                case '4':
                    if (!company.hasOffice) {
                        optionCompanyTaskRentOffice();
                        advanceNextDay();
                        return;
                    }
            }
        }
    }


    private void optionCompanyTaskApprovePayments(){

        if (company.getUnapprovedTransactions().size() == 0){
            console.info("There are no any unapproved transactions today.");
            return;
        }

        console.menuPaymentsUnapproved(company);

        List<Integer> transactionNumbersToApprove = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        switch (input){
            case "0": return;

            // approve all transactions
            case "all":
                for(Transaction tr:company.getUnapprovedTransactions())
                    tr.isApproved = true;
                console.info("You approved all pending transactions.");
                advanceNextDay();
                return;

            // process selected transactions
            default:
                String[] inputValues = input.split(",");
                int num;
                for (String inputValue : inputValues) {
                    num = Integer.parseInt(inputValue);
                    if (num > 0 && num <= company.getUnapprovedTransactions().size())
                        transactionNumbersToApprove.add(num);
                }

        }

        if (transactionNumbersToApprove.size() == 0){
            console.info("No valid numbers of transactions have been provided.");
            return;
        }

        // approve selected transactions
        console.menuSelectedTransactionsApproval(transactionNumbersToApprove);

        while (true){
            switch (Tool.getKey()){
                case 'n': return;
                case 'y':
                    console.menuSelectedTransactionsConfirmation(company, transactionNumbersToApprove);
                    advanceNextDay();
                    return;
            }
        }
    }


    private void optionCompanyTaskRentOffice(){
        company.officeRentMonthlyPayDayNumber = currentDate.getDayOfMonth();
        company.hasOffice = true;
        console.info("You rented office for your company.");
    }


    public void optionCompanyReports(){
        console.companyReports(this);
    }


    private void addEmployee(Employee employee){
        if (employees.size() >= Conf.MAX_AVAILABLE_EMPLOYEES) employees.remove(0);
        employees.add(employee);
    }


    private void generateInitialClients(){
        for (int i = 1; i <= Conf.CLIENTS_NUM; i++)
            clients.add(new Client());
    }


    private void generateInitialProjects(){
        for (int i = 1; i <= Conf.INITIAL_AVAILABLE_PROJECTS_NUM; i++)
            projects.add(new Project(clients.get(Tool.randInt(0, clients.size() - 1))));
    }


    private void generateInitialContractors() {
        // Double payForHour, Boolean finishOnTime, Boolean noErrors
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_HIGH, true, true));
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_MID, true, false));
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_LOW, false, false));
    }


    private void generateInitialEmployees(){
        for (int i = 1; i <= Conf.INITIAL_AVAILABLE_EMPLOYEES_NUM; i++)
            employees.add(new Employee());
    }


    public void advanceNextDay(){

        // PEOPLE WORK

        // set sickness state for employees
        for(Employee employee:company.employees)
            employee.setSickness();

        // contractors and employees don't work during weekends
        if (currentDate.getDayOfWeek().getValue() < 6){
            console.info(company.processSellersDailyWork(projects));
            console.info(company.processTestersDailyWork());
            console.info(company.processContractorsDailyWork());
            console.info(company.processProgrammersDailyWork());
        }


        // PAYMENTS

        company.processContractorsFinishedWork(currentDate, contractors);

        // on a first work day of each month are processed:
        // - taxes from income in past month
        // - employees' payments for past month
        // - employee costs (taxes, insurances, work place costs, etc.)
        if (isPaymentTime && currentDate.getDayOfMonth() <= 3 && currentDate.getDayOfWeek().getValue() < 6){
            company.processTaxes(currentDate);
            company.processEmployeesPayments(currentDate);
            company.processEmployeesCosts();
            isPaymentTime = false;
        }
        if (currentDate.getDayOfMonth() == 4)
            isPaymentTime = true;

        // rent for office
        if (currentDate.getDayOfMonth() == company.getOfficeRentMonthlyPayDayNumber())
            company.transactionsOut.add(new Transaction(
                    Conf.OFFICE_MONTHLY_COST,
                    currentDate.plusDays(Conf.OFFICE_RENT_PAY_AFTER_DAYS),
                    "Office rent " + currentDate.getYear() + "/" + currentDate.getMonthValue()
            ));


        // TRANSACTIONS

        company.processReturnedProjectsPayments();

        // transactions IN (company's income)
        console.info(company.processTransactionsIn(currentDate));
        // transactions OUT (company's costs)
        console.info(company.processTransactionsOut(currentDate));


        // HANDLING UNPAID EMPLOYEES
        List<Transaction> unpaidSalaries = company.getUnpaidSalaries(currentDate);
        if (currentDate.getDayOfMonth() == 25 && unpaidSalaries.size() > 0)
            for(Transaction tr:unpaidSalaries){
                company.processEmployeeCurrentMonthPayment(tr.employee, currentDate);
                company.processEmployeesCosts();
                company.employees.remove(tr.employee);
                console.info(
                        tr.employee.role + ", " + tr.employee.getName() +
                            " has not received salary for previous month and decided to leave the company."
                );
            }

        currentDate = currentDate.plusDays(1);
        console.summary(this);

        checkWinningScenario();
    }


    private void checkWinningScenario(){

        // LOOSING CONDITIONS

        // game over if money below 0
        if (company.money < 0.0){
            console.info("YOU LOST! Your company has no money to function properly.");
            console.gameOver();
            System.exit(0);
        }

        // if player hasn't approved any costs from previous month
        // and a date of each of those payments is in the past
        // then tax collectors are angry and the company is shut
        if (currentDate.getDayOfMonth() == 10)
            if (company.countUnpaidCostsPastMonth(currentDate) > 0) {
                console.info("YOU LOST! Your company hasn't payed some costs for the past month. Tax collectors and other officials are enraged. Your company was shut.");
                console.gameOver();
                System.exit(0);
            }


        // WINNING CONDITIONS

        if (company.countWinningProjects() >= Conf.VALID_PROJECTS_TO_WIN_COUNT)
            if (company.money > Conf.START_MONEY){
                console.info("YOU WON! Your company completed and got paid for three big projects, and has more money than at the beginning. Good work!");
                console.gameOver();
                System.exit(0);
            }
    }
}
