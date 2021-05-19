package com.company.people;

import com.company.assets.Lang;
import com.company.assets.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Contractor extends Person{

    public final Double payForHour;
    public final Boolean finishOnTime;
    public final Boolean noErrors;
    public final List<String> skills = generateSkills();


    public Contractor(Double payForHour, Boolean finishOnTime, Boolean noErrors) {
        this.payForHour = payForHour;
        this.finishOnTime = finishOnTime;
        this.noErrors = noErrors;
    }


    private List<String> generateSkills(){
        List<String> skills = new ArrayList<>(Arrays.asList(Lang.technologyNames));
        Collections.shuffle(skills);
        return skills.subList(0, Tool.randInt(1, skills.size()));
    }

}
