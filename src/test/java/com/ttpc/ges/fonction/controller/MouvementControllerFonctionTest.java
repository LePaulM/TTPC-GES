package com.ttpc.ges.fonction.controller;

import com.ttpc.ges.controller.MouvementController;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.utils.TTPCDateParser;
import com.ttpc.ges.view.MouvementPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.event.ActionEvent;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class MouvementControllerFonctionTest {

    private MouvementPanel mockMouvementPanel;
    private DatabaseManager mockDbManager;
    private MouvementController controller;

    @BeforeEach
    public void setUp() {
        mockMouvementPanel = mock(MouvementPanel.class);
        mockDbManager = mock(DatabaseManager.class);

        controller = new MouvementController(mockMouvementPanel, mockDbManager);
    }

    @Test
    public void testInitialisationChargeMouvementsEtBrancheListener() {
        // Vérifie que la table est chargée dès l'initialisation
        verify(mockMouvementPanel, times(1)).updateMouvementTable();

        // Vérifie que le listener a bien été ajouté
        verify(mockMouvementPanel, times(1)).addAddMouvementListener(any());
    }

    @Test
    public void testAjoutMouvementValide() {
        Mouvement mouvement = new Mouvement(0, 0, "entrée", TTPCDateParser.stringToSqlDate("22-03-2023"));

        when(mockMouvementPanel.getMouvementFormData()).thenReturn(mouvement);

        MouvementController.AddMouvementListener listener = controller.new AddMouvementListener();
        listener.actionPerformed(mock(ActionEvent.class));

        verify(mockDbManager, times(1)).ajouterMouvement(mouvement);
        verify(mockMouvementPanel, times(1)).updateMouvementTable();
    }

    @Test
    public void testAjoutMouvementNull() {
        when(mockMouvementPanel.getMouvementFormData()).thenReturn(null);

        MouvementController.AddMouvementListener listener = controller.new AddMouvementListener();
        listener.actionPerformed(mock(ActionEvent.class));

        verify(mockDbManager, never()).ajouterMouvement(any());
        verify(mockMouvementPanel, never()).updateMouvementTable();
    }
}
