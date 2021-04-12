package com.company.assets;

import java.time.LocalDate;

public class Conf {

    // BOOLEANS

    public static final Boolean START_COMPANY_HAS_OFFICE = true;


    // DOUBLES

    public static final Double PAY_FOR_HOUR = 100.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_HIGH = 70.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_LOW = 50.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_MID = 60.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_SELLER = 30.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_TESTER = 40.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER = 50.0;
    public static final Double PRICE_PENALTY_MULTIPLIER = 0.1;
    public static final Double SEARCH_FOR_EMPLOYEES_COST = 10.0;
    public static final Double START_MONEY = 200.0;


    // INTEGERS

    public static final Integer CLIENTS_NUM = 10;
    public static final Integer CONTRACTORS_CODE_DAY_FAILURE_CHANCE_PERCENT = 20;
    public static final Integer CONTRACTORS_TEST_DAY_FAILURE_CHANCE_PERCENT = 30;
    public static final Integer CONTRACTORS_PAY_AFTER_DAYS = 14;
    public static final Integer EMPLOYEE_TYPE_CHANCE_RANGE1 = 25;
    public static final Integer EMPLOYEE_TYPE_CHANCE_RANGE2 = 50;
    public static final Integer INITIAL_AVAILABLE_EMPLOYEES_NUM = 3;
    public static final Integer INITIAL_AVAILABLE_PROJECTS_NUM = 3;
    public static final Integer PLAYER_LUCKY_TEST_DAY_CHANCE_PERCENT = 25;
    public static final Integer PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD = 75;
    public static final Integer TESTER_ADDITIONAL_TESTS_CHANCE = 20;

    // following settings should be no more than 9
    // as the core game logic relies on processing
    // inputs between 0 and 9 in every menu when 0 is usually exit option
    public static final Integer MAX_AVAILABLE_EMPLOYEES = 9;
    public static final Integer MAX_AVAILABLE_PROJECTS = 9;
    public static final Integer MAX_COMPANY_PROJECTS_AT_A_TIME = 9;
    public static final Integer MAX_COMPANY_EMPLOYEES_AT_A_TIME = 9;

    // following settings should be no more than
    // the size of technologyNames array in Lang class file
    public static final Integer MAX_TECHNOLOGIES_PER_PROJECT = 5;
    public static final Integer PROGRAMMERS_MAX_SKILLS = 4;


    // OTHER

    public static final LocalDate START_DATE = LocalDate.of(2020, 1, 1);

}
