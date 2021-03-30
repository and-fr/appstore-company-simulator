package com.company;

import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.assets.Tool;
import com.company.things.Company;
import com.company.things.Project;
import com.company.things.Technology;
import com.company.people.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Game {

    // FIELDS

    private LocalDate currentDate;
    private final List<Client> clients = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final Company company = new Company();
    private Integer searchDaysForClients = 0;


    // CONSTRUCTORS

    public Game(){
        currentDate = Conf.START_DATE;
        generateClients();
        generateInitialProjects();
        System.out.println(Lang.GAME_WELCOME_MESSAGE);
    }


    // PUBLIC METHODS

    public void showSummary(){
        StringBuilder summary = new StringBuilder();
        summary.append("DAY ").append(getDayNumber()).append(" (").append(currentDate.getDayOfWeek()).append(") | ");
        summary.append("MONEY: ").append(company.getMoney()).append(" | ");
        summary.append("PROJECTS: ").append(company.getProjects().size()).append(" | ");
        summary.append("OFFICE: ").append(company.getHasOffice() ? "yes" : "no").append(" | ");
        summary.append("EMPLOYEES: ").append(company.getEmployees().size()).append(" | ");
        summary.append("CONTRACTORS: ").append(company.getContractors().size()).append("\n");
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
                company.addProject(projects.get(selectedProjectNum - 1));
                projects.remove(selectedProjectNum - 1);
                advanceNextDay();
                break;
            }
        }
    }


    public void optionSearchForClients(){
        advanceNextDay();
        if (searchDaysForClients < 4){
            searchDaysForClients += 1;
            System.out.println("(INFO) Since a new project was available you've searched for client for days: " + searchDaysForClients);
            return;
        }
        searchDaysForClients = 0;
        addNewProject();
        System.out.println("(INFO) Your search has finally payed off. A new project is available.");
    }


    public void optionProgramming(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
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
                    System.out.println("(INFO) #" + selectedProjectNum + " project has code already completed.");
                    continue;
                }

                System.out.println("Chosen project: #" + selectedProjectNum + " " + prj.getName());
                System.out.print("Project's technologies:\n\t");
                int count = 0;
                for (Technology tech:prj.getTechnologies()) {
                    System.out.print(++count + ". " + tech.getName() + "   ");
                }
                System.out.println("\n");
                System.out.println("[Type a number of a technology to write code for. Type 0 to cancel. Then Press Enter.]");

                while(true){
                    ch = Tool.getKey();
                    if (ch == '0') return;

                    int selectedTechNum = Character.getNumericValue(ch);
                    if (selectedTechNum >= 1 && selectedTechNum <= prj.getTechnologies().size()){

                        Technology tech = prj.getTechnologies().get(selectedTechNum - 1);

                        // check if tech is Mobile - player can't work on this, need to hire someone else
                        if (tech.getName().equals("Mobile")){
                            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.");
                            continue;
                        }

                        // check if tech is already completed, no more coding needed
                        if (tech.getWorkDaysDone() >= tech.getWorkDaysNeeded()) {
                            System.out.println("(INFO) The code for this technology is already complete.");
                            continue;
                        }

                        tech.setWorkDaysDonePlus(1);
                        System.out.println("(INFO) You spent one day on CODING for " + tech.getName() + " tech for " + prj.getName() + " project.");
                        advanceNextDay();
                        return;
                    }
                }
            }
        }
    }


    public void optionTesting(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
            return;
        }

        System.out.println("COMPANY'S PROJECTS:\n");
        company.showAllProjects();
        System.out.println("[Type a number of a project you would like to TEST a code for. Type 0 for cancel. Then press Enter.]");

        Project prj;
        while (true){
            char ch = Tool.getKey();
            if (ch == '0') return;

            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum >= 1 && selectedProjectNum <= company.getProjects().size()){
                prj = company.getProjects().get(selectedProjectNum - 1);

                if (prj.isTestCompleted()){
                    System.out.println("(INFO) #" + selectedProjectNum + " project has tests already completed for all its written code.");
                    continue;
                }

                System.out.println("Chosen project: #" + selectedProjectNum + " " + prj.getName());
                System.out.print("Project's technologies:\n\t");
                int count = 0;
                for (Technology tech:prj.getTechnologies()) {
                    System.out.print(++count + ". " + tech.getName() + "   ");
                }
                System.out.println("\n");
                System.out.println("[Type a number of a technology to TEST code for. Type 0 to cancel. Then Press Enter.]");

                while(true){
                    ch = Tool.getKey();
                    if (ch == '0') return;

                    int selectedTechNum = Character.getNumericValue(ch);
                    if (selectedTechNum >= 1 && selectedTechNum <= prj.getTechnologies().size()){

                        Technology tech = prj.getTechnologies().get(selectedTechNum - 1);

                        // check if tech is Mobile - player can't work on this, need to hire someone else
                        if (tech.getName().equals("Mobile")){
                            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.");
                            continue;
                        }

                        // check if tests are equal to number of work days
                        if (tech.getTestDaysDone() >= tech.getWorkDaysDone()) {
                            System.out.println("(INFO) The code for this technology has been already tested.");
                            continue;
                        }

                        tech.setTestDaysDonePlus(1);
                        System.out.println("(INFO) You spent one day on TESTING the code for " + tech.getName() + " tech for " + prj.getName() + " project.");
                        advanceNextDay();
                        return;
                    }
                }
            }
        }
    }


    public void optionReturnProject(){

        if (company.getProjects().size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
            return;
        }

    }


    public void advanceNextDay(){
        currentDate = currentDate.plusDays(1);
    }


    // PRIVATE METHODS

    private void generateClients(){
        for (int i = 1; i <= Conf.CLIENTS_NUM; i++)
            clients.add(new Client());
    }


    private void generateInitialProjects(){
        for (int i = 1; i <= Conf.INITIAL_AVAILABLE_PROJECTS_NUM; i++)
            projects.add(new Project(clients.get(Tool.randInt(0, clients.size() - 1))));
    }


    private Long getDayNumber() {
        return ChronoUnit.DAYS.between(Conf.START_DATE, currentDate) + 1;
    }

}
