package com.ttpc.ges.view;

import javax.swing.*;

import com.ttpc.ges.controller.AnimalController;
import com.ttpc.ges.controller.MouvementController;
import com.ttpc.ges.model.DatabaseManager;

import java.awt.*;
import java.net.URL;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private static AnimalPanel animalPanel;
    private static MouvementPanel mouvementPanel;
	private Image logo;

    public MainFrame() {
        super("TTPC-GES - Gestion des Entrees et Sorties");
        
        try {
            logo = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("logo_TENDRES_TRUFFES.png"));
            setIconImage(logo);  // Pour la barre des tâches et l'icône de la fenêtre
        } catch (Exception e) {
            System.err.println("[WARN] Icône non trouvée ou invalide.");
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setSize(1000, 600);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DatabaseManager dbManager = new DatabaseManager();

        animalPanel = new AnimalPanel(dbManager);
        mouvementPanel = new MouvementPanel(dbManager);

        // Controleurs
        new AnimalController(animalPanel, dbManager, mouvementPanel);
        new MouvementController(mouvementPanel, dbManager);

        // Onglets
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 40, 10, 40));
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Animaux", chargerIcone("animal.png"), animalPanel);
        tabbedPane.addTab("Mouvements", chargerIcone("mouvement.png"), mouvementPanel);


        add(tabbedPane, BorderLayout.CENTER);
        setExtendedState(Frame.MAXIMIZED_BOTH);


        setSize(new Dimension(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width,java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height));
    }
    
    private ImageIcon chargerIcone(String nomFichier) {
        URL url = getClass().getClassLoader().getResource("icons/" + nomFichier);
        System.out.println("URL icones : ");
        System.out.println(url);
        if (url != null) {
        	ImageIcon originalIcon = new ImageIcon(url);
        	Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH); // largeur x hauteur
        	ImageIcon resizedIcon = new ImageIcon(scaledImage);
            return resizedIcon;
        } else {
            System.err.println("Icône non trouvée : " + nomFichier);
            return null;
        }
    }
    
    public static AnimalPanel getAnimalPanelInstance() {
        return animalPanel;
    }

    public static MouvementPanel getMouvementPanelInstance() {
        return mouvementPanel;
    }

    public static void updateMouvementComboBox() {
        if (mouvementPanel != null) {
            mouvementPanel.rafraichirListeAnimaux();
        }
    }


}


