package com.company.things;

import com.company.people.Employee;

import java.time.LocalDate;

public class Transaction {

    private final LocalDate processDate;
    private final Double money;
    private final String description;
    private Boolean isApproved;
    private Boolean isSalary;
    private Boolean isCostGenerated;
    private Boolean isMandatoryCost;
    private Boolean isPayed;
    private Employee employee;


    public Transaction(Double money, LocalDate processDate, String description){
        this.processDate = processDate;
        this.money = money;
        this.description = description;
        isApproved = false;
        isSalary = false;
        isCostGenerated = false;
        isMandatoryCost = false;
        isPayed = false;
        employee = null;
    }


    public LocalDate getProcessDate() { return processDate; }
    public Double getMoney() { return money; }
    public String getDescription() { return description; }
    public Boolean isApproved() { return isApproved; }
    public Boolean isSalary() { return isSalary; }
    public Boolean isCostGenerated() { return isCostGenerated; }
    public Boolean isMandatoryCost() { return isMandatoryCost; }
    public Boolean isPayed() { return isPayed; }
    public Employee getEmployee() { return employee; }


    public void setAsApproved() { isApproved = true; }
    public void setAsSalary() { isSalary = true; }
    public void setAsCostGenerated() { isCostGenerated = true; }
    public void setAsMandatoryCost() { isMandatoryCost = true; }
    public void setAsPayed() { isPayed = true; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
