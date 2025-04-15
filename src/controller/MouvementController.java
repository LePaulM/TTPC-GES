package controller;

import model.DatabaseManager;
import model.Mouvement;
import view.MouvementPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
