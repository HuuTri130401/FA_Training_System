package com.fptacademy.training.service.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateTimeUtil {


    //Test later
    public static LocalDate getFirstDateOfCurrentWeek(LocalDate date) {
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        return date.minusDays(date.getDayOfWeek()
                .getValue() - firstDayOfWeek.getValue()
        );
    }

    public static LocalDate getLastDateOfCurrentWeek(LocalDate date) {
        return getFirstDateOfCurrentWeek(date).plusDays(6);
    }

}
