
package com.ttpc.ges.utils;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TTPCDateParserTest {

    @Test
    public void testSqlDateToDisplay_withValidDate() {
        Date sqlDate = Date.valueOf("2024-05-01");
        String formatted = TTPCDateParser.sqlDateToDisplay(sqlDate);
        assertEquals("01/05/2024", formatted);
    }

    @Test
    public void testSqlDateToDisplay_withNull() {
        assertEquals("", TTPCDateParser.sqlDateToDisplay(null));
    }

    @Test
    public void testSqlDateToString_withValidDate() {
        Date sqlDate = Date.valueOf("2024-05-01");
        String formatted = TTPCDateParser.sqlDateToString(sqlDate);
        assertEquals("2024-05-01", formatted);
    }

    @Test
    public void testSqlDateToString_withNull() {
        assertEquals("", TTPCDateParser.sqlDateToString(null));
    }

    @Test
    public void testStringToSqlDate_ddMMyyyy() {
        Date result = TTPCDateParser.stringToSqlDate("01/05/2024");
        assertEquals(Date.valueOf("2024-05-01"), result);
    }

    @Test
    public void testStringToSqlDate_yyyyMMdd() {
        Date result = TTPCDateParser.stringToSqlDate("2024-05-01");
        assertEquals(Date.valueOf("2024-05-01"), result);
    }

    @Test
    public void testStringToSqlDate_invalidInput() {
        assertNull(TTPCDateParser.stringToSqlDate("invalid-date"));
    }

    @Test
    public void testStringToSqlDate_nullInput() {
        assertNull(TTPCDateParser.stringToSqlDate(null));
    }

    @Test
    public void testStringToSqlDate_emptyInput() {
        assertNull(TTPCDateParser.stringToSqlDate(" "));
    }
}
