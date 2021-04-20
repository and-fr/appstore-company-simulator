package com.company;

import com.company.assets.Conf;
import com.company.assets.Tool;
import com.company.people.Client;
import com.company.people.Employee;
import com.company.things.Project;
import com.company.things.Transaction;


public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        if (Conf.TEST_MODE_ENABLED){
            Project project;
            for(int i=1; i<=7; i++){
                project = new Project(new Client());
                project.setStartDate(game.getCurrentDate());
                game.getCompany().addProject(project);
            }
            for(int i=1; i<=7; i++){
                Employee employee = new Employee();
                employee.setHireDate(game.getCurrentDate());
                game.getCompany().addEmployee(employee);
            }
            for(int i=1; i<=3; i++){
                Transaction transaction = new Transaction(
                        i * 100.0,
                        game.getCurrentDate(),
                        "Test transaction IN no. " + i
                );
                game.getCompany().addTransactionIn(transaction);
            }
            System.out.println("<< TEST_MODE: added initial projects, employees, transactions to company >>");
        }

        game.console.summary(game);
        while(true){
            game.console.mainMenu();

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
                    game.optionCompanyTasks();
                    break;
                case '8': // company reports
                    game.optionCompanyReports();
                    break;
                case '9': // next day
                    game.advanceNextDay();
                    break;
            }
        }

    }
}
