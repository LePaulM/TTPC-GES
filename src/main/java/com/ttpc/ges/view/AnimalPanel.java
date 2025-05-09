package com.ttpc.ges.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.ttpc.ges.components.TTPCButton;
import com.ttpc.ges.components.TTPCComboBox;
import com.ttpc.ges.components.TTPCDialogMouvement;
import com.ttpc.ges.components.TTPCTextField;
import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

public class AnimalPanel extends JPanel {
    private final DatabaseManager dbManager;
    private TTPCTextField nomField, especeField, provenanceField, searchField, numeroIdField;
    private JTextArea descriptionArea;
    private TTPCComboBox<String> sexeBox;
    private JSpinner ageSpinner;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel formWrapper;
    private JSplitPane splitPane;
    private TTPCButton addButton,editButton, deleteButton, voirMouvementsButton; 
    private JButton importButton, exportButton, toggleFormButton;
    private Color blueColor = new Color(188,218,244);

    public AnimalPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());
        
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("CheckBox.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Spinner.font", new Font("SansSerif", Font.PLAIN, 14));

        // Table : contenu des cellules
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));

        // Table : en-têtes de colonnes
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 14));

        // === Formulaire ===
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un animal"));
        formPanel.setPreferredSize(new Dimension(250, 200));

        numeroIdField = new TTPCTextField();
        nomField = new TTPCTextField();
        especeField = new TTPCTextField();
        sexeBox = new TTPCComboBox<>(new String[]{"M", "F"});
        ageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        provenanceField = new TTPCTextField();
        descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        addButton = new TTPCButton("Ajouter animal");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton = new TTPCButton("Modifier la sélection");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton = new TTPCButton("Supprimer la sélection");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        voirMouvementsButton = new TTPCButton("Voir les mouvements");
        voirMouvementsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        formPanel.add(new JLabel("Numéro d'identification :")); formPanel.add(numeroIdField);
        formPanel.add(new JLabel("Nom :")); formPanel.add(nomField);
        formPanel.add(new JLabel("Espèce :")); formPanel.add(especeField);
        formPanel.add(new JLabel("Sexe :")); formPanel.add(sexeBox);
        formPanel.add(new JLabel("Âge :")); formPanel.add(ageSpinner);
        formPanel.add(new JLabel("Provenance (Nom, qualité et adresse du fournisseur) :")); formPanel.add(provenanceField);
        formPanel.add(new JLabel("Description (Race, robe, poils, taille,signes particuliers) :")); formPanel.add(descriptionScroll);

        configurerAgeSpinner();
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(voirMouvementsButton);
        buttonPanel.setLayout(new GridLayout(1, 0, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, 75));

        formPanel.setBackground(blueColor);
        buttonPanel.setBackground(blueColor);
        formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.add(formPanel);
        formWrapper.add(buttonPanel);
        

        // === Table ===
        String[] cols = {"ID","Numero d'identification", "Nom", "Espèce", "Sexe", "Âge", "Provenance", "Description", "Décédé ?"," Présent ?"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        
        searchField = new TTPCTextField();
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

        // Listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        addButton.addActionListener(e -> ajouterAnimal());
        editButton.addActionListener(e -> modifierSelection());
        deleteButton.addActionListener(e -> supprimerSelection());
        importButton.addActionListener(e -> importerCSV());
        exportButton.addActionListener(e -> exporterCSV());
        voirMouvementsButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
            	int modelIndex = table.convertRowIndexToModel(row);
            	int id = (int) tableModel.getValueAt(modelIndex, 0);
                Animal a = dbManager.getAnimalById(id);
                System.out.println("ID sélectionné : " + id);
                System.out.println("Animal récupéré : " + a);
                System.out.println("Animal : " + a.getId() + " - " + a.getNom());

                if (a != null) {
                    afficherMouvementsAnimal(a);
                } else {
                    JOptionPane.showMessageDialog(this, "Animal introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un animal.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateAnimalTable(this.dbManager.getTousLesAnimaux());
    }

    private void applyFilter() {
        String query = searchField.getText();
        sorter.setRowFilter(query.isEmpty() ? null : RowFilter.regexFilter("(?i)" + query));
    }

    public Animal getAnimalFormData() {
    	String numeroId = numeroIdField.getText().trim();
        String nom = nomField.getText().trim();
        String espece = especeField.getText().trim();
        char sexe = ((String) sexeBox.getSelectedItem()).charAt(0);
        int age = (int) ageSpinner.getValue();
        String provenance = provenanceField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (nom.isEmpty() || espece.isEmpty() || numeroId.isEmpty()) {
            showMessage("Nom, espèce et identifiant sont obligatoires.");
            return null;
        }
        
//        System.out.println("AnimalPanel GetAnimalFormData");
//        System.out.println(new Animal(numeroId, nom, espece, sexe, age, provenance, description).toString());

        return new Animal(numeroId, nom, espece, sexe, age, provenance, description);
    }

    public void updateAnimalTable(List<Animal> animaux) {
    	//System.out.println("updateAnimalTable");
        tableModel.setRowCount(0);
        for (Animal a : animaux) {
        	// System.out.println(a.toStringLong());
            tableModel.addRow(new Object[]{
            	a.getId(),
                a.getNumeroId(),
                a.getNom(),
                a.getEspece(),
                a.getSexe(),
                a.getAge(),
                a.getProvenance(),
                a.getDescription(),
                a.isDecede() ? "Vrai" : "Faux",
                a.isPresent(dbManager) ? "Oui" : "Non"
            });
        }
    }

    private void importerCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importer un fichier CSV");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String line;
                boolean firstLine = true;

                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false; // sauter la ligne d'entête
                        continue;
                    }

                    String[] parts = line.split(",", -1); // -1 pour ne pas ignorer les colonnes vides

                    if (parts.length >= 9) {
                        String numeroId = parts[1].trim();
                        String nom = parts[2].trim();
                        String espece = parts[3].trim();
                        char sexe = parts[4].trim().isEmpty() ? 'M' : parts[4].trim().charAt(0);
                        int age = Integer.parseInt(parts[5].trim());
                        String provenance = parts[6].trim();
                        String description = parts[7].trim();
                        boolean decede = parts[8].trim().equalsIgnoreCase("oui") || parts[8].trim().equalsIgnoreCase("true");

                        Animal a = new Animal(numeroId, nom, espece, sexe, age, provenance, description, decede);
                        dbManager.ajouterAnimal(a);
                    }
                }

                updateAnimalTable(dbManager.getTousLesAnimaux());
                JOptionPane.showMessageDialog(this, "Import terminé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
            	showMessage("Erreur : " + e.getMessage());
            }
        }
    }

    private void exporterCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter les animaux en CSV");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
                // En-têtes CSV
                pw.println("ID,Numéro ID,Nom,Espèce,Sexe,Âge,Provenance,Description,Décédé");

                // Données
                for (int i = 0; i < table.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object val = table.getValueAt(i, j);
                        line.append(val != null ? val.toString().replace(",", " ") : "");
                        if (j < table.getColumnCount() - 1) line.append(",");
                    }
                    pw.println(line);
                }

                JOptionPane.showMessageDialog(this, "Export CSV terminé :\n" + file.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
            	showMessage("Erreur : " + e.getMessage());
            }
        }
    }

    private void afficherMouvementsAnimal(Animal animal) {
        List<Mouvement> mouvements = dbManager.getMouvementsParAnimal(animal.getId());

        if (mouvements.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pas de mouvements enregistrés"
                );
            
            return;
        };

        String[] colonnes = {"Type", "Date", "Décédé", "Destination / Provenance"};
        Object[][] donnees = new Object[mouvements.size()][4];

        for (int i = 0; i < mouvements.size(); i++) {
            Mouvement m = mouvements.get(i);
            donnees[i][0] = m.getTypeMouvement();
            donnees[i][1] = m.getDateMouvement();
            donnees[i][2] = m.isDecede() ? "Oui" : "Non";
            donnees[i][3] = m.getDestination();
        }

        JTable table = new JTable(donnees, colonnes);

        TTPCDialogMouvement dialog = new TTPCDialogMouvement(
        	    "Mouvements de : " + animal.getNom(),
        	    table,
        	    600,
        	    350
        	);
        	dialog.setVisible(true);

    }

    public void clearForm() {
    	numeroIdField.setText("");
        nomField.setText("");
        especeField.setText("");
        sexeBox.setSelectedIndex(0);
        ageSpinner.setValue(0);
        provenanceField.setText("");
        descriptionArea.setText("");
        numeroIdField.setText("");
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    
    private void ajouterAnimal() {
    	//System.out.println("AnimalPanel ajouterAnimal");
    	String numero = numeroIdField.getText().trim();
        //System.out.println("Panel numero: " + numero);
        String nom = nomField.getText().trim();
        // A GARDER - Evite les lignes vides lors de l'ajout
        if (nom.isEmpty() || numero.isEmpty()) {
        	showMessage("Nom et identifiant sont obligatoires.");
            return;
        }
        String espece = especeField.getText().trim();
        String sexe = (String) sexeBox.getSelectedItem();
        int age = (Integer) ageSpinner.getValue();
        String provenance = provenanceField.getText().trim();
        String description = descriptionArea.getText().trim();


        
        Animal animal = new Animal(numero, nom, espece, sexe.charAt(0), age, provenance, description);
        //System.out.println("DEBUG >>> Animal après insertion : " + animal);

        dbManager.ajouterAnimal(animal);
        System.out.println("ID Animal : " + animal.getId());
        clearForm();
        updateAnimalTable(dbManager.getTousLesAnimaux());
        MainFrame.updateMouvementComboBox();
    }
 
    private void modifierSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Sélectionnez un animal.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String numeroId = String.valueOf(id); // ou utilise id directement si c'est ce que tu veux
        String nom = (String) tableModel.getValueAt(modelRow, 1);
        String espece = (String) tableModel.getValueAt(modelRow, 2);
        String sexe = tableModel.getValueAt(modelRow, 3).toString();
        int age = (int) tableModel.getValueAt(modelRow, 4);
        String provenance = (String) tableModel.getValueAt(modelRow, 5);
        String description = (String) tableModel.getValueAt(modelRow, 6);
        boolean decede = "Oui".equals(tableModel.getValueAt(modelRow, 7));

        JTextField numeroIdField = new JTextField(numeroId);
        JTextField nomField = new JTextField(nom);
        JTextField especeField = new JTextField(espece);
        JComboBox<String> sexeBox = new JComboBox<>(new String[]{"M", "F"});
        sexeBox.setSelectedItem(sexe);
        JSpinner ageSpinner = ageSpinner = new JSpinner(new SpinnerNumberModel(age, 0, 100, 1));
        JTextField provenanceField = new JTextField(provenance);
        JTextArea descriptionArea = new JTextArea(description, 3, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        JCheckBox decedeBox = new JCheckBox("Décédé", decede);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Numéro d'identification :")); panel.add(numeroIdField);
        panel.add(new JLabel("Nom :")); panel.add(nomField);
        panel.add(new JLabel("Espèce :")); panel.add(especeField);
        panel.add(new JLabel("Sexe :")); panel.add(sexeBox);
        panel.add(new JLabel("Âge :")); panel.add(ageSpinner);
        panel.add(new JLabel("Provenance :")); panel.add(provenanceField);
        panel.add(new JLabel("Description :")); panel.add(descScroll);
        panel.add(new JLabel("")); panel.add(decedeBox);

        int res = JOptionPane.showConfirmDialog(this, panel, "Modifier Animal", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int newAge = (Integer) ageSpinner.getValue();
                Animal a = new Animal(numeroIdField.getText().trim(), nomField.getText().trim(), especeField.getText().trim(), 
                                      ((String) sexeBox.getSelectedItem()).charAt(0), newAge,
                                      provenanceField.getText().trim(), descriptionArea.getText().trim(),
                                      decedeBox.isSelected());
                dbManager.updateAnimal(a);
                updateAnimalTable(dbManager.getTousLesAnimaux());
                showMessage("Animal modifié !");
                MainFrame.updateMouvementComboBox();
            } catch (NumberFormatException e) {
                showMessage("Âge invalide.");
            }
        }
    }
    
    private void supprimerSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un animal à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0); // ID en première colonne

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer cet animal ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
        	dbManager.supprimerAnimalEtMouvements(id);
            updateAnimalTable(dbManager.getTousLesAnimaux());
            MainFrame.updateMouvementComboBox();
            MainFrame.getMouvementPanelInstance().updateMouvementTable(); // mise à jour mouvements
            JOptionPane.showMessageDialog(this, "Animal supprimé avec succès.");
        }
    }

    // Empecher les lettres et caracteres speciaux dans le agespinner
    private void configurerAgeSpinner() {
        JFormattedTextField txt = ((JSpinner.DefaultEditor) ageSpinner.getEditor()).getTextField();
        ((AbstractDocument) txt.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d*")) {
                    super.replace(fb, offset, length, string, attr);
                }
            }
        });
    }

}
