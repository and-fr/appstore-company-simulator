package com.company;

import com.company.people.Contractor;
import com.company.people.Person;

import java.util.ArrayList;
import java.util.List;


public class Company {

    private Double money = 200.0;
    private List<Contractor> contractors = new ArrayList<>();
    private List<Person> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private Boolean hasOffice = false;


    public Boolean getHasOffice() { return hasOffice; }
    public Double getMoney() { return money; }
    public Integer getContractorsCount(){ return contractors.size(); }
    public Integer getEmployeesCount(){
        return employees.size();
    }
    public Integer getProjectsCount(){
        return projects.size();
    }


    public void addEmployee(Person employee){ employees.add(employee); }
    public void setHasOffice(Boolean hasOffice) { this.hasOffice = hasOffice; }

}
