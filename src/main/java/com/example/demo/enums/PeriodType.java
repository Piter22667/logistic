package com.example.demo.enums;

public enum PeriodType{
    DAY("day"),
    WEEK("week"),
    MONTH("month");

    public String getSqlValue() {
        return sqlValue;
    }

    private final String sqlValue;

    PeriodType(String sqlValue) {
        this.sqlValue = sqlValue;
    }
}
