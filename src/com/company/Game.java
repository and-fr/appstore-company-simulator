package com.company;


import com.company.people.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    Company company = new Company();


    public Game(){
        showWelcomeMessage();
        generateClients();
    }




    void showWelcomeMessage(){
        System.out.println(GAME_WELCOME_MESSAGE);
    }


    public Long getDayNumber() { return ChronoUnit.DAYS.between(START_DATE, currentDate) + 1; }


    public void showSummary(){
        String hasOffice = company.getHasOffice() ? "yes" : "no";

        System.out.println(
            "-> DAY " + getDayNumber() + " |  MONEY: " + company.getMoney() + " | PROJECTS: " + company.getProjectsCount() +
            " | OFFICE: " + hasOffice + " | EMPLOYEES: " + company.getEmployeesCount() + " | CONTRACTORS: " + company.getContractorsCount()
        );
    }


    void generateClients(){
        for (int i = 0; i < 10; i++)
            clients.add(new Client());
    }


    public static Integer randInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
