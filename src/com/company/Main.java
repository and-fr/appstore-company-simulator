package com.company;

import com.company.people.Client;

public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        game.showSummary();


        Project prj;

        for (int i = 1; i <= 100; i++) {
            prj = new Project(new Client());

            System.out.println("#" + i);
            System.out.println("name: " + prj.name);
            System.out.println("techs:");
            for (Technology tech : prj.technologies) {
                System.out.println(tech.name + " " + tech.workDaysNeeded);
            }
            System.out.println("price: " + prj.price);
            System.out.println("penalty: " + prj.penalty);
            System.out.println("payment due: " + prj.paymentDue);
            System.out.println("payment never: " + prj.paymentNever);
            System.out.println("payment delay days: " + prj.paymentDelayDays);
            System.out.println();
        }

    }
}
