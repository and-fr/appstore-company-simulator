package com.company.people;

import com.company.Game;

public class Person {

    private static String[] firstNames = {
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
            "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen"
    };

    private static String[] lastNames = {
            "Smith", "Johnson", "Anderson", "Nelson", "Olson", "Miller", "Garcia", "Hernandez", "Lopez", "Martinez",
            "Williams", "Brown", "Jones", "Lee", "Wong", "Kim"
    };


    public static String generateName(){
        return firstNames[Game.randInt(0,firstNames.length-1)] + " " + lastNames[Game.randInt(0, lastNames.length-1)];
    }
}
