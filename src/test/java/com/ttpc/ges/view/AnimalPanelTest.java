
package com.ttpc.ges.view;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.model.DatabaseManager;
import static org.mockito.Mockito.*;

import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalPanelTest {

    @Test
    public void testAnimalPanelInitialization() {
    	DatabaseManager dbMock = mock(DatabaseManager.class);
        AnimalPanel panel = new AnimalPanel(dbMock);
        assertNotNull(panel);
        assertTrue(panel instanceof JPanel);
    }
}
