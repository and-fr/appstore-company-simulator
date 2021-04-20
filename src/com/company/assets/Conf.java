package com.company.assets;

import java.time.LocalDate;

public class Conf {

    // BOOLEANS

    public static final Boolean START_COMPANY_HAS_OFFICE = false;
    public static final Boolean TEST_MODE_ENABLED = true;


    // DOUBLES

    public static final Double EMPLOYEE_HIRE_COST = 300.0;
    public static final Double EMPLOYEE_SEARCH_COST = 10.0;
    public static final Double OFFICE_MONTHLY_COST = 500.0;
    public static final Double PAY_FOR_HOUR = 100.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_HIGH = 60.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_LOW = 40.0;
    public static final Double PAY_FOR_HOUR_CONTRACTOR_MID = 50.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_SELLER = 10.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_SELLER_MAX_BONUS = 10.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_TESTER = 15.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER = 15.0;
    public static final Double PAY_FOR_HOUR_EMPLOYEE_PROGRAMMER_TECH_BONUS = 5.0;
    public static final Double PRICE_PENALTY_MULTIPLIER = 0.1;
    public static final Double START_MONEY = 2000000.0;


    // INTEGERS

    public static final Integer CLIENTS_NUM = 10;
    public static final Integer CONTRACTORS_CODE_DAY_FAILURE_CHANCE_PERCENT = 20;
    public static final Integer CONTRACTORS_TEST_DAY_FAILURE_CHANCE_PERCENT = 30;
    public static final Integer CONTRACTORS_PAY_AFTER_DAYS = 14;
    public static final Integer EMPLOYEE_SICKNESS_CHANCE = 5;
    public static final Integer EMPLOYEE_SICKNESS_DAYS_MAX = 7;
    public static final Integer EMPLOYEE_TYPE_CHANCE_RANGE1 = 25;
    public static final Integer EMPLOYEE_TYPE_CHANCE_RANGE2 = 50;
    public static final Integer EMPLOYEE_WORK_COST_PERCENT = 20;
    public static final Integer INITIAL_AVAILABLE_EMPLOYEES_NUM = 3;
    public static final Integer INITIAL_AVAILABLE_PROJECTS_NUM = 3;
    public static final Integer OFFICE_RENT_PAY_AFTER_DAYS = 7;
    public static final Integer PLAYER_LUCKY_TEST_DAY_CHANCE_PERCENT = 25;
    public static final Integer PROGRAMMER_SKIP_DAY_MAX_PERCENT_CHANCE = 25;
    public static final Integer PROJECT_PAYMENT_ADVANCE_PERCENT = 20;
    public static final Integer PROJECT_PAYMENT_ADVANCE_AFTER_DAYS = 3;
    public static final Integer PROJECT_PAYMENT_ADVANCE_FOR_AT_LEAST_TECHS = 4;
    public static final Integer PROJECT_PAYMENT_ADVANCE_FOR_AT_LEAST_DAYS = 50;
    public static final Integer PROJECT_PERCENT_COMPLETION_MIN_ACCEPT_THRESHOLD = 75;
    public static final Integer TAX_FROM_INCOME_MONTHLY_PERCENT = 10;
    public static final Integer TESTER_ADDITIONAL_TESTS_CHANCE = 20;
    public static final Integer VALID_PROJECTS_TO_WIN_COUNT = 3;
    public static final Integer VALID_PROJECTS_TO_WIN_MIN_TECHS = 4;

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
