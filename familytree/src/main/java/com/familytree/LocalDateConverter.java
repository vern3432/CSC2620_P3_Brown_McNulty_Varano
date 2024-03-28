package com.familytree;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateConverter {
    public static void main(String[] args) {
        String dateString1 = "2 8 2001";
        String dateString2 = "12 22 2001";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M d yyyy", Locale.ENGLISH);

        LocalDate date1 = LocalDate.parse(dateString1, formatter);
        LocalDate date2 = LocalDate.parse(dateString2, formatter);

        System.out.println("Date 1: " + date1);
        System.out.println("Date 2: " + date2);
    }
}
