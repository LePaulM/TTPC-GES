
package com.ttpc.ges.unit.components;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.components.TTPCButton;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.*;

public class TTPCButtonTest {

    @Test
    public void testButtonCreation() {
        TTPCButton button = new TTPCButton("Valider");
        assertNotNull(button);
        assertTrue(button instanceof JButton);
        assertTrue(button.getText().contains("Valider"));
    }
}
