package com.ttpc.ges.fonction.controller;

import com.ttpc.ges.controller.AnimalController;
import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.AnimalPanel;
import com.ttpc.ges.view.MouvementPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.awt.event.ActionEvent;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnimalControllerFonctionTest {

    private AnimalPanel mockAnimalPanel;
    private DatabaseManager mockDbManager;
    private MouvementPanel mockMouvementPanel;
    private AnimalController controller;

    @BeforeEach
    public void setUp() {       
    	mockDbManager = mock(DatabaseManager.class);
        mockAnimalPanel = mock(AnimalPanel.class);
        mockMouvementPanel = mock(MouvementPanel.class);

        // Simuler la liste d’animaux retournée par la base
        when(mockDbManager.getTousLesAnimaux()).thenReturn(Collections.emptyList());

        controller = new AnimalController(mockAnimalPanel, mockDbManager, mockMouvementPanel);
    }

    @Test
    public void testInitialisationChargeLesAnimaux() {
        verify(mockDbManager, times(1)).getTousLesAnimaux();
        verify(mockAnimalPanel, times(1)).updateAnimalTable(Collections.emptyList());
    }

    @Test
    public void testAjoutAnimalValideMetAJourTable() {
        // Créer un animal factice
        Animal animal = new Animal(0, "Vache", "Normande", "2022-01-01", 'F', 10, "La Habana", "Roux", false);

        when(mockAnimalPanel.getAnimalFormData()).thenReturn(animal);

        AnimalController.AddAnimalListener listener = controller.new AddAnimalListener();

        // Simuler un clic
        listener.actionPerformed(mock(ActionEvent.class));

        // Vérifie que l’animal est inséré
        verify(mockDbManager, times(1)).ajouterAnimal(animal);
        // Vérifie que la table est mise à jour après insertion
        verify(mockAnimalPanel, times(1)).updateAnimalTable(any());
    }

    @Test
    public void testAjoutAnimalNullNeFaitRien() {
        when(mockAnimalPanel.getAnimalFormData()).thenReturn(null);

        AnimalController.AddAnimalListener listener = controller.new AddAnimalListener();
        listener.actionPerformed(mock(ActionEvent.class));

        verify(mockDbManager, never()).ajouterAnimal(any());
        verify(mockAnimalPanel, never()).updateAnimalTable(any());
    }
}