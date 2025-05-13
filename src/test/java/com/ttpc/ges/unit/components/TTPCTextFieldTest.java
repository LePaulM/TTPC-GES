
package com.ttpc.ges.unit.components;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.components.TTPCTextField;

import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.*;

public class TTPCTextFieldTest {

    @Test
    public void testTextFieldCreation() {
        TTPCTextField textField = new TTPCTextField();
        assertNotNull(textField);
        assertTrue(textField instanceof JTextField);
    }
}
