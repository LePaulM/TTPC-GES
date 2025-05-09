package com.ttpc.ges.components;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

public class TTPCDialogMouvement extends JDialog {

    public TTPCDialogMouvement(String titre, JTable table, int largeur, int hauteur) {
        super((Frame) null, titre, true);

        setSize(largeur, hauteur);
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 250, 255));

        JScrollPane scroll = new JScrollPane(table);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setOpaque(false);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dispose());

        JButton exportButton = new JButton("Exporter CSV");
        exportButton.addActionListener(e -> exporterCSV(table));

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(exportButton);
        bottomBar.add(closeButton);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(bottomBar, BorderLayout.SOUTH);
    }

    private void exporterCSV(JTable table) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter les mouvements en CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                TableModel model = table.getModel();
                // En-têtes
                for (int i = 0; i < model.getColumnCount(); i++) {
                    pw.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();

                // Données
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object val = model.getValueAt(row, col);
                        pw.print(val != null ? val.toString().replace(",", " ") : "");
                        if (col < model.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Export terminé : " + file.getAbsolutePath());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
