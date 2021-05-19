package com.company.things;

import com.company.people.Employee;

import java.time.LocalDate;

public class Transaction {

    public final LocalDate processDate;
    public final Double money;
    public final String description;
    public Boolean isApproved = false;
    public Boolean isSalary = false;
    public Boolean isCostGenerated = false;
    public Boolean isMandatoryCost = false;
    public Boolean isPayed = false;
    public Employee employee = null;


    public Transaction(Double money, LocalDate processDate, String description){
        this.processDate = processDate;
        this.money = money;
        this.description = description;
    }
}
