package com.ttpc.ges.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TTPCDateParser {

    private static final SimpleDateFormat FORMAT_JOUR = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd");

    // Convertit une date SQL (java.sql.Date) vers String format affiché
    public static String sqlDateToDisplay(Date sqlDate) {
        if (sqlDate == null) return "";
        return FORMAT_JOUR.format(sqlDate);
    }

    // Convertit une date SQL vers String format SQL (utile pour debug ou logs)
    public static String sqlDateToString(Date sqlDate) {
        if (sqlDate == null) return "";
        return FORMAT_SQL.format(sqlDate);
    }

    // Convertit une String ("dd/MM/yyyy") en java.sql.Date
    public static Date stringToSqlDate(String input) {
        if (input == null || input.trim().isEmpty()) return null;

        String[] formats = {"dd/MM/yyyy", "yyyy-MM-dd"};
        for (String pattern : formats) {
            try {
                java.util.Date parsed = new SimpleDateFormat(pattern).parse(input.trim());
                return new Date(parsed.getTime());
            } catch (Exception e) {
                // ignore and try next
            }
        }

        return null;
    }
}
