package com.company.assets;

import java.time.LocalDate;

public class Conf {

    public static final Boolean START_COMPANY_HAS_OFFICE = false;

    public static final Double PAY_FOR_HOUR = 100.0;
    public static final Double PENALTY_MULTIPLIER = 0.1;
    public static final Double START_MONEY = 200.0;

    public static final Integer CLIENTS_NUM = 10;
    public static final Integer INITIAL_AVAILABLE_PROJECTS_NUM = 3;

    // MAX_AVAILABLE_PROJECTS and MAX_COMPANY_PROJECTS_AT_A_TIME
    // should be no more than 9, as the core game logic relies on
    // processing only inputs between 0 and 9 in every menu
    public static final Integer MAX_AVAILABLE_PROJECTS = 9;
    public static final Integer MAX_COMPANY_PROJECTS_AT_A_TIME = 9;

    // MAX_TECHNOLOGIES_PER_PROJECT should be no more than
    // the size of technologyNames array in Lang class file
    public static final Integer MAX_TECHNOLOGIES_PER_PROJECT = 5;

    public static final LocalDate START_DATE = LocalDate.of(2020, 1, 1);

}
