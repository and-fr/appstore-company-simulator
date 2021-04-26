package com.company.people;

import com.company.assets.Lang;
import com.company.assets.Tool;

public class Person {

    private final String firstName;
    private final String lastName;


    protected Person(){
        firstName = Lang.firstNames[Tool.randInt(0, Lang.firstNames.length - 1)];
        lastName = Lang.lastNames[Tool.randInt(0, Lang.lastNames.length - 1)];
    }


    public String getName() { return firstName + " " + lastName; }

}
