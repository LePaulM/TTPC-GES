package com.ttpc.ges.integration.controller;

import com.ttpc.ges.controller.AnimalController;
import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.AnimalPanel;
import com.ttpc.ges.view.MouvementPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.awt.event.ActionEvent;
import java.util.List;

import static org.mockito.Mockito.*;

public class AnimalControllerIntegrationTest {

    private AnimalPanel animalPanel;
    private DatabaseManager dbManager;
    private MouvementPanel mouvementPanel;
    private AnimalController controller;

    @BeforeEach
    void setUp() {
        animalPanel = mock(AnimalPanel.class);
        dbManager = mock(DatabaseManager.class);
        mouvementPanel = mock(MouvementPanel.class);

        when(dbManager.getTousLesAnimaux()).thenReturn(List.of());

        controller = new AnimalController(animalPanel, dbManager, mouvementPanel);

        verify(animalPanel).updateAnimalTable(List.of()); // Test du comportement initial
    }

    @Test
    void testAddAnimalListenerAddsAnimalAndRefreshesUI() {
    	Animal fakeAnimal = new Animal("A123", "Leo", "Lion", 'M', 5, "Savane", "Jeune lion dominant", false);
        when(animalPanel.getAnimalFormData()).thenReturn(fakeAnimal);
        when(dbManager.getTousLesAnimaux()).thenReturn(List.of(fakeAnimal));

        AnimalController.AddAnimalListener listener = controller.new AddAnimalListener();

        listener.actionPerformed(mock(ActionEvent.class));

        verify(dbManager).ajouterAnimal(fakeAnimal);
        verify(animalPanel).updateAnimalTable(List.of(fakeAnimal));
        verify(animalPanel).clearForm();
        verify(animalPanel).showMessage("Animal ajoute !");
        verify(mouvementPanel).rafraichirListeAnimaux();
    }

    @Test
    void testAddAnimalListenerWithNullAnimalDoesNothing() {
        when(animalPanel.getAnimalFormData()).thenReturn(null);

        AnimalController.AddAnimalListener listener = controller.new AddAnimalListener();
        listener.actionPerformed(mock(ActionEvent.class));

        verify(dbManager, never()).ajouterAnimal(any());
        verify(animalPanel, never()).updateAnimalTable(any());
        verify(animalPanel, never()).clearForm();
        verify(animalPanel, never()).showMessage(anyString());
        verify(mouvementPanel).rafraichirListeAnimaux(); // appelé même si animal est null
    }
}
