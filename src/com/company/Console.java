package com.company;

import com.company.assets.Conf;
import com.company.people.Contractor;
import com.company.people.Employee;
import com.company.things.Company;
import com.company.things.Project;
import com.company.things.Technology;
import com.company.things.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


public class Console {

    // methods of this class generate console messages


    public void welcomeMessage(){
        System.out.print("""
        IT SOFTWARE COMPANY SIMULATOR
        You start with some amount of money. You have no office, no employees, no clients.
        You know how to program in Java, have some knowledge of databases, and you can do some front-end work.
        You can also configure a webpage using Wordpress, and implement e-commerce solution using Prestashop.
        You can't create mobile apps. For that you need to make a deal with a contractor, hire an employee, or avoid such projects.
        """);
    }


    public void mainMenu(){
        System.out.print("""
        
        MAIN MENU
        
        \t1. New projects         3. Projects Code/Test/View   5. Contractors   7. Company tasks     9. Next day
        \t2. Search for clients   4. Return project            6. Employees     8. Company reports   0. Exit
        
        [Type a number and press Enter]
        """);
    }


    public void summary(Game game){
        int dayNumber = (int) (ChronoUnit.DAYS.between(Conf.START_DATE, game.getCurrentDate()) + 1);

        StringBuilder summary = new StringBuilder();
        summary.append("DAY ").append(dayNumber).append(" (").append(game.getCurrentDate()).append(" ").append(game.getCurrentDate().getDayOfWeek()).append(") | ");
        summary.append("MONEY: ").append(BigDecimal.valueOf(game.getCompany().getMoney()).toPlainString()).append(" | ");
        summary.append("PROJECTS: ").append(game.getCompany().getProjects().size()).append(" | ");
        summary.append("OFFICE: ").append(game.getCompany().hasOffice() ? "yes" : "no").append(" | ");
        summary.append("EMPLOYEES: ").append(game.getCompany().getEmployees().size()).append(" | ");
        summary.append("CONTRACTORS: ").append(game.getCompany().getContractorsCount()).append("\n");

        StringBuilder line = new StringBuilder().append("-".repeat(summary.length())).append("\n");

        System.out.print("\n" + line + summary + line);
    }


    public void info(String text){
        if (!text.equals(""))
            System.out.println("-> " + text);
    }


    public void actionMessage(String text){
        System.out.println("[" + text + " (or 0 for cancel) then press Enter.]");
    }


    public void menuProjectsAvailable(List<Project> projects){
        StringBuilder sb = new StringBuilder("AVAILABLE PROJECTS:\n\n");
        int count = 0;
        for (Project pr : projects) {
            sb.append("\t").append(++count).append(". | ").append(pr.getName()).append(" from ").append(pr.getClient().getName()).append(" | price: ").append(pr.getPrice()).append(" | technologies: ");
            for (Technology tech : pr.getTechnologies())
                sb.append(tech.getName()).append(" ");
            sb.append("| work days: ").append(pr.getTotalWorkDaysNeeded());
            if (pr.isNegotiatedBySeller())
                sb.append(" | NEGOTIATED BY ").append(pr.getSeller().getName()).append(" (price: +").append(pr.getPriceBonus()).append(")");
            sb.append("\n");
        }
        sb.append("\n");
        System.out.print(sb);

        actionMessage("Type the number of a project to work on");
    }


    public void menuProjectsCurrent(List<Project> projects){
        StringBuilder sb = new StringBuilder("COMPANY'S PROJECTS:\n\n");
        int count = 0;
        for (Project prj:projects) {
            sb.append("\t").append(++count).append(".");
            sb.append(" | ").append(prj.getName()).append(" for ").append(prj.getClient().getName());
            sb.append(" | price: ").append(prj.getPrice());
            if (prj.getSeller() != null)
                sb.append(" (N)");
            sb.append(" | deadline: ").append(prj.getDeadline());
            if (prj.getProgrammers().size() > 0){
                sb.append(" | programmers: ");
                for(Employee programmer:prj.getProgrammers())
                    sb.append(programmer.getName()).append(" ");
            }
            if (prj.getTester() != null)
                sb.append(" | tester: ").append(prj.getTester().getName());
            sb.append("\n\t\t techs: ");
            for (Technology tech : prj.getTechnologies()) {
                sb.append(tech.getName()).append(" (code ").append(tech.getCodeDaysDone()).append("/");
                sb.append(tech.getCodeDaysNeeded()).append(", tests: ").append(tech.getTestDaysDone()).append("/").append(tech.getCodeDaysDone());
                sb.append((tech.isContractorAssigned() ? ", contractor" : "")).append("); ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }


    public void menuEmployees(){
        System.out.println(
            "EMPLOYEES\n\n" + "\t1. View employees\n" +
            "\t2. Hire an employee (cost: " + Conf.EMPLOYEE_HIRE_COST + ")\n" +
            "\t3. Fire an employee\n" +
            "\t4. Search for employees (cost: " + Conf.EMPLOYEE_SEARCH_COST + ")\n" +
            "\t5. Assign testers to projects\n" +
            "\t6. Assign programmers to projects\n"
        );
        actionMessage("Type a number of an option");
    }


    public void employeesForHire(List<Employee> employees){
        StringBuilder sb = new StringBuilder("EMPLOYEES FOR HIRE:\n\n");
        int count = 0;
        for (Employee employee : employees) {
            sb.append("\t").append(++count).append(". ").append(employee.getName()).append(" (").append(employee.getEmployeeRole().toLowerCase());
            if (employee.isProgrammer()) {
                sb.append(": ");
                for (String skill:employee.getSkills())
                    sb.append(skill).append(" ");
            }
            sb.append(") | monthly salary: ").append(employee.getMonthlySalary()).append("\n");
        }
        System.out.println(sb);
    }


    public void employeesForFire(List<Employee> employees){
        System.out.println("COMPANY'S EMPLOYEES:\n");
        int count = 0;
        for(Employee employee: employees)
            System.out.println("\t" + ++count + ". " + employee.getName()
                    + " (" + employee.getEmployeeRole().toLowerCase() + ")");
        System.out.println("\n");
    }


    public void testers(Company company){
        StringBuilder sb = new StringBuilder("COMPANY'S TESTERS:\n\n");
        Project project;
        int count = 0;
        for(Employee tester:company.getTesters()) {
            project = company.getProjectTesterIsAssignedTo(tester);
            sb.append("\t").append(++count).append(". ").append(tester.getName()).append(project == null ? " (unassigned)" : ", works on " + project.getName() + " project").append("\n");
        }
        System.out.println(sb);
    }


    public void programmers(Company company){
        StringBuilder sb = new StringBuilder("COMPANY'S PROGRAMMERS:\n\n");
        Project prj;
        int count = 0;
        for(Employee programmer:company.getProgrammers()) {
            prj = company.getProjectProgrammerIsAssignedTo(programmer);
            sb.append("\t").append(++count).append(". ").append(programmer.getName());
            sb.append(" ( ");
            for(String skill:programmer.getSkills())
                sb.append(skill).append(" ");
            sb.append(")");
            if (prj != null)
                sb.append(" works on ").append(prj.getName()).append(" project");
            sb.append("\n");
        }
        System.out.println(sb);
    }


    public void menuTasks(Company company){
        StringBuilder sb = new StringBuilder("COMPANY, TASKS\n\n");
        sb.append("\t1. Approve payments     2. View approved payments status     3. View income");
        if (!company.hasOffice())
            sb.append("     4. Rent an office (monthly cost: ").append(Conf.OFFICE_MONTHLY_COST).append(")");
        sb.append("\n");
        System.out.println(sb);

        actionMessage("Type a number of an option");
    }


    public void menuPaymentsUnapproved(Company company){
        System.out.println("COMPANY'S UNAPPROVED PAYMENTS:\n");
        int count = 0;
        for(Transaction tr:company.getUnapprovedTransactions())
            System.out.println(++count + ". " + tr.getDescription() + " | " + tr.getMoney() + " | " + tr.getProcessDate());
        System.out.println("\n");

        actionMessage("Type numbers separated by coma of transactions you want to approve. Type all to approve all transactions");
    }


    public void menuPaymentsApproved(Company company){
        if (company.getApprovedTransactions().size() == 0){
            info("There are no approved transactions today.");
            return;
        }
        System.out.println("APPROVED PAYMENTS STATUS:\n");
        int count = 0;
        for(Transaction tr:company.getApprovedTransactions())
            System.out.println("\t" + ++count + ". " + tr.getDescription() + " | " + tr.getMoney() + " | " + tr.getProcessDate());
        System.out.println();
    }


    public void menuIncome(List<Transaction> transactions){
        if (transactions.size() == 0){
            info("There were no any incoming payments yet.");
            return;
        }
        System.out.println("COMPANY'S INCOME:\n");
        int count = 0;
        for(Transaction tr:transactions)
            System.out.println("\t" + ++count + ". " + tr.getDescription() + " | " + tr.getMoney() + " | " + tr.getProcessDate());
        System.out.println();
    }


    public void contractorsForHire(List<Contractor> contractors){
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


    public void menuWorkType(Technology technology){
        System.out.println("On what type of work you want to focus on with " + technology.getName() + " technology?\n");
        System.out.println("\t1. Programming     2. Testing\n");
        actionMessage("Type a number of your work choice");
    }


    public void menuProjectReturnConfirmation(Project project, LocalDate currentDate){
        int delayDays = project.getDaysOfDelay(currentDate);
        int codePercentComplete;
        int testPercentComplete;

        StringBuilder sb = new StringBuilder("PROJECT'S SUMMARY:\n");
        sb.append(project.getName()).append(" for ").append(project.getClient().getName());
        sb.append(" | price: ").append(project.getPrice());
        sb.append(" | deadline: ").append(project.getDeadline());
        sb.append(" (delay days: ").append( delayDays > 0 ? delayDays : "no" ).append(")\n");
        sb.append("techs: ");
        for (Technology tech:project.getTechnologies()) {
            codePercentComplete = (int) (((double)tech.getCodeDaysDone() / (double)tech.getCodeDaysNeeded()) * 100.0);
            testPercentComplete = (int) (((double)tech.getTestDaysDone() / (double)tech.getCodeDaysNeeded()) * 100.0);
            sb.append(tech.getName()).append(" (code: ").append(codePercentComplete).append("%, tests: ").append(testPercentComplete).append("%) ");
        }

        sb.append("\nCODE COMPLETED: ").append(project.getCodeCompletionPercent()).append("% | ");
        sb.append("TESTS COMPLETED: ").append(project.getTestCompletionPercent()).append("% | ");
        sb.append("OVERALL: ").append(project.getCompletionPercent()).append("%\n");

        System.out.println(sb);

        actionMessage("Do you really want to return this project to client? y/n");
    }


    public void menuSelectedTransactionsApproval(List<Integer> transactions){
        StringBuilder sb = new StringBuilder("[Selected transactions: ");
        for(int trNum:transactions)
            sb.append(trNum).append(" ");
        sb.append("Approve? y/n]");
        System.out.println(sb);
    }


    public void menuSelectedTransactionsConfirmation(Company company, List<Integer> transactions){
        StringBuilder sb = new StringBuilder("You approved transactions: ");
        transactions.sort(Collections.reverseOrder());
        for(int num:transactions){
            company.getUnapprovedTransactions().get(num - 1).setAsApproved();
            sb.append(num).append(" ");
        }
        info(sb.toString());
    }


    public void employeesStatus(Company company){
        Project project;
        StringBuilder sb = new StringBuilder("COMPANY'S EMPLOYEES\n-------------------\n");

        sb.append("PROGRAMMERS:\n");
        for (Employee programmer:company.getProgrammers()){
            sb.append("\t").append(programmer.getName());
            project = company.getProjectProgrammerIsAssignedTo(programmer);
            if (programmer.isSick())
                sb.append(" (SICK: ").append(programmer.getSickDays()).append(")");
            if (project == null)
                sb.append(" (UNASSIGNED)");
            else
                sb.append(" (works on ").append(project.getName()).append(" project)");
            sb.append(" | skills: ");
            for(String skill:programmer.getSkills())
                sb.append(skill).append(" ");
            sb.append("| salary: ").append(programmer.getMonthlySalary()).append("\n");
        }

        sb.append("TESTERS:\n");
        for (Employee tester:company.getTesters()){
            sb.append("\t").append(tester.getName());
            project = company.getProjectTesterIsAssignedTo(tester);
            if (tester.isSick())
                sb.append(" (SICK: ").append(tester.getSickDays()).append(")");
            if (project == null)
                sb.append(" (UNASSIGNED)");
            else
                sb.append(" (works on ").append(project.getName()).append(" project)");
            sb.append(" | salary: ").append(tester.getMonthlySalary()).append("\n");
        }

        sb.append("SELLERS:\n");
        for (Employee seller:company.getSellers()){
            sb.append("\t").append(seller.getName());
            if (seller.isSick())
                sb.append(" (SICK, days left: ").append(seller.getSickDays()).append(")");
            sb.append(" | salary: ").append(seller.getMonthlySalary()).append("\n");
        }

        sb.append("-------------------\n");

        System.out.println(sb);
    }


    public void projectTechnologies(Project project){
        System.out.println("Project: " + project.getName());
        System.out.println("Technologies:\n");
        int count = 0;
        for (Technology tech:project.getTechnologies())
            System.out.print("\t" + ++count + ". " + tech.getName() + "   ");
        System.out.println("\n");
    }


    public void companyReports(Game game){

        System.out.println(
                "COMPANY'S REPORTS - CONDITIONS TO WIN:\n\n" +
                "\t1) Payments for complex projects(*): " + game.getCompany().getValidComplexProjectsPayed().size() + "/" + Conf.VALID_PROJECTS_TO_WIN_COUNT + "\n" +
                "\t2) Complex projects negotiated by a seller: " + game.getCompany().getValidComplexProjectsWithSeller().size() + "/1" + "\n" +
                "\t3) Current amount of money higher than at the game's start: " + ((game.getCompany().getMoney() > Conf.START_MONEY) ? "YES" : "NO") + "\n\n" +
                "(*) complex projects are projects with at least " + Conf.VALID_PROJECTS_TO_WIN_MIN_TECHS + " techs and company's owner was not involved in any coding or testing\n" +
                "    also at least one of those projects needs to be negotiated by one of company's sellers\n"
        );
    }


    public void gameOver(){
        System.out.println("<<< GAME OVER >>>");
    }
}
