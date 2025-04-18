package com.ttpc.ges.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MouvementPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JComboBox<Animal> animalComboBox;
    private JComboBox<String> typeBox;
    private JCheckBox decedeCheckBox;
    private JTextField dateField,searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Animal> animaux;
    private JPanel formWrapper;
    private JButton toggleFormButton,editButton, addButton, deleteButton,importButton,exportButton, voirMouvementsButton;
    private JSplitPane splitPane;
    private Color blueColor = new Color(188,218,244);

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
        voirMouvementsButton = new JButton("Voir mouvements");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(voirMouvementsButton);

        formPanel.setBackground(blueColor);
        buttonPanel.setBackground(blueColor);
        
        formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.add(formPanel);
        formWrapper.add(buttonPanel);

        // === Table ===
        String[] colonnes = {
        	    "ID", // important pour la suppression
        	    "Nom", "Espèce", "Sexe", "ID Animal", "Âge",
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
        table.removeColumn(table.getColumnModel().getColumn(0)); // planque la colonne ID


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
        importButton = new JButton("Importer CSV");
        exportButton = new JButton("Exporter CSV");
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
        importButton.addActionListener(e -> importerCSV());
        exportButton.addActionListener(e -> exporterCSV());
        voirMouvementsButton.addActionListener(e -> {
            Animal selectedAnimal = (Animal) animalComboBox.getSelectedItem();
            if (selectedAnimal == null) {
                JOptionPane.showMessageDialog(this, "Aucun animal sélectionné.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Mouvement> mouvements = dbManager.getMouvementsParAnimal(selectedAnimal.getId());
            if (mouvements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun mouvement trouvé pour cet animal.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Mouvements de l’animal ").append(selectedAnimal.getNom()).append(" (ID: ").append(selectedAnimal.getNumeroId()).append(")\n\n");

            for (Mouvement m : mouvements) {
                sb.append("• ").append(m.getTypeMouvement()).append(" - ").append(m.getDateMouvement());
                if ("sortie".equalsIgnoreCase(m.getTypeMouvement()) && m.getDestination() != null) {
                    sb.append(" → ").append(m.getDestination());
                }
                sb.append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Historique des mouvements", JOptionPane.INFORMATION_MESSAGE);
        });


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
            showMessage("Sélectionnez une ligne contenant un mouvement.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int animalId = (int) tableModel.getValueAt(modelRow, 3); // Colonne "ID" de l'animal (index à adapter)
        List<Mouvement> mouvements = dbManager.getMouvementsParAnimal(animalId);

        boolean hasEntree = mouvements.stream().anyMatch(m -> "entrée".equalsIgnoreCase(m.getTypeMouvement()));
        boolean hasSortie = mouvements.stream().anyMatch(m -> "sortie".equalsIgnoreCase(m.getTypeMouvement()));

        if (hasEntree && hasSortie) {
            String[] choix = {"Supprimer l'entrée", "Supprimer la sortie", "Annuler"};
            int reponse = JOptionPane.showOptionDialog(
                this,
                "L’animal a une entrée et une sortie. Que souhaitez-vous supprimer ?",
                "Suppression ciblée",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                choix,
                choix[0]
            );

            if (reponse == 0) { // Supprimer l'entrée
                Mouvement entree = mouvements.stream().filter(m -> "entrée".equalsIgnoreCase(m.getTypeMouvement())).findFirst().orElse(null);
                if (entree != null) {
                    dbManager.deleteMouvement(entree.getId());
                }
            } else if (reponse == 1) { // Supprimer la sortie
                Mouvement sortie = mouvements.stream().filter(m -> "sortie".equalsIgnoreCase(m.getTypeMouvement())).findFirst().orElse(null);
                if (sortie != null) {
                    dbManager.deleteMouvement(sortie.getId());
                }
            } else {
                return; // Annulé
            }

        } else if (hasEntree) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cette entrée ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Mouvement entree = mouvements.stream().filter(m -> "entrée".equalsIgnoreCase(m.getTypeMouvement())).findFirst().orElse(null);
                if (entree != null) dbManager.deleteMouvement(entree.getId());
            }
        } else if (hasSortie) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cette sortie ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Mouvement sortie = mouvements.stream().filter(m -> "sortie".equalsIgnoreCase(m.getTypeMouvement())).findFirst().orElse(null);
                if (sortie != null) dbManager.deleteMouvement(sortie.getId());
            }
        }

        updateMouvementTable();
        showMessage("Mise à jour effectuée.");
    }


    public void rafraichirListeAnimaux() {
        animalComboBox.removeAllItems();
        animaux = dbManager.getTousLesAnimaux();
        
        for (Animal a : animaux) {
            animalComboBox.addItem(a); // si Animal.toString() affiche bien le nom
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
        Animal animal = (Animal) animalComboBox.getSelectedItem();
        if (animal == null) return;

        Date date = parseDateSafely(dateField.getText().trim());
        if (date == null) {
            showMessage("Format de date invalide.");
            return;
        }

        String type = (String) typeBox.getSelectedItem();

        String destination = JOptionPane.showInputDialog(this,
            "entrée".equals(type) ? "Provenance de l’animal :" : "Destination de l’animal :",
            "Information", JOptionPane.PLAIN_MESSAGE);

        if (destination == null || destination.trim().isEmpty()) {
            showMessage("Champ requis !");
            return;
        }

        boolean estDecede = "sortie".equalsIgnoreCase(type) && decedeCheckBox.isSelected();

        Mouvement m = new Mouvement(animal.getId(), type, date, destination, estDecede);
        dbManager.ajouterMouvement(m);

        if (estDecede) {
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
        List<Mouvement> mouvements = dbManager.getTousLesMouvements();
        String[] colonnes = {
            "ID_Entrée", "ID_Sortie", "Nom", "Espèce", "Sexe", "ID Animal", "Âge",
            "Date Entrée", "Provenance", "Date Sortie", "Destination", "Décédé"
        };

        tableModel.setDataVector(new Object[0][0], colonnes);

        // Regrouper les mouvements par animal
        Map<Integer, Mouvement> entrees = new HashMap<>();
        Map<Integer, Mouvement> sorties = new HashMap<>();

        for (Mouvement m : mouvements) {
            if ("entrée".equalsIgnoreCase(m.getTypeMouvement())) {
                entrees.put(m.getAnimalId(), m);
            } else if ("sortie".equalsIgnoreCase(m.getTypeMouvement())) {
                sorties.put(m.getAnimalId(), m);
            }
        }

        Set<Integer> allAnimalIds = new HashSet<>();
        allAnimalIds.addAll(entrees.keySet());
        allAnimalIds.addAll(sorties.keySet());

        for (int animalId : allAnimalIds) {
            Animal a = dbManager.getAnimalById(animalId);
            if (a == null) {
                System.out.println("Animal introuvable pour animalId=" + animalId);
                continue;
            }

            Mouvement entree = entrees.get(animalId);
            Mouvement sortie = sorties.get(animalId);

            Object[] ligne = new Object[]{
                entree != null ? entree.getId() : null,
                sortie != null ? sortie.getId() : null,
                a.getNom(), a.getEspece(), a.getSexe(), a.getNumeroId(), a.getAge(),
                entree != null ? entree.getDateMouvement().toString() : "",
                a.getProvenance(),
                sortie != null ? sortie.getDateMouvement().toString() : "",
                sortie != null ? sortie.getDestination() : "",
                a.isDecede() ? "Oui" : "Non"
            };

            tableModel.addRow(ligne);
        }

        table.setModel(tableModel);
    }

    public void clearForm() {
        if (animalComboBox.getItemCount() > 0) {
            animalComboBox.setSelectedIndex(0);
        }
        typeBox.setSelectedIndex(0);
        
        // Met la date du jour
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        dateField.setText(sdf.format(new java.util.Date()));

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
            showMessage("Veuillez selectionner une ligne.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        Object idEntreeObj = tableModel.getValueAt(modelRow, 0);
        Object idSortieObj = tableModel.getValueAt(modelRow, 1);

        boolean hasEntree = idEntreeObj != null;
        boolean hasSortie = idSortieObj != null;

        int mouvementId = -1;
        String type = "";

        if (hasEntree && hasSortie) {
            Object[] options = {"Modifier entree", "Modifier sortie", "Annuler"};
            int choix = JOptionPane.showOptionDialog(
                this,
                "Quel mouvement voulez-vous modifier ?",
                "Modification mouvement",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choix == 0) {
                mouvementId = (int) idEntreeObj;
                type = "entree";
            } else if (choix == 1) {
                mouvementId = (int) idSortieObj;
                type = "sortie";
            } else {
                return; // annule
            }

        } else if (hasEntree) {
            mouvementId = (int) idEntreeObj;
            type = "entree";
        } else if (hasSortie) {
            mouvementId = (int) idSortieObj;
            type = "sortie";
        } else {
            showMessage("Aucun mouvement a modifier.");
            return;
        }

        Mouvement m = dbManager.getMouvementById(mouvementId);
        if (m == null) {
            showMessage("Mouvement introuvable.");
            return;
        }

        Animal animal = dbManager.getAnimalById(m.getAnimalId());
        if (animal == null) {
            showMessage("Animal introuvable.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField dateField = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(m.getDateMouvement()));
        JTextField destinationField = new JTextField(m.getDestination() != null ? m.getDestination() : "");
        JCheckBox decedeBox = new JCheckBox("Decede");
        decedeBox.setVisible("sortie".equalsIgnoreCase(type));
        if ("sortie".equalsIgnoreCase(type)) {
            decedeBox.setSelected(animal.isDecede());
        }

        panel.add(new JLabel("Date (JJ/MM/AAAA) :"));
        panel.add(dateField);
        panel.add(new JLabel(type.equals("entree") ? "Provenance :" : "Destination :"));
        panel.add(destinationField);
        if ("sortie".equalsIgnoreCase(type)) {
            panel.add(new JLabel(""));
            panel.add(decedeBox);
        }

        int res = JOptionPane.showConfirmDialog(this, panel, "Modifier " + type, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            java.sql.Date newDate = parseDateSafely(dateField.getText());
            if (newDate == null) {
                showMessage("Format de date invalide.");
                return;
            }

            m.setDateMouvement(newDate);
            m.setDestination(destinationField.getText().trim());

            dbManager.updateMouvement(m);

            if ("sortie".equalsIgnoreCase(type)) {
                dbManager.mettreAJourStatutDeces(animal.getId(), decedeBox.isSelected());
            }

            updateMouvementTable();
            showMessage("Mouvement " + type + " modifie !");
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

    private String formatDate(java.sql.Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
    
    private void importerCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importer mouvements depuis un fichier CSV");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            int imported = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false; // skip header
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length < 5) continue;

                    try {
                        int animalId = Integer.parseInt(parts[1]);
                        String type = parts[3].trim();
                        java.sql.Date date = parseDateSafely(parts[4]);

                        String destination = parts.length > 5 ? parts[5].trim() : "";
                        boolean decede = parts.length > 6 && parts[6].trim().equalsIgnoreCase("oui");

                        Mouvement m = new Mouvement(animalId, type, date);
                        m.setDestination(destination);
                        dbManager.ajouterMouvement(m);

                        if ("sortie".equalsIgnoreCase(type)) {
                            dbManager.mettreAJourStatutDeces(animalId, decede);
                        }

                        imported++;
                    } catch (Exception ex) {
                        System.out.println("Erreur ligne ignorée : " + line);
                    }
                }

                updateMouvementTable();
                showMessage(imported + " mouvements importés !");
            } catch (IOException e) {
                showMessage("Erreur de lecture du fichier CSV : " + e.getMessage());
            }
        }
    }

    private void exporterCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter les mouvements en CSV");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(file)) {
                // En-têtes
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pw.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) pw.print(",");
                }
                pw.println();

                // Données
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object val = tableModel.getValueAt(row, col);
                        pw.print(val != null ? val.toString() : "");
                        if (col < tableModel.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }

                showMessage("Export terminé : " + file.getName());
            } catch (IOException e) {
                showMessage("Erreur export CSV : " + e.getMessage());
            }
        }
    }


}

