
package com.ttpc.ges.components;

import org.junit.jupiter.api.Test;
import javax.swing.JFormattedTextField;

import static org.junit.jupiter.api.Assertions.*;

public class TTPCFormattedTextFieldTest {

    @Test
    public void testFormattedTextFieldCreation() {
        TTPCFormattedTextField field = new TTPCFormattedTextField();
        assertNotNull(field);
        assertTrue(field instanceof JFormattedTextField);
    }
}
