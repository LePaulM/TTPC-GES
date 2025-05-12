
package com.ttpc.ges.controller;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.AnimalPanel;
import com.ttpc.ges.view.MouvementPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class AnimalControllerTest {

    @Mock
    private AnimalPanel mockAnimalPanel;

    @Mock
    private DatabaseManager mockDatabaseManager;

    @Mock
    private MouvementPanel mockMouvementPanel;

    private AnimalController controller;

    @BeforeEach
    public void setup() {
        when(mockDatabaseManager.getTousLesAnimaux()).thenReturn(new ArrayList<>());
        controller = new AnimalController(mockAnimalPanel, mockDatabaseManager, mockMouvementPanel);
    }

    @Test
    public void testAddAnimalListenerAddsAnimalToDatabase() {
        Animal fakeAnimal = new Animal("A101", "Simba", "Lion", 'M', 3, "Savane", "Jeune lion", false);
        when(mockAnimalPanel.getAnimalFormData()).thenReturn(fakeAnimal);

        AnimalController.AddAnimalListener listener = controller.new AddAnimalListener();
        listener.actionPerformed(mock(ActionEvent.class));

        verify(mockDatabaseManager).ajouterAnimal(fakeAnimal);
    }

    @Test
    public void testConstructorInitializesPanel() {
        verify(mockAnimalPanel).updateAnimalTable(any());
    }
}
