package com.company.people;

import com.company.assets.Lang;
import com.company.assets.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Contractor extends Person{

    private final Double payForHour;
    private final Boolean finishOnTime;
    private final Boolean noErrors;
    private List<String> skills;


    public Double getPayForHour() { return payForHour; }
    public Boolean isFinishOnTime() { return finishOnTime; }
    public Boolean isNoErrors() { return noErrors; }
    public List<String> getSkills() { return skills; }


    public Contractor(Double payForHour, Boolean finishOnTime, Boolean noErrors) {
        this.payForHour = payForHour;
        this.finishOnTime = finishOnTime;
        this.noErrors = noErrors;
        skills = generateSkills();
    }


    private List<String> generateSkills(){
        List<String> skills = new ArrayList<>(Arrays.asList(Lang.technologyNames));
        Collections.shuffle(skills);
        return skills.subList(0, Tool.randInt(1, skills.size()));
    }



}
