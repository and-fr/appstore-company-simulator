package com.company;

import com.company.people.Client;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        while(true){
            game.showSummary();
            game.showMainMenu();

            switch (game.getKey()){
                case '0':
                    return;
                case '1': // check available projects
                    game.optionAvailableProjects();
                    break;
                case '2': // search for clients
                    game.optionSearchForClients();
                    break;
                case '3': // programming
                    game.optionProgramming();
                    break;
                case '4': // testing
                    game.optionTesting();
                    break;
                case '5': // return project
                    game.optionReturnProject();
                    break;
                case '6': // hire/fire employee
                    break;
                case '7': // hire contractor
                    break;
                case '8': // company tasks
                    break;
                case '9': // next day
                    game.advanceNextDay();
                    break;
            }
        }

    }
}
