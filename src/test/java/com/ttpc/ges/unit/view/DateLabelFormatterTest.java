
package com.ttpc.ges.unit.view;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.view.DateLabelFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class DateLabelFormatterTest {

    @Test
    public void testDateLabelFormatterCreation() {
        DateLabelFormatter formatter = new DateLabelFormatter();
        assertNotNull(formatter);
    }
}
