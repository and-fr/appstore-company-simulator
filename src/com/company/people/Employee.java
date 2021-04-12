package com.company.people;

import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.assets.Tool;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Employee extends Person{

    private final String role;
    private LocalDate hireDate;
    private LocalDate fireDate;
    private Integer searchDaysForClients;
    private final Double monthlySalary;
    private List<String> skills;
    private Integer codeDayDelayChance;
    private Integer testDayDelayChance;


    // CONSTRUCTORS

    public Employee(){
        role = generateEmployeeRole();
        monthlySalary = generateMonthlySalary();

        if (isSeller()) searchDaysForClients = 0;

        if (isProgrammer()) {
            skills = generateSkills();
        }
    }



    // GETTERS

    public Boolean isProgrammer() { return role.equals("Programmer"); }
    public Boolean isSeller() { return role.equals("Seller"); }
    public Boolean isTester() { return role.equals("Tester"); }
    public LocalDate getHireDate() { return hireDate; }
    public LocalDate getFireDate() { return fireDate; }
    public Integer getSearchDaysForClients() { return searchDaysForClients; }
    public String getEmployeeRole() { return role; }
    public Double getMonthlySalary() { return monthlySalary; }
    public  List<String> getSkills() { return skills; }



    // SETTERS

    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setFireDate(LocalDate fireDate) { this.fireDate = fireDate; }
    public void setSearchDaysForClientsPlus(int days) { searchDaysForClients += days; }
    public void resetSearchDays() { searchDaysForClients = 0; }


    // OTHER METHODS

    private String generateEmployeeRole(){

        // by default a chance for employee's type is:
        // 25% seller, 25% tester, 50% programmer
        int rnd = Tool.randInt(1,100);

        if (rnd <= Conf.EMPLOYEE_TYPE_CHANCE_RANGE1)
            return "Seller";

        if (rnd <= Conf.EMPLOYEE_TYPE_CHANCE_RANGE2)
            return "Tester";

        return "Programmer";
    }


    private Double generateMonthlySalary(){
        double salary = 0.0;

        if (isSeller()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_SELLER;
        if (isTester()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_TESTER;
        if (isProgrammer()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER;

        return salary * 8.0 * 30.0;
    }


    private List<String> generateSkills(){
        List<String> skills = new ArrayList<>(Arrays.asList(Lang.technologyNames));
        Collections.shuffle(skills);
        return skills.subList(0, Tool.randInt(1, Conf.PROGRAMMERS_MAX_SKILLS));
    }
}
