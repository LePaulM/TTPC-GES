
package com.ttpc.ges.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {

    @Test
    public void testAnimalCreationWithoutId() {
        Animal animal = new Animal("A123", "Leo", "Lion", 'M', 5, "Savane", "Jeune lion dominant", false);

        assertEquals("A123", animal.getNumeroId());
        assertEquals("Leo", animal.getNom());
        assertEquals("Lion", animal.getEspece());
        assertEquals('M', animal.getSexe());
        assertEquals(5, animal.getAge());
        assertEquals("Savane", animal.getProvenance());
        assertEquals("Jeune lion dominant", animal.getDescription());
        assertFalse(animal.isDecede());
    }

    @Test
    public void testAnimalCreationWithId() {
        Animal animal = new Animal(1, "Mimi", "Chat", 'F', 3, "Refuge", "Chatte calme", true);

        assertEquals(1, animal.getId());
        assertEquals("Mimi", animal.getNom());
        assertEquals("Chat", animal.getEspece());
        assertEquals('F', animal.getSexe());
        assertEquals(3, animal.getAge());
        assertEquals("Refuge", animal.getProvenance());
        assertEquals("Chatte calme", animal.getDescription());
        assertTrue(animal.isDecede());
    }

    @Test
    public void testAllSettersAndGetters() {
        Animal animal = new Animal("X000", "?", "?", '?', 0, "?", "?", false);

        animal.setId(99);
        animal.setNumeroId("B456");
        animal.setNom("Zazu");
        animal.setEspece("Oiseau");
        animal.setSexe('M');
        animal.setAge(2);
        animal.setProvenance("Forêt");
        animal.setDescription("Messager royal");
        animal.setDecede(true);

        assertEquals(99, animal.getId());
        assertEquals("B456", animal.getNumeroId());
        assertEquals("Zazu", animal.getNom());
        assertEquals("Oiseau", animal.getEspece());
        assertEquals('M', animal.getSexe());
        assertEquals(2, animal.getAge());
        assertEquals("Forêt", animal.getProvenance());
        assertEquals("Messager royal", animal.getDescription());
        assertTrue(animal.isDecede());
    }
}
