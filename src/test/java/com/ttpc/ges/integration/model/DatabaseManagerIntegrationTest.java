package com.ttpc.ges.integration.model;

import org.junit.jupiter.api.*;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.utils.TTPCDateParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseManagerIntegrationTest {

    private static DatabaseManager dbManager;

    @BeforeAll
    static void setup() {
        dbManager = new DatabaseManager();
    }

    @Test
    @Order(1)
    void testAjouterEtRecupererAnimal() {
    	Animal a = new Animal("A123", "Leo", "Lion", 'M', 5, "Savane", "Jeune lion dominant", false);
        a.setNom("TestAnimal");
        a.setSexe('F');
        a.setEspece("bovin");

        dbManager.ajouterAnimal(a);

        List<Animal> animaux = dbManager.getTousLesAnimaux();
        assertTrue(animaux.stream().anyMatch(animal -> "TestAnimal".equals(animal.getNom())));
    }

    @Test
    @Order(2)
    void testAjoutMouvementEtStatutDeces() {
        List<Animal> animaux = dbManager.getTousLesAnimaux();
        assertFalse(animaux.isEmpty(), "La base devrait contenir au moins un animal.");

        int animalId = animaux.get(0).getId();

        Mouvement m = new Mouvement(0, 0, null, TTPCDateParser.stringToSqlDate("22-03-2023"));
        m.setAnimalId(animalId);
        m.setTypeMouvement("sortie");
        m.setDateMouvement("2025-05-01");
        m.setDestination("Vente");

        dbManager.ajouterMouvement(m);
        dbManager.mettreAJourStatutDeces(animalId, true);

        // Pour aller plus loin : récupérer l'animal et vérifier son statut
        Animal updated = dbManager.getTousLesAnimaux().stream()
                .filter(animal -> animal.getId() == animalId)
                .findFirst()
                .orElse(null);

        assertNotNull(updated);
        assertTrue(updated.isDecede(), "Le statut décédé devrait être mis à jour.");
    }
}
