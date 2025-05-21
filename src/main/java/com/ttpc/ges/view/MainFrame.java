package com.ttpc.ges.view;

import javax.swing.*;

import com.ttpc.ges.components.RoundedBorder;
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
            JOptionPane.showMessageDialog(null,
                "Une erreur est survenue : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
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
        UIManager.put("TabbedPane.tabInsets", new Insets(0,0,0,0));
        tabbedPane = new JTabbedPane();


        // Crée les composants d'onglet
        JLabel tabAnimaux = creerOnglet("Animaux", "animal.png");
        JLabel tabMouvements = creerOnglet("Mouvements", "mouvement.png");

        // Ajout des panels au tabbedPane
        tabbedPane.addTab(null, wrapWithTopMargin(animalPanel));
        tabbedPane.setTabComponentAt(0, tabAnimaux);

        tabbedPane.addTab(null, wrapWithTopMargin(mouvementPanel));
        tabbedPane.setTabComponentAt(1, tabMouvements);

        // Gestion du changement de sélection
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component comp = tabbedPane.getTabComponentAt(i);
                if (comp instanceof JLabel) {
                    comp.setBackground(i == selected ? new Color(100, 149, 237) : Color.WHITE);
                    comp.setForeground(i == selected ? Color.WHITE : Color.BLACK);
                }
            }
        });
        
        tabbedPane.setFocusable(false);       

        // Appliquer l'état sélectionné à l'onglet 0
        tabAnimaux.setBackground(new Color(100, 149, 237));
        tabAnimaux.setForeground(Color.WHITE);

        // Ajout au layout
        add(tabbedPane, BorderLayout.CENTER);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        setSize(new Dimension(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width,java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height));
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
    
    private JLabel creerOnglet(String texte, String iconePath) {
        URL iconURL = getClass().getClassLoader().getResource("icons/" + iconePath);
        ImageIcon icone = new ImageIcon(iconURL);
        Image img = icone.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon iconeRedim = new ImageIcon(img);

        JLabel label = new JLabel(texte, iconeRedim, SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        label.setPreferredSize(new Dimension(200, 60));
        label.setFocusable(false);
        return label;
    }

    public JPanel wrapWithTopMargin(JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 5px en haut
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }


}


