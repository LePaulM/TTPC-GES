package com.ttpc.ges.fonction.model;

import org.junit.jupiter.api.*;

import com.ttpc.ges.utils.TTPCDateParser;
import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;

import java.io.File;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerFonctionTest {

    private static final String TEST_DB_PATH = System.getProperty("user.home") + "/TTPC-GES/test_ges.db";
    private DatabaseManager dbManager;

    @BeforeEach
    public void setUp() {
        // Supprime la base de test précédente si elle existe
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        // Instanciation va déclencher la création de la DB
        dbManager = new DatabaseManager();
    }

    @Test
    public void testInsertionEtRécupérationAnimal() {
    	Animal animal = new Animal("A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Jeune vache", false);
        dbManager.ajouterAnimal(animal);

        List<Animal> animaux = dbManager.getTousLesAnimaux();
        assertFalse(animaux.isEmpty(), "La liste d'animaux ne devrait pas être vide après insertion");

        Animal a = animaux.get(0);
        assertEquals("A001", a.getId());
        assertEquals("Bovin", a.getEspece());
    }

    @Test
    public void testInsertionEtRécupérationMouvement() {
    	Animal animal = new Animal("A001", "Bella", "Charolaise", 'F', 3, "Ferme A", "Jeune vache", false);
        Mouvement mouvement = new Mouvement(0, "entrée", TTPCDateParser.stringToSqlDate("22-03-2023"));
        dbManager.ajouterMouvement(mouvement);

        // Test minimal : vérifier que l’appel ne lève pas d'exception
        assertDoesNotThrow(() -> dbManager.ajouterMouvement(mouvement));
    }

    @AfterEach
    public void tearDown() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }
}
