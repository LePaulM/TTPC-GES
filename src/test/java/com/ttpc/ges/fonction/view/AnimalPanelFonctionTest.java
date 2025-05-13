package com.ttpc.ges.fonction.view;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.AnimalPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnimalPanelFonctionTest {

    private AnimalPanel animalPanel;
    private DatabaseManager dbMock;

    @BeforeEach
    public void setUp() {
        dbMock = mock(DatabaseManager.class);
        SwingUtilities.invokeLater(() -> animalPanel = new AnimalPanel(dbMock));
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }

    @Test
    public void testGetAnimalFormData_withValidFields_shouldReturnAnimal() {
        SwingUtilities.invokeLater(() -> {
            animalPanel.getNomField().setText("Bella");
            animalPanel.getEspeceField().setText("Charolaise");
            animalPanel.getProvenanceField().setText("Ferme A");
            animalPanel.getNumeroIdField().setText("A001");
            animalPanel.getAgeSpinner().setValue(4);
            animalPanel.getSexeBox().setSelectedItem("F");
            animalPanel.getDescriptionArea().setText("Jeune vache docile");

            Animal a = animalPanel.getAnimalFormData();

            assertNotNull(a);
            assertEquals("Bella", a.getNom());
            assertEquals("Charolaise", a.getEspece());
            assertEquals('F', a.getSexe());
            assertEquals(4, a.getAge());
            assertEquals("Ferme A", a.getProvenance());
            assertEquals("A001", a.getNumeroId());
        });
    }

    @Test
    public void testUpdateAnimalTable_shouldDisplayDataInTable() {
        List<Animal> animaux = List.of(
            new Animal("A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Docile", false),
            new Animal("A002", "Leo", "Limousine", 'M', 2, "Ferme B", "Énergique", false)
        );

        SwingUtilities.invokeLater(() -> {
            animalPanel.updateAnimalTable(animaux);
            JTable table = animalPanel.getTable();  // méthode d’accès possible ou public
            assertEquals(2, table.getRowCount());
        });
    }
}
