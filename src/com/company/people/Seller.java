package com.company.people;

public class Seller extends Person {

    private Integer searchDaysForClients;


    public Integer getSearchDaysForClients() {return searchDaysForClients; }


    public void setSearchDaysForClients(Integer number) { searchDaysForClients = number; }


    public Seller(){
        searchDaysForClients = 0;
    }
}
