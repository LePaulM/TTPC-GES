package com.ttpc.ges;

import javax.swing.SwingUtilities;

import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.MainFrame;

import java.awt.SplashScreen;

public class Main {
    public static void main(String[] args) {
    	System.out.println("TTPC-GES - Application démarre...");
        // Affiche le splash s'il est defini
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            try {
                Thread.sleep(2000); // garde le splash 2 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    	
        SwingUtilities.invokeLater(() -> {
            DatabaseManager db = new DatabaseManager();
            MainFrame app = new MainFrame();
            db.verifierBase(); // Lancement du diagnostic
            
            app.setVisible(true);
        });
    }
}
