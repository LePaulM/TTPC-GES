package com.ttpc.ges.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TTPCButtonPanel extends JPanel {
    private boolean selected = false;
    private final int cornerRadius = 20;
    private final Color selectedColor = new Color(100, 149, 237);
    private final Color unselectedColor = new Color(230, 230, 230);
    private final JLabel label;

    public TTPCButtonPanel(String text, ImageIcon icon) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // padding intérieur

        // Configure le label avec icône + texte centré verticalement
        label = new JLabel(text, icon, JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(Color.BLACK);

        add(label, BorderLayout.CENTER);

        // Gérer le clic (tu peux remplacer ça par une logique centrale)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSelected(true);
            }
        });
    }

    public void setSelected(boolean value) {
        this.selected = value;
        label.setForeground(value ? Color.WHITE : Color.BLACK);
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(selected ? selectedColor : unselectedColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
