package com.ttpc.ges.fonction.model;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.utils.TTPCDateParser;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MouvementFonctionTest {

    @Test
    public void testCreationMouvementValide() {
        int animalId = 1;
        String type = "Sortie";
        Date date = Date.valueOf(LocalDate.of(2024, 5, 1));
        String destination = "Ferme B";

        Mouvement m = new Mouvement(animalId, type, date);
        m.setDestination(destination);
        m.setDecede(false);

        assertEquals(animalId, m.getAnimalId());
        assertEquals(type, m.getTypeMouvement());
        assertEquals(date, m.getDateMouvement());
        assertEquals(destination, m.getDestination());
        assertFalse(m.isDecede());
    }

    @Test
    public void testSetDateFromString() {
        Mouvement m = new Mouvement(0, 0, "entrée", TTPCDateParser.stringToSqlDate("22-03-2023"));
        m.setDateMouvement("2024-05-12");

        assertNotNull(m.getDateMouvement());
        assertEquals(Date.valueOf("2024-05-12"), m.getDateMouvement());
    }

    @Test
    public void testToStringGeneratesExpectedOutput() {
        Mouvement m = new Mouvement(2, 1, "Entrée", Date.valueOf("2024-01-01"), false, "N/A");
        String result = m.toString();
        assertTrue(result.contains("animalId=1"));
        assertTrue(result.contains("typeMouvement=Entrée"));
    }
}
