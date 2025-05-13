
package com.ttpc.ges.unit.model;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.model.Mouvement;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MouvementTest {

    @Test
    public void testMouvementFullConstructor() {
        Date date = Date.valueOf(LocalDate.of(2024, 5, 10));
        Mouvement m = new Mouvement(1, "Transfert", date, "Zoo Lyon", true);

        assertEquals(1, m.getAnimalId());
        assertEquals("Transfert", m.getTypeMouvement());
        assertEquals(date, m.getDateMouvement());
        m.setDestination("Zoo Lyon");
        assertEquals("Zoo Lyon", m.getDestination());
        assertTrue(m.isDecede());
    }

    @Test
    public void testMouvementPartialConstructor() {
        Date date = Date.valueOf(LocalDate.of(2024, 5, 10));
        Mouvement m = new Mouvement(2, "Adoption", date);

        assertEquals(2, m.getAnimalId());
        assertEquals("Adoption", m.getTypeMouvement());
        assertEquals(date, m.getDateMouvement());
        assertEquals("", m.getDestination());
        assertFalse(m.isDecede());
    }

    @Test
    public void testAllSettersAndGetters() {
        Mouvement m = new Mouvement(3, "Observation", Date.valueOf("2024-04-01"));

        m.setId(77);
        m.setAnimalId(8);
        m.setTypeMouvement("Soins");
        m.setDateMouvement(Date.valueOf("2024-04-15"));
        m.setDestination("Infirmerie");
        m.setDecede(true);

        assertEquals(77, m.getId());
        assertEquals(8, m.getAnimalId());
        assertEquals("Soins", m.getTypeMouvement());
        assertEquals(Date.valueOf("2024-04-15"), m.getDateMouvement());
        assertEquals("Infirmerie", m.getDestination());
        assertTrue(m.isDecede());
    }
}
