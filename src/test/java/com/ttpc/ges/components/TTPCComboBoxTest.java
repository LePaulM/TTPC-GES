
package com.ttpc.ges.components;

import org.junit.jupiter.api.Test;
import javax.swing.JComboBox;

import static org.junit.jupiter.api.Assertions.*;

public class TTPCComboBoxTest {

    @Test
    public void testComboBoxCreation() {
        TTPCComboBox<String> comboBox = new TTPCComboBox<>();
        assertNotNull(comboBox);
        assertTrue(comboBox instanceof JComboBox);
    }
}
