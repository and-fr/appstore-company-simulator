package com.company.assets;

public class Lang {

    public static final String GAME_WELCOME_MESSAGE = """
        # IT SOFTWARE COMPANY SIMULATOR
        # You start with some amount of money. You have no office, no employees, no clients.
        # You know how to program in Java, have some knowledge of databases, and you can do some front-end work.
        # You can also configure a webpage using Wordpress, and implement e-commerce solution using Prestashop.
        # You can't create mobile apps. For that you need to make a deal with a contractor, hire an employee, or avoid such projects.
        """;

    public static final String[] projectNames = {
        "E-Commerce Solution", "Company Web Page", "Company Web Page Integration", "Software Upgrade",
        "Software Update", "Simulation Software", "Scientific Research Project", "Infrastructure Upgrade",
        "Infrastructure Update", "API Implementation", "Security Solutions"
    };

    public static final String[] technologyNames = {
        "Front-End", "Backend", "Database", "Mobile", "Wordpress", "Prestashop"
    };

    public static String[] firstNames = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
        "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen"
    };

    public static String[] lastNames = {
        "Smith", "Johnson", "Anderson", "Nelson", "Olson", "Miller", "Garcia", "Hernandez", "Lopez", "Martinez",
        "Williams", "Brown", "Jones", "Lee", "Wong", "Kim"
    };

    public static final String MAIN_MENU = """
        \t1. Take a new project     5. Return project to client     9. Next day
        \t2. Search for clients     6. Hire/Fire an employee        0. Exit
        \t3. Programming            7. Hire a contractor
        \t4. Testing                8. Company tasks
        
        [Type a number and press Enter]
        """;

}
