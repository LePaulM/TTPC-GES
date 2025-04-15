import javax.swing.SwingUtilities;
import java.awt.SplashScreen;

import model.Animal;
import model.DatabaseManager;
import view.MainFrame;

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
