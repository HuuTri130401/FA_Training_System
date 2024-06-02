package com.fptacademy.training.service.util;

import java.time.LocalDate;

public class RandomUtil {
    static public Integer randomInt(Integer min, Integer max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    static public String randomString(Integer length, boolean space) {
        String elements = "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" 
            + "0123456789";
        if (space)
            elements += " ";
        String str = "";
        for (int i = 0; i < length; i++) {
            char c;
            if (i == 0)
                c = elements.charAt(randomInt(0, 51)); 
            else if (i == length - 1)
                c = elements.charAt(randomInt(0, 61));
            else 
                c = elements.charAt(randomInt(0, elements.length() - 1));
            str += c;
        }
        return str;
    }

    static public Boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    static public LocalDate randomDate() {
        int year = randomInt(1900, 2020);
        int month = randomInt(1, 12);
        int day = randomInt(1, 28);
        return LocalDate.of(year, month, day);
    }
}
