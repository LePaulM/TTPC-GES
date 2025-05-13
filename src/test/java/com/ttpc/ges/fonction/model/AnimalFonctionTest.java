package com.ttpc.ges.fonction.model;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalFonctionTest {
	@Test
    public void testCreationEtAccesseursAnimal() {
        Animal animal = new Animal("A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Jeune vache", false);

        assertEquals("A001", animal.getNumeroId());
        assertEquals("Bella", animal.getNom());
        assertEquals("Charolaise", animal.getEspece());
        assertEquals('F', animal.getSexe());
        assertEquals(3, animal.getAge());
        assertEquals("Ferme A", animal.getProvenance());
        assertEquals("Jeune vache", animal.getDescription());
        assertFalse(animal.isDecede());
    }

    @Test
    public void testAnimalIsPresentDelegatesToDbManager() {
        Animal animal = new Animal(10, "A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Jeune vache", false);
        DatabaseManager dbMock = org.mockito.Mockito.mock(DatabaseManager.class);
        org.mockito.Mockito.when(dbMock.estPresent(10)).thenReturn(true);

        assertTrue(animal.isPresent(dbMock));
        org.mockito.Mockito.verify(dbMock).estPresent(10);
    }
}
