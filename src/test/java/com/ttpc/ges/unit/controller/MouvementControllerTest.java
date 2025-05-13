package com.ttpc.ges.unit.controller;

import com.ttpc.ges.controller.MouvementController;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.view.MouvementPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MouvementControllerTest {

    @Mock
    private MouvementPanel mockMouvementPanel;

    @Mock
    private DatabaseManager mockDatabaseManager;

    private MouvementController controller;

    @BeforeEach
    public void setup() {
        controller = new MouvementController(mockMouvementPanel, mockDatabaseManager);
    }

    @Test
    public void testAddMouvementListenerAddsMouvementToDatabase() {
        Mouvement fakeMouvement = new Mouvement(1, "Transfert", Date.valueOf(LocalDate.now()), "Sanctuaire", false);
        when(mockMouvementPanel.getMouvementFormData()).thenReturn(fakeMouvement);

        controller.new AddMouvementListener().actionPerformed(mock(ActionEvent.class));

        verify(mockDatabaseManager).ajouterMouvement(fakeMouvement);
    }

    @Test
    public void testConstructorInitializesPanel() {
        verify(mockMouvementPanel).addAddMouvementListener(any());
        verify(mockMouvementPanel).updateMouvementTable();
    }
}
