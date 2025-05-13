package com.ttpc.ges.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import com.ttpc.ges.components.TTPCButton;
import com.ttpc.ges.components.TTPCComboBox;
import com.ttpc.ges.components.TTPCDialogMouvement;
import com.ttpc.ges.components.TTPCFormattedTextField;
import com.ttpc.ges.components.TTPCTextField;
import com.ttpc.ges.model.Animal;
import com.ttpc.ges.model.DatabaseManager;
import com.ttpc.ges.model.Mouvement;
import com.ttpc.ges.utils.TTPCDateParser;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MouvementPanel extends JPanel {
    private final DatabaseManager dbManager;
    private TTPCComboBox<Animal> animalComboBox;
    private TTPCComboBox<String> typeBox;
    private JCheckBox decedeCheckBox;
    private TTPCFormattedTextField dateField;
	private TTPCTextField searchField;
	private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Animal> animaux;
    private JPanel formWrapper;
    private TTPCButton voirMouvementsButton,editButton, addButton, deleteButton;
    private JButton importButton,exportButton,toggleFormButton;
    private JSplitPane splitPane;
    
    private Color blueColor = new Color(188,218,244);

    public MouvementPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());

        // === Formulaire ===
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un mouvement"));
        formPanel.setPreferredSize(new Dimension(350, 200));

        animalComboBox = new TTPCComboBox<>();
        typeBox = new TTPCComboBox<>(new String[]{""});
        decedeCheckBox = new JCheckBox("Décédé");
        decedeCheckBox.setVisible(false);
        decedeCheckBox.setBackground(blueColor);

        new SimpleDateFormat("dd/MM/yyyy");
		dateField = new TTPCFormattedTextField();

        //  Préremplir avec la date du jour
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateField.setText(sdf.format(new java.util.Date()));


        formPanel.add(new JLabel("Animal :")); formPanel.add(animalComboBox);
        formPanel.add(new JLabel("Type :")); formPanel.add(typeBox);
        formPanel.add(new JLabel("Date (JJ/MM/AAAA) :")); formPanel.add(dateField);
        formPanel.add(new JLabel("")); formPanel.add(decedeCheckBox);

        addButton = new TTPCButton("Ajouter");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton = new TTPCButton("Modifier la sélection");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton = new TTPCButton("Supprimer la sélection");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        voirMouvementsButton = new TTPCButton("Voir tous les mouvements");
        voirMouvementsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(voirMouvementsButton);

        formPanel.setBackground(blueColor);
        buttonPanel.setBackground(blueColor);
        buttonPanel.setLayout(new GridLayout(1, 0, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, 75));


        
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
     // Cacher la colonne ID (colonne 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
     // Cacher la colonne ID (colonne 1)
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);


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

        // === Listeners ===
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        addButton.addActionListener(e -> ajouterMouvement());
        editButton.addActionListener(e -> {
			try {
				modifierSelection();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				showMessage("Erreur bouton midifier : " + e1.getMessage());
			}
		});
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
            System.out.println("Mouvements récupérés : " + mouvements.size());
            for (Mouvement m : mouvements) {
                System.out.println("→ " + m.getTypeMouvement() + " | " + m.getDateMouvement());
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            TTPCDialogMouvement dialog = new TTPCDialogMouvement(
        	    "Mouvements de : " + selectedAnimal.getNom(),
        	    table,
        	    600,
        	    350
        	);
        	dialog.setVisible(true);

        });


        // === Init données ===
        rafraichirListeAnimaux();
        updateMouvementTable();
    }
    
    
    
    public TTPCComboBox<Animal> getAnimalComboBox() {
		return animalComboBox;
	}

	public void setAnimalComboBox(TTPCComboBox<Animal> animalComboBox) {
		this.animalComboBox = animalComboBox;
	}

	public TTPCComboBox<String> getTypeBox() {
		return typeBox;
	}

	public void setTypeBox(TTPCComboBox<String> typeBox) {
		this.typeBox = typeBox;
	}

	public JCheckBox getDecedeCheckBox() {
		return decedeCheckBox;
	}

	public void setDecedeCheckBox(JCheckBox decedeCheckBox) {
		this.decedeCheckBox = decedeCheckBox;
	}

	public TTPCFormattedTextField getDateField() {
		return dateField;
	}

	public void setDateField(TTPCFormattedTextField dateField) {
		this.dateField = dateField;
	}

	public TTPCTextField getSearchField() {
		return searchField;
	}

	public void setSearchField(TTPCTextField searchField) {
		this.searchField = searchField;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public TableRowSorter<DefaultTableModel> getSorter() {
		return sorter;
	}

	public void setSorter(TableRowSorter<DefaultTableModel> sorter) {
		this.sorter = sorter;
	}

	public List<Animal> getAnimaux() {
		return animaux;
	}

	public void setAnimaux(List<Animal> animaux) {
		this.animaux = animaux;
	}

	public JPanel getFormWrapper() {
		return formWrapper;
	}

	public void setFormWrapper(JPanel formWrapper) {
		this.formWrapper = formWrapper;
	}

	public TTPCButton getVoirMouvementsButton() {
		return voirMouvementsButton;
	}

	public void setVoirMouvementsButton(TTPCButton voirMouvementsButton) {
		this.voirMouvementsButton = voirMouvementsButton;
	}

	public TTPCButton getEditButton() {
		return editButton;
	}

	public void setEditButton(TTPCButton editButton) {
		this.editButton = editButton;
	}

	public TTPCButton getAddButton() {
		return addButton;
	}

	public void setAddButton(TTPCButton addButton) {
		this.addButton = addButton;
	}

	public TTPCButton getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(TTPCButton deleteButton) {
		this.deleteButton = deleteButton;
	}

	public JButton getImportButton() {
		return importButton;
	}

	public void setImportButton(JButton importButton) {
		this.importButton = importButton;
	}

	public JButton getExportButton() {
		return exportButton;
	}

	public void setExportButton(JButton exportButton) {
		this.exportButton = exportButton;
	}

	public JButton getToggleFormButton() {
		return toggleFormButton;
	}

	public void setToggleFormButton(JButton toggleFormButton) {
		this.toggleFormButton = toggleFormButton;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	public void setSplitPane(JSplitPane splitPane) {
		this.splitPane = splitPane;
	}

	public Color getBlueColor() {
		return blueColor;
	}

	public void setBlueColor(Color blueColor) {
		this.blueColor = blueColor;
	}

	public DatabaseManager getDbManager() {
		return dbManager;
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
        Object idEntreeObj = tableModel.getValueAt(modelRow, 0); // ID_Entrée
        Object idSortieObj = tableModel.getValueAt(modelRow, 1); // ID_Sortie
        
        boolean hasEntree = idEntreeObj != null;
        boolean hasSortie = idSortieObj != null;

        if (!hasEntree && !hasSortie) {
            showMessage("Aucun mouvement associé à cette ligne.");
            return;
        }

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
                dbManager.deleteMouvement(Integer.parseInt(idEntreeObj.toString()));
            } else if (reponse == 1) { // Supprimer la sortie
                dbManager.deleteMouvement(Integer.parseInt(idSortieObj.toString()));
            }

        } else if (hasEntree) {
        	int idEntree = Integer.parseInt(idEntreeObj.toString());
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cette entrée ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteMouvement(idEntree);
            }

        } else if (hasSortie) {
        	int idSortie = Integer.parseInt(idSortieObj.toString());
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cette sortie ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteMouvement(idSortie);
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
        if (animal == null) {
            showMessage("Veuillez sélectionner un animal.");
            return;
        }

        String type = ((String) typeBox.getSelectedItem()).toLowerCase();
        String dateStr = dateField.getText().trim();
        Date sqlDate = null;
        try {
			sqlDate = TTPCDateParser.stringToSqlDate(dateStr);
            System.out.println("Date SQL : " + sqlDate);
        } catch (Exception e) {
            System.out.println("Erreur de format : " + e.getMessage());
        }
        
        boolean estDecede = decedeCheckBox.isSelected();

        if (dateStr.contains("_")) {
            showMessage("Veuillez compléter la date.");
            return;
        }

        // Préremplir la destination avec la provenance de l’animal
        String defaultDestination = animal.getProvenance() != null ? animal.getProvenance() : "";
        String destination = JOptionPane.showInputDialog(
            this,
            "Destination / provenance :",
            defaultDestination
        );

        if (destination == null || destination.trim().isEmpty()) {
            showMessage("La destination est requise.");
            return;
        }

        Mouvement m = new Mouvement(animal.getId(), type, sqlDate, destination.trim(), estDecede);
        dbManager.ajouterMouvement(m);
        updateMouvementTable();
        showMessage("Mouvement ajouté avec succès.");
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
     // Cacher les colonnes ID_Entrée (index 0) et ID_Sortie (index 1)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

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
    
    private void modifierSelection() throws ParseException {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Sélectionnez une ligne.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        Object idEntreeObj = tableModel.getValueAt(modelRow, 0);
        Object idSortieObj = tableModel.getValueAt(modelRow, 1);

        boolean hasEntree = idEntreeObj != null;
        boolean hasSortie = idSortieObj != null;

        if (!hasEntree && !hasSortie) {
            showMessage("Aucun mouvement sélectionné.");
            return;
        }

        String[] options = {"Modifier l'entrée", "Modifier la sortie", "Annuler"};
        int choix = 0;

        if (hasEntree && hasSortie) {
            choix = JOptionPane.showOptionDialog(
                this,
                "Quel mouvement souhaitez-vous modifier ?",
                "Modifier un mouvement",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (choix == 2) return;
        } else if (hasSortie) {
            choix = 1;
        }

        int id = choix == 0
            ? Integer.parseInt(idEntreeObj.toString())
            : Integer.parseInt(idSortieObj.toString());

        Mouvement m = dbManager.getMouvementById(id);
        if (m == null) {
            showMessage("Mouvement introuvable.");
            return;
        }

        JTextField dateField = new JTextField(m.getDateMouvement().toString());
        JCheckBox decedeBox = new JCheckBox("Décédé", m.isDecede());
        JTextField destinationField = new JTextField(m.getDestination());
        if (hasEntree && !hasSortie) {
        	destinationField = new JTextField(dbManager.getAnimalById(m.getAnimalId()).getProvenance());
        } 
        

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Date (JJ/MM/AAAA) :")); panel.add(dateField);
        panel.add(new JLabel("Décédé ?")); panel.add(decedeBox);
        panel.add(new JLabel("Destination / provenance :")); panel.add(destinationField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Modifier Mouvement", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String date = dateField.getText().trim();
            boolean decede = decedeBox.isSelected();
            String destination = destinationField.getText().trim();

            if (date.isEmpty() || destination.isEmpty()) {
                showMessage("Champs requis.");
                return;
            }

            Mouvement updated = new Mouvement(m.getAnimalId(), m.getTypeMouvement(), TTPCDateParser.stringToSqlDate(date), destination, decede);
            updated.setId(m.getId());
            dbManager.updateMouvement(updated);

            updateMouvementTable();
            showMessage("Mouvement modifié !");
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
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",", -1); // garder les champs vides

                    if (parts.length < 11) continue;

                    try {
                        // Colonne "ID Animal" (numeroId)
                        String numeroId = parts[5].trim();
                        if (numeroId.isEmpty()) continue;

                        Animal a = dbManager.getAnimalByNumeroId(numeroId);
                        if (a == null) {
                            System.out.println("Animal introuvable : " + numeroId);
                            continue;
                        }

                        int animalId = a.getId();

                        // Entrée
                        String dateEntreeStr = parts[7].trim();
                        if (!dateEntreeStr.isEmpty()) {
                            Date dateEntree = TTPCDateParser.stringToSqlDate(dateEntreeStr);
                            if (dateEntree != null) {
                                Mouvement entree = new Mouvement(animalId, "entrée", dateEntree, parts[8].trim(), false);
                                dbManager.ajouterMouvement(entree);
                                System.out.println("Entrée ajoutée pour animal ID " + animalId);
                                imported++;
                            } else {
                                System.out.println("→ Date entrée invalide : " + dateEntreeStr);
                            }
                        }

                        // Sortie
                        String dateSortieStr = parts[9].trim();
                        if (!dateSortieStr.isEmpty()) {
                            Date dateSortie = TTPCDateParser.stringToSqlDate(dateSortieStr);
                            if (dateSortie != null) {
                                boolean decede = parts.length > 11 && parts[11].trim().equalsIgnoreCase("oui");
                                Mouvement sortie = new Mouvement(animalId, "sortie", dateSortie, parts[10].trim(), decede);
                                dbManager.ajouterMouvement(sortie);
                                dbManager.mettreAJourStatutDeces(animalId, decede);
                                System.out.println("Sortie ajoutée pour animal ID " + animalId);
                                imported++;
                            } else {
                                System.out.println("→ Date sortie invalide : " + dateSortieStr);
                            }
                        }

                    } catch (Exception ex) {
                        System.out.println("Erreur ligne CSV : " + ex.getMessage());
                        // continue sans planter
                    }
                }

                updateMouvementTable();
                showMessage(imported + " mouvements importés avec succès.");

            } catch (IOException e) {
                showMessage("Erreur lecture fichier : " + e.getMessage());
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
            } catch (IOException e) {
                showMessage("Erreur export CSV : " + e.getMessage());
            }
        }
    }

}

