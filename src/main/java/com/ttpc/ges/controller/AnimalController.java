package com.ttpc.ges.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.AnimalPanel;
import com.ttpc.ges.view.MouvementPanel;

public class AnimalController {
    private final AnimalPanel animalPanel;
    private final DatabaseManager dbManager;
    private final MouvementPanel mouvementPanel;

    public AnimalController(AnimalPanel animalPanel, DatabaseManager dbManager, MouvementPanel mouvementPanel) {
        this.animalPanel = animalPanel;
        this.dbManager = dbManager;
        this.mouvementPanel = mouvementPanel;
        
        // On charge la table au demarrage
        animalPanel.updateAnimalTable(dbManager.getTousLesAnimaux());
    }

    class AddAnimalListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Animal a = animalPanel.getAnimalFormData();
            if (a != null) {
                dbManager.ajouterAnimal(a);
                animalPanel.updateAnimalTable(dbManager.getTousLesAnimaux());
                
                animalPanel.clearForm();
                animalPanel.showMessage("Animal ajoute !");
            }
            
            mouvementPanel.rafraichirListeAnimaux();
        }
    }
}
