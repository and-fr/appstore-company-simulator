package com.company.assets;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Tool {


    public static Integer randInt(int min, int max){
        // max + 1 because we want inclusive max value for the range
        // this method by default returns values up to max but no max
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    public static char getKey(){
        Scanner sc = new Scanner(System.in);
        return sc.next().charAt(0);
    }

}
