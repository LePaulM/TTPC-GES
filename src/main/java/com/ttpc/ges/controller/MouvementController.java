package com.ttpc.ges.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.view.MouvementPanel;

public class MouvementController {
    private final MouvementPanel mouvementPanel;
    private final DatabaseManager dbManager;

    public MouvementController(MouvementPanel mouvementPanel, DatabaseManager dbManager) {
        this.mouvementPanel = mouvementPanel;
        this.dbManager = dbManager;

        // Branche le bouton "Ajouter"
        mouvementPanel.addAddMouvementListener(new AddMouvementListener());

        // Charge les mouvements au lancement
        mouvementPanel.updateMouvementTable();
    }

    class AddMouvementListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Mouvement m = mouvementPanel.getMouvementFormData();
            if (m != null) {
                dbManager.ajouterMouvement(m);

                if ("sortie".equalsIgnoreCase(m.getTypeMouvement()) && mouvementPanel.isDecedeCoche()) {
                    dbManager.mettreAJourStatutDeces(m.getAnimalId(), true);
                }

                mouvementPanel.updateMouvementTable();
                mouvementPanel.clearForm();
                mouvementPanel.showMessage("Mouvement ajoute !");
            }
        }
    }
}
