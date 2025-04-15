package view;

import javax.swing.*;

import controller.AnimalController;
import controller.MouvementController;
import model.DatabaseManager;

import java.awt.*;
import java.net.URL;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private AnimalPanel animalPanel;
    private MouvementPanel mouvementPanel;
	private Image logo;

    public MainFrame() {
        super("TTPC-GES - Gestion des Entrees et Sorties");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		// Icon
		URL url = ClassLoader.getSystemResource("/logo_TENDRES_TRUFFES.png");
		if (url != null) {
		    Toolkit kit = Toolkit.getDefaultToolkit();
		    logo = kit.createImage(url);
		    setIconImage(logo);
		} else {
		    System.err.println("Logo introuvable !");
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
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Animaux", animalPanel);
        tabbedPane.addTab("Mouvements", mouvementPanel);

        add(tabbedPane, BorderLayout.CENTER);
        setExtendedState(Frame.MAXIMIZED_BOTH);


        setSize(new Dimension(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width,java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height));
    }
}


