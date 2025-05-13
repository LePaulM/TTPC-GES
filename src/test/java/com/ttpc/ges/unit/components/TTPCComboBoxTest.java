
package com.ttpc.ges.unit.components;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.components.TTPCComboBox;

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
