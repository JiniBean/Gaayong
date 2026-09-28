package com.gaayong.api.util;

import java.time.LocalDate;

public final class ApiDateHelper {

    private ApiDateHelper() {
    }

    public static String defaultYear(String year) {
        if (year != null && !year.isEmpty()) {
            return year;
        }
        return String.valueOf(LocalDate.now().getYear());
    }

    public static String defaultMonth(String month) {
        if (month != null && !month.isEmpty()) {
            return month;
        }
        return String.valueOf(LocalDate.now().getMonthValue());
    }
}
