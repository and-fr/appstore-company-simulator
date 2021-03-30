package com.company.things;

import com.company.assets.Conf;
import com.company.people.Contractor;
import com.company.people.Person;

import java.util.ArrayList;
import java.util.List;


public class Company {

    // FIELDS

    private Double money;
    private Boolean hasOffice;
    private final List<Project> projects = new ArrayList<>();
    private final List<Person> employees = new ArrayList<>();
    private final List<Contractor> contractors = new ArrayList<>();


    // CONSTRUCTORS

    public Company(){
        money = Conf.START_MONEY;
        hasOffice = Conf.START_COMPANY_HAS_OFFICE;
    }


    // GETTERS

    public Double getMoney() { return money; }
    public Boolean getHasOffice() { return hasOffice; }
    public List<Project> getProjects(){ return projects; }
    public List<Person> getEmployees(){ return employees; }
    public List<Contractor> getContractors(){ return contractors; }


    // SETTERS

    public void addMoney(double money) { this.money += money; }
    public void addProject(Project project){ projects.add(project); }
    public void addEmployee(Person employee){ employees.add(employee); }
    public void setHasOffice(Boolean hasOffice) { this.hasOffice = hasOffice; }


    // OTHER METHODS

    public void showAllProjects(){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Project prj:projects) {
            sb.append("\t").append(++count).append(".");
            sb.append(" | ").append(prj.getName()).append(" from ").append(prj.getClient().getName());
            sb.append(" | price: ").append(prj.getPrice()).append("\n\t\t techs: ");
            for (Technology tech : prj.getTechnologies()) {
                sb.append(tech.getName()).append(" (code ").append(tech.getWorkDaysDone()).append("/");
                sb.append(tech.getWorkDaysNeeded()).append(", tests: ").append(tech.getTestDaysDone()).append("/");
                sb.append(tech.getWorkDaysDone()).append("); ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }


}
