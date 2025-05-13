package com.ttpc.ges.fonction.view;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.view.MouvementPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MouvementPanelFonctionTest {

    private MouvementPanel mouvementPanel;
    private DatabaseManager dbMock;

    @BeforeEach
    public void setUp() {
        dbMock = mock(DatabaseManager.class);
        SwingUtilities.invokeLater(() -> mouvementPanel = new MouvementPanel(dbMock));
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }

    @Test
    public void testGetMouvementFormData_retourneMouvementCorrect() {
        SwingUtilities.invokeLater(() -> {
            // Créer un animal de test
            Animal animal = new Animal("A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Docile", false);
            mouvementPanel.getAnimalComboBox().setSelectedItem(animal);

            mouvementPanel.getTypeBox().setSelectedItem("Sortie");
            mouvementPanel.getDateField().setText("2024-05-12");
            mouvementPanel.getDecedeCheckBox().setSelected(true);

            Mouvement m = mouvementPanel.getMouvementFormData();

            assertNotNull(m);
            assertEquals("Sortie", m.getTypeMouvement());
            assertEquals(Date.valueOf("2024-05-12"), m.getDateMouvement());
            assertEquals(animal.getId(), m.getAnimalId());  // Vérifie que l'ID est repris
            assertTrue(m.isDecede());
        });
    }

    @Test
    public void testUpdateMouvementTable_afficheDonneesDansLaTable() {
        Mouvement m1 = new Mouvement(1, 1, "Sortie", Date.valueOf("2024-01-01"), false, "Ferme B");
        Mouvement m2 = new Mouvement(2, 2, "Entrée", Date.valueOf("2024-02-01"), false, "Ferme A");

        List<Mouvement> mouvements = List.of(m1, m2);

        SwingUtilities.invokeLater(() -> {
            mouvementPanel.updateMouvementTable();
            JTable table = mouvementPanel.getTable(); // méthode à exposer si nécessaire
            assertEquals(2, table.getRowCount());
        });
    }
}
