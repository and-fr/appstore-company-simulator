package com.company;

import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.assets.Tool;
import com.company.people.Contractor;
import com.company.things.Company;
import com.company.things.Project;
import com.company.things.Technology;
import com.company.people.Client;
import com.company.things.Transaction;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Game {

    // FIELDS

    private LocalDate currentDate;
    private final List<Client> clients = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<Contractor> contractors = new ArrayList<>();
    private final Company company = new Company();
    private Integer searchDaysForClients = 0;


    // CONSTRUCTORS

    public Game(){
        currentDate = Conf.START_DATE;
        generateClients();
        generateInitialProjects();
        generateContractors();
        System.out.println(Lang.GAME_WELCOME_MESSAGE);
    }


    // PUBLIC METHODS

    public void showSummary(){
        StringBuilder summary = new StringBuilder();
        summary.append("DAY ").append(getDayNumber()).append(" (").append(currentDate).append(" ").append(currentDate.getDayOfWeek()).append(") | ");
        summary.append("MONEY: ").append(company.getMoney()).append(" | ");
        summary.append("PROJECTS: ").append(company.getProjects().size()).append(" | ");
        summary.append("OFFICE: ").append(company.getHasOffice() ? "yes" : "no").append(" | ");
        summary.append("EMPLOYEES: ").append(company.getEmployees().size()).append(" | ");
        summary.append("CONTRACTORS: ").append(company.getContractorsCount()).append("\n");
        StringBuilder line = new StringBuilder().append("-".repeat(summary.length())).append("\n");
        System.out.println(line + String.valueOf(summary) + line);
    }


    public void showMainMenu(){
        System.out.print(Lang.MAIN_MENU);
    }


    private void addNewProject(){
        if (projects.size() >= Conf.MAX_AVAILABLE_PROJECTS)
            projects.remove(0);
        int clientNum = Tool.randInt(0, clients.size() - 1);
        projects.add(new Project(clients.get(clientNum)));
    }


    public void optionAvailableProjects(){

        if (company.getProjects().size() >= Conf.MAX_COMPANY_PROJECTS_AT_A_TIME){
            System.out.println(
                "(INFO) Your company can handle only " + Conf.MAX_COMPANY_PROJECTS_AT_A_TIME +
                " projects at a time. Finish some current projects first."
            );
            return;
        }

        StringBuilder menu = new StringBuilder("AVAILABLE PROJECTS:\n\n");
        int count = 0;

        if (projects.size() > 0) {
            for (Project pr : projects) {
                menu.append("\t").append(++count).append(". | ").append(pr.getName()).append(" from ").append(pr.getClient().getName()).append(" | price: ").append(pr.getPrice()).append(" | technologies: ");
                for (Technology tech : pr.getTechnologies())
                    menu.append(tech.getName()).append(" ");
                menu.append("| work days: ").append(pr.getTotalWorkDaysNeeded()).append("\n");
            }
        } else {
            menu.append("\tThere are no any projects available right now. Search for clients or hire a seller.\n");
        }

        menu.append("\n[Type the number of a project to work on (or 0 for none) then press Enter to continue]\n");
        System.out.print(menu);

        while(true){
            char ch = Tool.getKey();
            if (ch == '0') break;

            // if player selects a valid project then the project is added to company's projects
            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum > 0 && selectedProjectNum <= projects.size()){
                Project prj = projects.get(selectedProjectNum - 1);
                company.addProject(prj);
                prj.setStartDate(currentDate);
                projects.remove(selectedProjectNum - 1);
                advanceNextDay();
                break;
            }
        }
    }


    public void optionSearchForClients(){
        if (searchDaysForClients < 4){
            searchDaysForClients += 1;
            System.out.println("(INFO) Since a new project was available you've searched for client for days: " + searchDaysForClients + "\n");
        } else {
            searchDaysForClients = 0;
            addNewProject();
            System.out.println("(INFO) Your search has finally payed off. A new project is available.\n");
        }
        System.out.println();
        advanceNextDay();
    }


    public void optionProgramming(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.\n");
            return;
        }

        Project project = null;
        Technology technology = null;
        String workType = null;
        int selectedNum;
        char key;

        // MENU: PROJECT LEVEL
        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to CODE/TEST for. Or type 0 to cancel. Then press Enter.]");

        while (project == null){
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.getProjects().size())
                project = company.getProjects().get(selectedNum - 1);
        }

        // if all code and tests are completed there's no point to work on project anymore
        if (project.isFinished()){
            System.out.println("(INFO) " + project.getName() + " project has all code and tests already completed.\n");
            return;
        }

        // MENU: TECHNOLOGY LEVEL
        project.showAllTechnologies();
        System.out.println("[Type a number of a technology you want to work with. Or type 0 to cancel. Then Press Enter.]");

        while (technology == null) {
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= project.getTechnologies().size())
                technology = project.getTechnologies().get(selectedNum - 1);
        }

        // if all code and tests for technology are completed there's no point to work on it anymore
        if (technology.isFinished()){
            System.out.println("(INFO) " + technology.getName() + " technology for this project has all code and tests already completed.\n");
            return;
        }

        // check if tech is Mobile - player can't work on this, need to hire someone else
        if (technology.getName().equals("Mobile")){
            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.\n");
            return;
        }

        // MENU: WORK TYPE LEVEL
        System.out.println("On what type of work you want to focus on with " + technology.getName() + " technology?\n");
        System.out.println("\t1. Programming     2. Testing\n");
        System.out.println("[Type a number of your work choice. Or type 0 to cancel. Then Press Enter.]");
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
                System.out.println("(INFO) " + project.getName() + " project has code already completed.\n");
                return;
            }

            // check if tech is not assigned to contractor (player can't work on this)
            if (technology.isContractorAssigned()){
                System.out.println("(INFO) There's a contractor who works on this technology. You can't code it.\n");
                return;
            }

            // if player can actually work on tech
            technology.setCodeDaysDonePlus(1);

            // player has a chance of "lucky test day"
            // when the code is so good that free test day for that tech is received automatically
            technology.setLuckyTestDayForPlayer();

            System.out.println("(INFO) You spent one day on CODING for " + technology.getName() + " tech for " + project.getName() + " project.\n");
            advanceNextDay();
            return;
        }

        if (workType.equals("test")){

            // if no code, no tests
            if (technology.getCodeDaysDone() == 0){
                System.out.println("(INFO) No code for " + technology.getName() + " technology was written. There's nothing to test for.\n");
                return;
            }

            // if days of tests are equal to code days
            // this indicates all written code has already been tested
            if (technology.getTestDaysDone() >= technology.getCodeDaysDone()){
                System.out.println("(INFO) All code for " + technology.getName() + " technology has been already tested.\n");
                return;
            }

            technology.setTestDaysDonePlus(1);
            System.out.println("(INFO) You spent one day on TESTING the code for " + technology.getName() + " tech for " + project.getName() + " project.\n");
            advanceNextDay();
        }
    }


    public void optionReturnProject(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.\n");
            return;
        }

        Project project = null;
        char key;
        int selectedNum;

        // MENU: PROJECT SELECTION LEVEL
        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to RETURN to client. Or type 0 for cancel. Then press Enter.]");

        while (project == null) {
            key = Tool.getKey();
            if (key == '0') return;

            selectedNum = Character.getNumericValue(key);
            if (!(selectedNum >= 1 && selectedNum <= company.getProjects().size()))
                continue;

            project = company.getProjects().get(selectedNum - 1);
        }

        // MENU: CONFIRMATION LEVEL
        System.out.println("PROJECT'S DETAILS:\n");
        project.showProjectDetails(currentDate);
        System.out.println("\n[Do you really want to return this project to client?] [y/n]");
        do {
            key = Tool.getKey();
            if (key == 'n') return;
        } while (key != 'y');

        // HANDLE THINGS CAUSED BY RETURN OF THE PROJECT
        project.setReturnDate(currentDate);
        company.addToReturnedProjects(project);
        company.removeProject(project);

        // DEAL WITH CONTRACTORS (payment, release)

        for (Technology technology:project.getTechnologies())
            if (technology.isContractorAssigned()){
                // payment for contractor
                String description = "Payment for " + technology.getContractor().getName() + ". Tech: " + technology.getName()
                        + ". Project: " + project.getName() + ". Work days: " + technology.getContractorWorkDays() + ".";
                company.addTransactionOut(new Transaction(technology.getContractorCost(), currentDate.plusDays(Conf.CONTRACTORS_PAY_AFTER_DAYS), description));

                // add contractor to available contractors global group
                // and remove contractor from current tech
                contractors.add(technology.getContractor());
                technology.removeContractor();
            }


        // DEAL WITH CLIENTS
        boolean willPay = false;

        // PART 1/2: check whether client will pay

        // by default project needs to be completed at 75% or more
        // otherwise no client will ever pay for it, and the contract is lost
        if (project.getCompletionPercent() < Conf.PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD){
            System.out.println("(INFO) Unfortunately, the returned project has been completed in less than 75%. Client will not pay for it. This contract is lost.\n");
            advanceNextDay();
            return;
        }

        // if project is at decent state (75% or more) but not fully finished
        // payment will depend on the chance generated by the start of the project
        if (project.getCompletionPercent() >= Conf.PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD && project.getCompletionPercent() < 100) {
            if (project.isProblemFromNotWorkingProject()){
                System.out.println("(INFO) Unfortunately, the client decided to cancel the contract, because project was not completed. There will be no any payment.");
                advanceNextDay();
                return;
            } else
                willPay = true;
        }

        // project is fully completed
        if (project.getCompletionPercent() >= 100) {
            if (project.isPaymentNever()){
                // if clients has such trait as "will never pay" then this is an outcome for it
                // player is notified that the project is returned, but no payment (transaction) will be ever made
                System.out.println("(INFO) Project '" + project.getName() + "' has been returned to client.\n");
                advanceNextDay();
                return;
            } else
                willPay = true;
        }

        // PART 2/2: calculate clients payment (date, project's price - penalties + bonuses if any)

        if (willPay){

            double money = project.getPrice();
            LocalDate date = currentDate.plusDays(project.getPaymentDaysDue() + project.getPaymentDelayDays());
            String desc = "Payment for '" + project.getName() + "' project from " + project.getClient().getName() + ".";

            // money to pay can change if project is delayed
            int delayDays = project.getDaysOfDelay(currentDate);
            if (delayDays > 0) {
                if (delayDays <= 7)
                    money -= project.getPenaltyPrice();
                else
                    money -= project.getPenaltyPrice() * 2.0;
                System.out.println("(INFO) Project has been delayed by days: " + delayDays + ". The price client will pay will be lower, as penalty for the delay will be deducted from it.\n");
            }

            company.addTransactionIn(new Transaction(money, date, desc));
            System.out.println("(INFO) Project '" + project.getName() + "' has been returned to " + project.getClient().getName()
                    + ". Expected payment within " + project.getPaymentDaysDue() + " days: " + money + "\n");

            advanceNextDay();
        }
    }


    public void optionContractors(){

        // if company has no projects then contractors can't be hired
        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no projects for any contractor to work on.\n");
            return;
        }

        Contractor contractor = null;
        Project project = null;
        Technology technology = null;
        int selectedNum;
        char key;

        // MENU: CONTRACTORS LEVEL
        showContractors();
        System.out.println("[Type number of contractor you want to hire. Type 0 for cancel. Then Press Enter.]");
        while(contractor == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= contractors.size()) {
                contractor = contractors.get(selectedNum - 1);
            }
        }

        // MENU: PROJECTS LEVEL
        company.showAllProjects();
        System.out.println("[Type number of project you want contractor to work on. Type 0 for cancel. Then Press Enter.]");
        while(project == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.getProjects().size()) {
                project = company.getProjects().get(selectedNum - 1);
            }
        }

        // MENU: TECHNOLOGY LEVEL
        project.showAllTechnologies();
        System.out.println("[Type number of technology you want contractor to work on. Type 0 for cancel. Then Press Enter.]");
        while(technology == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= project.getTechnologies().size()) {
                technology = project.getTechnologies().get(selectedNum - 1);

                // can't hire contractor if tech was contracted previously
                if (technology.isContractorWorkFinished()){
                    System.out.println("(INFO) Contractor worked on this tech in the past. It can't be assigned to any contractor anymore.\n");
                    continue;
                }

                // check if other contractor works on tech
                if (technology.getContractor() != null){
                    System.out.println("(INFO) Technology " + technology.getName() + " has already contractor assigned.\n");
                    continue;
                }

                // check if contractor has required skill
                if (!contractor.getSkills().contains(technology.getName())){
                    System.out.println("(INFO) Contractor " + contractor.getName() + " doesn't know " + technology.getName() + " technology.\n");
                    continue;
                }

                technology.setContractor(contractor);
                contractors.remove(contractor);
                System.out.println("(INFO) Contractor " + contractor.getName() + " has agreed to work on "
                        + technology.getName() + " technology for " + project.getName() + " project.\n");

                advanceNextDay();
            }
        }
    }


    public void advanceNextDay(){

        // contractors and workers don't work during weekends
        if (currentDate.getDayOfWeek().getValue() < 6){
            company.processContractorsDailyWork();
        }

        company.processContractorsFinishedWork(currentDate, contractors);

        //System.out.println(company.getTransactionsOut().toString());

        currentDate = currentDate.plusDays(1);
        showSummary();
    }


    public void showContractors() {
        StringBuilder sb = new StringBuilder().append("CONTRACTORS:\n\n");
        int count = 0;
        for (Contractor con : contractors) {
            sb.append("\t").append(++count).append(". | ").append(con.getName());
            sb.append(" | pay for hour: ").append(con.getPayForHour());
            sb.append(" | on time: ").append(con.isFinishOnTime()).append(" | no errors: ").append(con.isNoErrors());
            sb.append("\n\t\t ").append("skills: ");
            for (String skill : con.getSkills()) {
                sb.append(skill).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }



    private void generateClients(){
        for (int i = 1; i <= Conf.CLIENTS_NUM; i++)
            clients.add(new Client());
    }


    private void generateInitialProjects(){
        for (int i = 1; i <= Conf.INITIAL_AVAILABLE_PROJECTS_NUM; i++)
            projects.add(new Project(clients.get(Tool.randInt(0, clients.size() - 1))));
    }


    private void generateContractors() {
        // Double payForHour, Boolean finishOnTime, Boolean noErrors
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_HIGH, true, true));
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_MID, true, false));
        contractors.add(new Contractor(Conf.PAY_FOR_HOUR_CONTRACTOR_LOW, false, false));
    }


    private Long getDayNumber() {
        return ChronoUnit.DAYS.between(Conf.START_DATE, currentDate) + 1;
    }

}
