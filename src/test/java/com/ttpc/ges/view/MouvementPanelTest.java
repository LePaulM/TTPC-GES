
package com.ttpc.ges.view;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;


import com.ttpc.ges.model.DatabaseManager;

import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.*;

public class MouvementPanelTest {

    @Test
    public void testMouvementPanelInitialization() {
    	DatabaseManager dbMock = mock(DatabaseManager.class);
        MouvementPanel panel = new MouvementPanel(dbMock);
        assertNotNull(panel);
        assertTrue(panel instanceof JPanel);
    }
}
