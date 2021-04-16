package com.company.things;

import java.time.LocalDate;

public class Transaction {

    private final LocalDate processDate;
    private final Double money;
    private final String description;
    private Boolean isApproved;


    public Transaction(Double money, LocalDate processDate, String description){
        this.processDate = processDate;
        this.money = money;
        this.description = description;
        isApproved = false;
    }


    public LocalDate getProcessDate() { return processDate; }
    public Double getMoney() { return money; }
    public String getDescription() { return description; }
    public Boolean isApproved() { return isApproved; }


    public void setAsApproved() { isApproved = true; }
}
