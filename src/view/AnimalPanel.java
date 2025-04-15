package view;

import model.Animal;
import model.DatabaseManager;
import model.Mouvement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class AnimalPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JTextField nomField, especeField, provenanceField, searchField;
    private JTextArea descriptionArea;
    private JComboBox<String> sexeBox;
    //private JTextField ageField;
    private JSpinner ageSpinner;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel formWrapper;
    private JButton toggleFormButton;
    private JSplitPane splitPane;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField numeroIdField;
    private JButton voirMouvementsButton;


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

        numeroIdField = new JTextField();
        nomField = new JTextField();
        especeField = new JTextField();
        sexeBox = new JComboBox<>(new String[]{"M", "F"});
        ageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        provenanceField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier la sélection");
        deleteButton = new JButton("Supprimer la sélection");
        voirMouvementsButton = new JButton("Voir les mouvements");
        
        formPanel.add(new JLabel("Numéro d'identification :")); formPanel.add(numeroIdField);
        formPanel.add(new JLabel("Nom :")); formPanel.add(nomField);
        formPanel.add(new JLabel("Espèce :")); formPanel.add(especeField);
        formPanel.add(new JLabel("Sexe :")); formPanel.add(sexeBox);
        formPanel.add(new JLabel("Âge :")); formPanel.add(ageSpinner);
        formPanel.add(new JLabel("Provenance (Nom, qualité et adresse du fournisseur) :")); formPanel.add(provenanceField);
        formPanel.add(new JLabel("Description (Race, robe, poils, taille,signes particuliers) :")); formPanel.add(descriptionScroll);

//        Dimension champTaille = new Dimension(200, 25);
//        numeroIdField.setMaximumSize(champTaille);
//        especeField.setMaximumSize(champTaille);
//        nomField.setMaximumSize(champTaille);
//        ageSpinner.setMaximumSize(champTaille);
//        provenanceField.setMaximumSize(champTaille);
//        descriptionArea.setMaximumSize(new Dimension(200, 60));

        configurerAgeSpinner();
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(voirMouvementsButton);

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
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
            	int modelIndex = table.convertRowIndexToModel(selectedRow);
                String nom = (String) tableModel.getValueAt(modelIndex, 1); // Colonne "Nom"
                String description = (String) tableModel.getValueAt(modelIndex, 6); // Colonne "Description"

             // On identifie un animal via son nom + description, faute d'avoir accès à l'objet directement.
             // Cette méthode peut être trompeuse si plusieurs animaux ont le même nom ET la même description.

                // Recherche approximative dans la base
                Animal a = dbManager.getTousLesAnimaux().stream()
                    .filter(animal ->
                        animal.getNom().equalsIgnoreCase(nom) &&
                        animal.getDescription().equalsIgnoreCase(description)
                    )
                    .findFirst()
                    .orElse(null);

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
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Scanner scanner = new Scanner(chooser.getSelectedFile())) {
                if (scanner.hasNextLine()) scanner.nextLine();
                while (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split(",");
                    String nom = line[1].trim();
                    String espece = line[2].trim();
                    char sexe = line[3].trim().charAt(0);
                    int age = Integer.parseInt(line[4].trim());
                    String provenance = line[5].trim();
                    String description = line[6].trim();
                    boolean decede = Boolean.parseBoolean(line[7].trim());

                    Animal a = new Animal(nom, espece, sexe, age, provenance, description, decede);
                    dbManager.ajouterAnimal(a);
                }
                updateAnimalTable(dbManager.getTousLesAnimaux());
                showMessage("Importation réussie !");
            } catch (Exception ex) {
                showMessage("Erreur import : " + ex.getMessage());
            }
        }
    }

    public void exporterCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile() + ".csv")) {
                pw.println("id,nom,espece,sexe,age,provenance,description,decede");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount() - 1; j++) {
                        pw.print(tableModel.getValueAt(i, j));
                        pw.print(",");
                    }
                    pw.println(tableModel.getValueAt(i, tableModel.getColumnCount() - 1));
                }
                showMessage("Exportation réussie !");
            } catch (Exception ex) {
                showMessage("Erreur export : " + ex.getMessage());
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
        JScrollPane scroll = new JScrollPane(table);

        JOptionPane.showMessageDialog(this,
            scroll,
            "Mouvements de : " + animal.getNom() + " (" + animal.getEspece() + ")",
            JOptionPane.INFORMATION_MESSAGE
        );
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
    }

    
    private void modifierSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Sélectionnez un animal.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String numeroId = (String) tableModel.getValueAt(modelRow, 0);
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
            dbManager.deleteAnimal(id);
            updateAnimalTable(dbManager.getTousLesAnimaux());
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
