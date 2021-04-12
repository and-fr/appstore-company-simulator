package com.company;

import com.company.assets.Tool;
import com.company.people.Client;
import com.company.people.Employee;
import com.company.things.Project;


public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        // TEST CODE START
        Project project;
        for(int i=1; i<=7; i++){
            project = new Project(new Client());
            project.setStartDate(game.getCurrentDate());
            game.getCompany().addProject(project);
        }
        for(int i=1; i<=7; i++)
            game.getCompany().addEmployee(new Employee());
        // TEST CODE END

        game.showSummary();
        while(true){
            game.showMainMenu();

            switch (Tool.getKey()){
                case '0':
                    return;
                case '1': // check available projects
                    game.optionAvailableProjects();
                    break;
                case '2': // search for clients
                    game.optionSearchForClients();
                    break;
                case '3': // programming: coding or testing
                    game.optionProgramming();
                    break;
                case '4': // return project
                    game.optionReturnProject();
                    break;
                case '5': // contractors
                    game.optionContractors();
                    break;
                case '6': // employees
                    game.optionEmployees();
                    break;
                case '7': // company tasks
                    break;
                case '8': // company reports
                    break;
                case '9': // next day
                    game.advanceNextDay();
                    break;
            }
        }

    }
}
