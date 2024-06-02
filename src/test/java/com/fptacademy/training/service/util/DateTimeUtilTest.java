package com.fptacademy.training.service.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DateTimeUtilTest {

    @Test
    void testGetFirstDateOfCurrentWeekCase1() {
        LocalDate date = LocalDate.of(2023, 3, 28);
        LocalDate result = DateTimeUtil.getFirstDateOfCurrentWeek(date);
        assertEquals(27, result.getDayOfMonth());
    }

    @Test
    void testGetFirstDateOfCurrentWeekCase2() {
        LocalDate date = LocalDate.of(2023, 4, 1);
        LocalDate result = DateTimeUtil.getFirstDateOfCurrentWeek(date);
        assertEquals(27, result.getDayOfMonth());
    }

    @Test
    void testGetLastDateOfCurrentWeekCase1(){
        LocalDate date = LocalDate.of(2023, 3, 28);
        LocalDate result = DateTimeUtil.getLastDateOfCurrentWeek(date);
        assertEquals(2, result.getDayOfMonth());
    }

    @Test
    void testGetLastDateOfCurrentWeekCase2(){
        LocalDate date = LocalDate.of(2023, 4, 1);
        LocalDate result = DateTimeUtil.getLastDateOfCurrentWeek(date);
        assertEquals(2, result.getDayOfMonth());
    }


}
