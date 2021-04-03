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

        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to CODE for. Or type 0 for exit. Then press Enter.]");

        Project prj;
        while (true){
            char ch = Tool.getKey();
            if (ch == '0') return;

            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum >= 1 && selectedProjectNum <= company.getProjects().size()){
                prj = company.getProjects().get(selectedProjectNum - 1);

                if (prj.isCodeCompleted()){
                    System.out.println("(INFO) #" + selectedProjectNum + " project has code already completed.\n");
                    continue;
                }

                prj.showAllTechnologies();
                System.out.println("[Type a number of a technology to write code for. Type 0 to cancel. Then Press Enter.]");

                while(true){
                    ch = Tool.getKey();
                    if (ch == '0') return;

                    int selectedTechNum = Character.getNumericValue(ch);
                    if (selectedTechNum >= 1 && selectedTechNum <= prj.getTechnologies().size()){

                        Technology tech = prj.getTechnologies().get(selectedTechNum - 1);

                        // check if tech is Mobile - player can't work on this, need to hire someone else
                        if (tech.getName().equals("Mobile")){
                            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.\n");
                            continue;
                        }

                        // check if tech is already completed, no more coding needed
                        if (tech.getCodeDaysDone() >= tech.getCodeDaysNeeded()) {
                            System.out.println("(INFO) The code for this technology is already complete.\n");
                            continue;
                        }

                        // check if tech is not assigned to contractor (player can't work on this)
                        if (tech.isContractorAssigned()){
                            System.out.println("(INFO) There's a contractor who works on this technology. You can't code it.\n");
                            continue;
                        }

                        tech.setCodeDaysDonePlus(1);

                        // player has a chance of "lucky test day"
                        // when the code is so good that free test day for that tech is received automatically
                        tech.setLuckyTestDayForPlayer();

                        System.out.println("(INFO) You spent one day on CODING for " + tech.getName() + " tech for " + prj.getName() + " project.\n");
                        advanceNextDay();
                        return;
                    }
                }
            }
        }
    }


    public void optionTesting(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.\n");
            return;
        }

        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to TEST a code for. Or type 0 for cancel. Then press Enter.]");

        Project prj;
        while (true){
            char ch = Tool.getKey();
            if (ch == '0') return;

            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum >= 1 && selectedProjectNum <= company.getProjects().size()){
                prj = company.getProjects().get(selectedProjectNum - 1);

                if (prj.isTestCompleted()){
                    System.out.println("(INFO) #" + selectedProjectNum + " project has tests already completed for all its written code.\n");
                    continue;
                }

                prj.showAllTechnologies();
                System.out.println("[Type a number of a technology to TEST code for. Or type 0 to cancel. Then Press Enter.]");

                while(true){
                    ch = Tool.getKey();
                    if (ch == '0') return;

                    int selectedTechNum = Character.getNumericValue(ch);
                    if (selectedTechNum >= 1 && selectedTechNum <= prj.getTechnologies().size()){

                        Technology tech = prj.getTechnologies().get(selectedTechNum - 1);

                        // check if tech is Mobile - player can't work on this, need to hire someone else
                        if (tech.getName().equals("Mobile")){
                            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.\n");
                            continue;
                        }

                        // check if tests are equal to number of work days
                        if (tech.getTestDaysDone() >= tech.getCodeDaysDone()) {
                            System.out.println("(INFO) The code for this technology has been already tested.\n");
                            continue;
                        }

                        tech.setTestDaysDonePlus(1);
                        System.out.println("(INFO) You spent one day on TESTING the code for " + tech.getName() + " tech for " + prj.getName() + " project.\n");
                        advanceNextDay();
                        return;
                    }
                }
            }
        }
    }


    public void optionReturnProject(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.\n");
            return;
        }

        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to RETURN to client. Or type 0 for cancel. Then press Enter.]");

        Project prj;
        while (true) {
            char ch = Tool.getKey();
            if (ch == '0') return;

            int selectedProjectNum = Character.getNumericValue(ch);
            if ((selectedProjectNum >= 1 && selectedProjectNum <= company.getProjects().size()) == false)
                continue;

            prj = company.getProjects().get(selectedProjectNum - 1);

            StringBuilder sb = new StringBuilder("PROJECT'S DETAILS:\n");
            prj.showProjectDetails(currentDate);

            System.out.println("\n[Do you really want to return this project to client?] [y/n]");
            while (true) {
                ch = Tool.getKey();
                if (ch == 'n') return;

                if (ch == 'y'){
                    prj.setReturnDate(currentDate);
                    company.addToReturnedProjects(prj);
                    company.removeProject(prj);

                    // deal with contractors (payment, release)

                    // check whether client will pay, if yes then calculate the date of payment
                    if ((prj.getCodeCompletionPercent() > Conf.PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD)
                        ){

                    }

                    // calculate clients payment (project's price - penalties + bonuses if any)


                    advanceNextDay();
                    return;
                }
            }
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
        int selectedNum = 0;
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
                company.showAllProjects();
                System.out.println("[Type number of project you want contractor to work on. Type 0 for cancel. Then Press Enter.]");
            }
        }

        // MENU: PROJECTS LEVEL
        while(contractor != null && project == null) {
            key = Tool.getKey();
            if (key == '0') return;
            selectedNum = Character.getNumericValue(key);
            if (selectedNum >= 1 && selectedNum <= company.getProjects().size()) {
                project = company.getProjects().get(selectedNum - 1);
                project.showAllTechnologies();
                System.out.println("[Type number of technology you want contractor to work on. Type 0 for cancel. Then Press Enter.]");
            }
        }

        // MENU: TECHNOLOGY LEVEL
        while(project != null && technology == null) {
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
                if (contractor.getSkills().contains(technology.getName()) == false){
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
        company.processContractorsDailyWork();
        company.processContractorsFinishedWork(currentDate, contractors);

        System.out.println(company.getTransactionsOut().toString());

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
