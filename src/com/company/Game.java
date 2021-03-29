package com.company;


import com.company.people.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Game {

    final String GAME_WELCOME_MESSAGE = """
        # IT SOFTWARE COMPANY SIMULATOR
        #
        #\tYou start with some amount of money. You have no office, no employees, no clients.
        #\tYou know how to program in Java, have some knowledge of databases, and you can do some front-end work.
        #\tYou can also configure a webpage using Wordpress, and implement e-commerce solution using Prestashop.
        #\tYou can't create mobile apps. For that you need to make a deal with a contractor, hire an employee, or avoid such projects.
        """;

    final LocalDate START_DATE = LocalDate.of(2020, 1, 1);
    LocalDate currentDate = START_DATE;

    List<Client> clients = new ArrayList<>();
    List<Project> projects = new ArrayList<>();

    Company company = new Company();

    Integer searchDaysForClients = 0;


    public Game(){
        showWelcomeMessage();
        generateClients();
        generateInitialProjects();
    }


    void showWelcomeMessage(){
        System.out.println(GAME_WELCOME_MESSAGE);
    }


    public Long getDayNumber() { return ChronoUnit.DAYS.between(START_DATE, currentDate) + 1; }


    public void showSummary(){
        String hasOffice = company.getHasOffice() ? "yes" : "no";

        String summary = "DAY " + getDayNumber() + " (" + currentDate.getDayOfWeek() + ") |  MONEY: " + company.getMoney() +
                " | PROJECTS: " + company.projects.size() + " | OFFICE: " + hasOffice + " | EMPLOYEES: " + company.getEmployeesCount() +
                " | CONTRACTORS: " + company.getContractorsCount();

        StringBuilder line = new StringBuilder();
        line.append("-".repeat(summary.length()));

        System.out.println();
        System.out.println(line);
        System.out.println(summary);
        System.out.println(line);
        System.out.println();
    }


    private void generateClients(){
        for (int i = 0; i < 10; i++)
            clients.add(new Client());
    }


    private void generateInitialProjects(){
        int clientNum = randInt(0, clients.size() - 1);

        for (int i = 0; i < 3; i++)
            projects.add(new Project(clients.get(clientNum)));
    }



    private void addNewProject(){
        if (projects.size() >= 9)
            projects.remove(0);

        int clientNum = randInt(0, clients.size() - 1);
        projects.add(new Project(clients.get(clientNum)));
    }


    public void showMainMenu(){
        System.out.println("Choose today's action:\n");
        System.out.println("""
                \t1. Take a new project             6. Hire/Fire an employee
                \t2. Search for clients             7. Hire a contractor
                \t3. Programming                    8. Company tasks
                \t4. Testing                        9. Next day
                \t5. Return project to client       0. Exit
                """);
        System.out.println("[Type a number and press Enter]");
    }


    public void optionAvailableProjects(){

        if (company.projects.size() >= 9){
            System.out.println("(INFO) Your company can handle only 9 projects at a time. Finish some current projects first.");
            return;
        }

        StringBuilder menu = new StringBuilder("AVAILABLE PROJECTS:\n\n");
        int count = 0;

        if (projects.size() > 0) {
            for (Project pr : projects) {
                menu.append("\t").append(++count).append(". | ").append(pr.name).append(" | price: ").append(pr.price).append(" | technologies: ");
                for (Technology tech : pr.technologies)
                    menu.append(tech.name).append(" ");
                menu.append("| work days: ").append(pr.getTotalWorkDaysNeeded()).append("\n");
            }
        } else {
            menu.append("\tThere are no any projects available right now. Search for clients or hire a seller.\n");
        }

        menu.append("\n[Type the number of a project to work on (or 0 for none) then press Enter to continue]\n");
        System.out.print(menu);

        while(true){
            char ch = getKey();
            if (ch == '0') break;

            // if player selects a valid project then the project is added to company's project
            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum > 0 && selectedProjectNum <= projects.size()){
                company.addProject(projects.get(selectedProjectNum - 1));
                projects.remove(selectedProjectNum - 1);
                advanceNextDay();
                break;
            }

            System.out.println("[Not a valid option]" + selectedProjectNum);
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

        if (company.projects.size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
            return;
        }

        showAllProjects();
        System.out.println("[Type a number of a project you would like to write a code for. Or type 0 for exit. Then press Enter.]");

        Project prj;
        while (true){
            char ch = getKey();
            if (ch == '0') return;

            int selectedProjectNum = Character.getNumericValue(ch);
            if (selectedProjectNum >= 1 && selectedProjectNum <= company.projects.size()){
                prj = company.projects.get(selectedProjectNum - 1);

                if (prj.isCodeCompleted()){
                    System.out.println("(INFO) #" + selectedProjectNum + "project has code already completed.");
                    continue;
                }

                System.out.println("Chosen project: #" + selectedProjectNum + " " + prj.name);
                System.out.print("Project's technologies:\n\t");
                int count = 0;
                for (Technology tech:prj.technologies) {
                    System.out.print(++count + ". " + tech.name + "   ");
                }
                System.out.println("\n");
                System.out.println("[Type a number of a technology to write code for. Type 0 to cancel. Then Press Enter.]");

                while(true){
                    ch = getKey();
                    if (ch == '0') return;

                    int selectedTechNum = Character.getNumericValue(ch);
                    if (selectedTechNum >= 1 && selectedTechNum <= prj.technologies.size()){

                        Technology tech = prj.technologies.get(selectedTechNum - 1);

                        // check if tech is Mobile - player can't work on this, need to hire someone else
                        if (tech.name.equals("Mobile")){
                            System.out.println("(INFO) You can't work on Mobile technology. You need to get a contractor or hire a programmer.");
                            continue;
                        }

                        // check if tech is already completed, no more coding needed
                        if (tech.workDaysDone >= tech.workDaysNeeded) {
                            System.out.println("(INFO) The code for this technology is already complete.");
                            continue;
                        }

                        tech.workDaysDone += 1;
                        System.out.println("(INFO) You spent one day on coding for " + tech.name + " tech for " + prj.name + " project.");
                        advanceNextDay();
                        return;
                    }
                }
            }
        }
    }


    public void optionTesting(){

        if (company.projects.size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
            return;
        }

    }


    public void optionReturnProject(){

        if (company.projects.size() == 0){
            System.out.println("(INFO) Your company has no any projects.");
            return;
        }

    }



    private void showAllProjects(){

        System.out.println("COMPANY'S PROJECTS:\n");

        int count = 0;
        for (Project prj:company.getProjects()) {
            count++;

            System.out.println(
                    "\t" + count + ". | " + prj.name + " from " + prj.client.name + " | price: " + prj.price
            );
            System.out.print("\t\t techs: ");
            for (Technology tech : prj.technologies) {
                System.out.print(
                        tech.name + " (code " + tech.workDaysDone + "/" + tech.workDaysNeeded
                                + ", tests: " + tech.testDaysDone + "/" + tech.testDaysDone + "); "
                );
            }
            System.out.println();
        }

        System.out.println();
    }


    public void advanceNextDay(){
        currentDate = currentDate.plusDays(1);
    }


    public char getKey(){
        Scanner sc = new Scanner(System.in);
        return sc.next().charAt(0);
    }

    public static Integer randInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
