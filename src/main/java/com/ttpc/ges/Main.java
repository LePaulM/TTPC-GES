package com.ttpc.ges;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.view.MainFrame;

import java.awt.SplashScreen;

public class Main {
	static boolean launched = false;
	
    public static void main(String[] args) {
    	System.out.println("TTPC-GES - Application démarre...");

        // Affiche le splash s'il est defini
    	SplashScreen splash = SplashScreen.getSplashScreen();
    	

    	if (splash != null) {
    	    Timer timer = new Timer(2000, e -> {
    	        if (!launched) {
    	            launched = true;
    	            lancerApp();
    	        }
    	    });
    	    timer.setRepeats(false);
    	    timer.start();
    	} else {
    	    if (!launched) {
    	        launched = true;
    	        lancerApp();
    	    }
    	}

    	
        SwingUtilities.invokeLater(() -> {
            DatabaseManager db = new DatabaseManager();
            MainFrame app = new MainFrame();
            db.verifierBase(); // Lancement du diagnostic
        });
    }
    
	private static void lancerApp() {
	    DatabaseManager db = new DatabaseManager();
	    MainFrame app = new MainFrame();
	    db.verifierBase();
	    app.setVisible(true);
	}
}
