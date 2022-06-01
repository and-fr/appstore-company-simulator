# :floppy_disk: Appstore - company simulator

This is a ***console game written in Java***. It's turn based simulation of management process over the small software company that creates mobile applications. You, as its owner, are supposed to make decisions which client projects to develop, what employess to hire, when and what payments to make, and decide whether to code or test already completed parts of the project yourself.


## More detailed description
You start the game with some amount of money. Your company has no workers, no office, and no clients. You can programm in Java, have some knowledge about databases and are able to create a front-end interfaces. However you know nothing how to create mobile applications. You need to delegate such tasks to someone else, either contractor or employee. Or you can just dismiss those projects. Your other skills include installing and configuring Wordpress pages and simple e-commerce soultions based on Prestashop.

At the begining of the game you have an option to start working on one of three available projects. Projects are simple, average, or complex. Without employees you can only do simple and average projects yourself.

Every project is described by various details:
- name of the project
- number of days needed to finish particular parts of the project
- technologies used in the project: front-end, backend, database, mobile, wordpress, prestashop
- client name
- price a client can pay for completed project
- and some other hidden random properties.

## How to play the game
You start the game at 1st of January, 2020. Each day equals one turn. If that's a day free of work then you can work alone, otherwise if that's a normal workday then your employees and contractors will also work.

Every day you can do the following:
- decide to start one of the client's projects
- search for new clients and projects (every 5 days new project should appear)
- programm client's application
- test client's application
- deliver final aplication to client
- hire new employee
- fire an employee
- make payments.

## Winning the game
You win the game if all following conditions are met:
- your company receives three full payments for complex projects,
- at least one of those projects has been aquired by salesman hired by you,
- no any part of those projects should be programmed and tested by you,
- your company should have more money than at starting day.

## Loosing the game
When the money of your company reach 0 then Game Over.
