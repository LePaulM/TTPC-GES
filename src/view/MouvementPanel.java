package view;

import model.Animal;
import model.DatabaseManager;
import model.Mouvement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MouvementPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JComboBox<Animal> animalComboBox;
    private JComboBox<String> typeBox;
    private JCheckBox decedeCheckBox;
    private JTextField dateField;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Animal> animaux;
    private JTextField searchField;
    private JPanel formWrapper;
    private JButton toggleFormButton;
    private JSplitPane splitPane;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public MouvementPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());

        // === Formulaire ===
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un mouvement"));
        formPanel.setPreferredSize(new Dimension(350, 200));

        animalComboBox = new JComboBox<>();
        typeBox = new JComboBox<>();
        decedeCheckBox = new JCheckBox("Décédé");
        decedeCheckBox.setVisible(false);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholder("  ");
            dateField = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            dateField = new JFormattedTextField();
        }

        //  Préremplir avec la date du jour
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateField.setText(sdf.format(new java.util.Date()));


        formPanel.add(new JLabel("Animal :")); formPanel.add(animalComboBox);
        formPanel.add(new JLabel("Type :")); formPanel.add(typeBox);
        formPanel.add(new JLabel("Date (JJ/MM/AAAA) :")); formPanel.add(dateField);
        formPanel.add(new JLabel("")); formPanel.add(decedeCheckBox);

        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier la sélection");
        deleteButton = new JButton("Supprimer la sélection");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.add(formPanel);
        formWrapper.add(buttonPanel);

        // === Table ===
        String[] colonnes = {
                "Nom", "Espèce", "Sexe", "ID", "Âge",
                "Date Entrée", "Provenance",
                "Date Sortie", "Destination", "Décédé"
            };

        tableModel = new DefaultTableModel(null, colonnes);

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        searchField = new JTextField();
        toggleFormButton = new JButton("▼ Cacher le formulaire");
        toggleFormButton.addActionListener(e -> {
            boolean visible = formWrapper.isVisible();
            formWrapper.setVisible(!visible);
            toggleFormButton.setText(visible ? "▶ Afficher le formulaire" : "▼ Cacher le formulaire");
            SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(visible ? 0.0 : 0.3));
        });

        JPanel topTableBar = new JPanel(new BorderLayout());
        topTableBar.add(toggleFormButton, BorderLayout.WEST);
        topTableBar.add(searchField, BorderLayout.CENTER);

        JPanel tableButtonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton importButton = new JButton("Importer CSV");
        JButton exportButton = new JButton("Exporter CSV");
        tableButtonBar.add(importButton);
        tableButtonBar.add(exportButton);

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.add(topTableBar, BorderLayout.NORTH);
        tableArea.add(scroll, BorderLayout.CENTER);
        tableArea.add(tableButtonBar, BorderLayout.SOUTH);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrapper, tableArea);
        splitPane.setResizeWeight(0.3);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);

        // === Listeners ===
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        addButton.addActionListener(e -> ajouterMouvement());
        editButton.addActionListener(e -> modifierSelection());
        deleteButton.addActionListener(e -> supprimerSelection());

        // === Init données ===
        rafraichirListeAnimaux();
        updateMouvementTable();
    }
    
    public static Date parseDateJJMMAAAA(String input) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // stricte : 32/13/2025 sera invalide
        java.util.Date parsed = sdf.parse(input);
        return new Date(parsed.getTime()); // java.sql.Date
    }



    private void applyFilter() {
        String txt = searchField.getText();
        sorter.setRowFilter(txt.isEmpty() ? null : RowFilter.regexFilter("(?i)" + txt));
    }
    
    private void supprimerSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Sélectionnez un mouvement à supprimer.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment supprimer ce mouvement ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dbManager.deleteMouvement(id);
            updateMouvementTable();
            showMessage("Mouvement supprimé.");
        }
    }


    public void rafraichirListeAnimaux() {
        animaux = dbManager.getTousLesAnimaux();
        animalComboBox.removeAllItems();
        for (Animal a : animaux) {
            animalComboBox.addItem(a); // on ajoute l'objet Animal directement
        }

        if (!animaux.isEmpty()) {
            animalComboBox.setSelectedIndex(0);
            updateTypeOptions();
        }

        animalComboBox.addItemListener(e -> updateTypeOptions());
    }


    private void updateTypeOptions() {
    	//System.out.println("UpdateTypeOptions");
        typeBox.removeAllItems();
        //System.out.println("selected item : " + animalComboBox.getSelectedItem());
        //tring nom = (String) animalComboBox.getSelectedItem().toString();
        Animal a = (Animal) animalComboBox.getSelectedItem();
        if (a == null) return;

        List<Mouvement> mouvements = dbManager.getMouvementsParAnimal(a.getId());
        // System.out.println("mouvements : " + mouvements);
        if (mouvements.isEmpty() || !"entrée".equalsIgnoreCase(mouvements.get(mouvements.size() - 1).getTypeMouvement())) {
            typeBox.addItem("entrée");
        } else {
            typeBox.addItem("sortie");
        }

        decedeCheckBox.setVisible("sortie".equalsIgnoreCase((String) typeBox.getSelectedItem()));
    }

    private void ajouterMouvement() {
        // System.out.println("ajouterMouvement");
    	Animal animal = (Animal) animalComboBox.getSelectedItem(); // cast direct
        // System.out.println(animal);
        if (animal == null) return;

        java.sql.Date date = parseDateSafely(dateField.getText().trim());
        if (date == null) {
            showMessage("Format de date invalide.");
            return;
        }

        String type = (String) typeBox.getSelectedItem();
        String destination = JOptionPane.showInputDialog(this,
            type.equals("entrée") ? "Provenance de l’animal :" : "Destination de l’animal :",animal.getProvenance());

        if (destination == null || destination.trim().isEmpty()) {
            showMessage("Champ de provenance/destination requis.");
            return;
        }

        Mouvement m = new Mouvement(animal.getId(), type, date, destination, decedeCheckBox.isSelected());
        dbManager.ajouterMouvement(m);

        if ("sortie".equalsIgnoreCase(type) && decedeCheckBox.isVisible() && decedeCheckBox.isSelected()) {
            dbManager.mettreAJourStatutDeces(animal.getId(), true);
        }

        updateMouvementTable();
        clearForm();
        rafraichirListeAnimaux();
        showMessage("Mouvement ajouté !");
    }


    public Mouvement getMouvementFormData() {
        // méthode exposée pour le controller si besoin
        return null; // à implémenter selon l'usage
    }

    public boolean isDecedeCoche() {
        return decedeCheckBox.isVisible() && decedeCheckBox.isSelected();
    }

    public void updateMouvementTable() {
    	// System.out.println("UpdateMouvementTable" );
        List<Mouvement> mouvements = dbManager.getTousLesMouvements();
        String[] colonnes = {
            "Nom", "Espèce", "Sexe", "ID", "Âge",
            "Date Entrée", "Provenance",
            "Date Sortie", "Destination", "Décédé"
        };
        tableModel.setDataVector(new Object[0][0], colonnes);
        // System.out.println("Mouvements récupérés : " + mouvements.size());

        for (Mouvement m : mouvements) {
        	// System.out.println(">> MOUVEMENT : ID=" + m.getId() + ", animalId=" + m.getAnimalId() + ", type=" + m.getTypeMouvement());

            Animal a = dbManager.getAnimalById(m.getAnimalId());
            if (a != null) {

                tableModel.addRow(new Object[]{
            	    a.getNom(),
            	    a.getEspece(),
            	    a.getSexe(),
            	    a.getNumeroId(),
            	    a.getAge(),
            	    m.getTypeMouvement().equalsIgnoreCase("entrée") ? m.getDateMouvement().toString() : "",
            	    m.getTypeMouvement().equalsIgnoreCase("entrée") ? m.getDestination() : "", // provenance
            	    m.getTypeMouvement().equalsIgnoreCase("sortie") ? m.getDateMouvement().toString() : "",
            	    m.getTypeMouvement().equalsIgnoreCase("sortie") ? m.getDestination() : "",
            	    m.getTypeMouvement().equalsIgnoreCase("sortie") && m.isDecede() ? "Oui" : "Non"
            	});

            } else {
            	// System.out.println("⚠️ Aucun animal trouvé pour mouvement ID=" + m.getId() + ", animalId=" + m.getAnimalId());
            	continue;

            }
        }

        table.setModel(tableModel);
    }

    public void clearForm() {
        animalComboBox.setSelectedIndex(0);
        dateField.setText("JJ-MM-AAAA");
        decedeCheckBox.setSelected(false);
        decedeCheckBox.setVisible(false);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void addAddMouvementListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
    
    private void modifierSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Sélectionnez un mouvement.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        int animalId = (int) tableModel.getValueAt(modelRow, 1);
        String type = (String) tableModel.getValueAt(modelRow, 3);
        String dateStr = tableModel.getValueAt(modelRow, 4).toString();
        boolean decede = "Oui".equals(tableModel.getValueAt(modelRow, 5));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<String> typeMod = new JComboBox<>(new String[]{"entrée", "sortie"});
        typeMod.setSelectedItem(type);
        JCheckBox decedeMod = new JCheckBox("Décédé");
        decedeMod.setVisible("sortie".equalsIgnoreCase(type));
        decedeMod.setSelected(decede);

        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            JFormattedTextField dateMod = new JFormattedTextField(mask);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(dateStr);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateMod.setText(displayFormat.format(utilDate));

            typeMod.addItemListener(e -> decedeMod.setVisible("sortie".equalsIgnoreCase((String) typeMod.getSelectedItem())));

            panel.add(new JLabel("Type :")); panel.add(typeMod);
            panel.add(new JLabel("Date (JJ/MM/AAAA) :")); panel.add(dateMod);
            panel.add(new JLabel("")); panel.add(decedeMod);

            int res = JOptionPane.showConfirmDialog(this, panel, "Modifier Mouvement", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String newType = (String) typeMod.getSelectedItem();
                java.util.Date parsed;
                try {
                    parsed = new SimpleDateFormat("dd/MM/yyyy").parse(dateMod.getText());
                } catch (Exception ex) {
                    showMessage("Format de date invalide. Utilisez JJ/MM/AAAA");
                    return;
                }
                Date newDate = new Date(parsed.getTime());

                Mouvement m = new Mouvement(id, animalId, newType, newDate);
                dbManager.updateMouvement(m);

                if ("sortie".equalsIgnoreCase(newType)) {
                    dbManager.mettreAJourStatutDeces(animalId, decedeMod.isSelected());
                }

                updateMouvementTable();
                showMessage("Mouvement mis à jour !");
            }
        } catch (Exception e) {
            showMessage("Erreur de parsing de la date existante : " + e.getMessage());
        }
    }
    
    public static java.sql.Date parseDateSafely(String input) {
        if (input == null || input.trim().isEmpty()) return null;

        input = input.trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            // Si c’est une vraie date JJ/MM/AAAA
            java.util.Date parsed =  sdf.parse(input);
            return new java.sql.Date(parsed.getTime());

        } catch (ParseException e) {
            try {
                // Sinon, essaie de le parser comme un timestamp
                long timestamp = Long.parseLong(input);
                return new java.sql.Date(timestamp);
            } catch (NumberFormatException ex) {
                System.out.println("Erreur parsing date : \"" + input + "\" n'est ni une date ni un timestamp.");
                return null;
            }
        }
    }


}

