package com.example.demo.dto.response;

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
