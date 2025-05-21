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
        UIManager.put("TabbedPane.tabInsets", new Insets(5,3,5,3));
        tabbedPane = new JTabbedPane();


        // Crée les composants d'onglet
        RoundedTabLabel tabAnimaux = new RoundedTabLabel("Animaux", "animal.png");
        RoundedTabLabel tabMouvements = new RoundedTabLabel("Mouvements", "mouvement.png");

        // Ajout des panels au tabbedPane
        tabbedPane.addTab(null, wrapWithTopMargin(animalPanel));
        tabbedPane.setTabComponentAt(0, tabAnimaux);

        tabbedPane.addTab(null, wrapWithTopMargin(mouvementPanel));
        tabbedPane.setTabComponentAt(1, tabMouvements);

        // Gestion du changement de sélection
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            tabAnimaux.setSelected(selected == 0);
            tabMouvements.setSelected(selected == 1);
        });
        
        tabbedPane.setFocusable(false);       

        // Appliquer l'état sélectionné à l'onglet 0
        tabAnimaux.setSelected(true);

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
        label.setBorder(new RoundedBorder(20, 15, Color.GRAY));
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
    
    class RoundedTabLabel extends JLabel {
        private boolean selected = false;
        private final int radius = 20;
        private final Color selectedColor = new Color(100, 149, 237);
        private final Color unselectedColor = new Color(230, 230, 230);

        public RoundedTabLabel(String text, String iconPath) {
            super(text);
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setHorizontalAlignment(CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setPreferredSize(new Dimension(200, 60));

            try {
                URL iconURL = getClass().getClassLoader().getResource("icons/" + iconPath);
                if (iconURL != null) {
                    ImageIcon icon = new ImageIcon(iconURL);
                    Image scaled = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(scaled));
                }
            } catch (Exception e) {
                System.err.println("[WARN] Icône non trouvée : " + iconPath);
            }
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            setForeground(selected ? Color.WHITE : Color.BLACK);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(selected ? selectedColor : unselectedColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }



}


