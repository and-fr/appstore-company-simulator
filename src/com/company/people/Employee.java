package com.company.people;

import com.company.Console;
import com.company.assets.Conf;
import com.company.assets.Lang;
import com.company.assets.Tool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Employee extends Person{

    public final String role = generateEmployeeRole();
    public LocalDate hireDate;
    public Integer searchDaysForClients;
    private final Double payForHourBase;
    public final Double payForHourBonus;
    public List<String> skills;
    public Integer skipDayPercentChance;
    public Integer sickDays = 0;
    private final Console console = new Console();


    public Employee(){
        if (isSeller()) searchDaysForClients = 0;
        if (isProgrammer()) {
            skills = generateSkills();
            skipDayPercentChance = Tool.randInt(0, Conf.PROGRAMMER_SKIP_DAY_MAX_PERCENT_CHANCE);
        }
        payForHourBase = generatePayForHourBase();
        payForHourBonus = generatePayForHourBonus();
    }


    public Boolean isProgrammer() { return role.equals("Programmer"); }
    public Boolean isSeller() { return role.equals("Seller"); }
    public Boolean isTester() { return role.equals("Tester"); }
    public Double getPayForHour() { return payForHourBase + payForHourBonus; }
    public Double getMonthlySalary() {
        return (payForHourBase + payForHourBonus) * 8.0 * 30.0;
    }
    public Boolean isSick() { return sickDays > 0; }


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


    private Double generatePayForHourBase(){
        double salary = 0.0;

        if (isSeller()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_SELLER;
        if (isTester()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_TESTER;
        if (isProgrammer()) salary = Conf.PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER;

        return salary;
    }


    private Double generatePayForHourBonus(){
        double salaryBonus = 0.0;

        if (isSeller()){
            // bonus for sellers is generated randomly
            // this value affects monthly payments and increases chance to negotiate better price for projects
            BigDecimal bd = new BigDecimal(String.valueOf(Conf.PAY_FOR_HOUR_EMPLOYEE_SELLER_MAX_BONUS));
            salaryBonus += Tool.randInt(0, bd.intValue());
        }

        if (isProgrammer()){
            // bonus for programmers depends on number of techs programmer knows
            salaryBonus += (double) skills.size() * Conf.PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER_TECH_BONUS;
            // bonus also depends on how reliable programmer is (skipDayPercentChance)
            salaryBonus += Conf.PROGRAMMER_SKIP_DAY_MAX_PERCENT_CHANCE - skipDayPercentChance;
        }

        return salaryBonus;
    }


    private List<String> generateSkills(){
        List<String> skills = new ArrayList<>(Arrays.asList(Lang.technologyNames));
        Collections.shuffle(skills);
        return skills.subList(0, Tool.randInt(1, Conf.PROGRAMMERS_MAX_SKILLS));
    }


    public void setSickness(){
        // sickness handling

        // if employee is healthy, then there's always a chance to catch a disease
        if (sickDays <= 0)
            if (Tool.randInt(1,100) <= Conf.EMPLOYEE_SICKNESS_CHANCE){
                sickDays = Tool.randInt(1,Conf.EMPLOYEE_SICKNESS_DAYS_MAX);
                console.info(role + ", " + getName() + " got sick. Estimated away days: " + sickDays);
                return;
            }

        if (sickDays > 0) {
            sickDays -= 1;
            if (sickDays == 0)
                console.info(role + ", " + getName() + " is healthy now.");
        }
    }
}
