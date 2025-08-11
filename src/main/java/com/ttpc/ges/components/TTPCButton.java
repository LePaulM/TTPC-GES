package com.ttpc.ges.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TTPCButton extends JButton {

    private Color normalBackground = new Color(250, 250, 250);
    private Color normalBorder = new Color(160, 190, 220);
    private Color focusBackground = new Color(200, 230, 255);
    private Color focusBorder = new Color(100, 160, 210);

    public TTPCButton(String text) {
        super();
        System.out.println("i am button : " + text);
        setMultilineText(text);
        setFont(new Font("SansSerif", Font.BOLD, 13));

        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setBackground(normalBackground);
        setForeground(Color.DARK_GRAY);
        //setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setMargin(new Insets(6, 12, 6, 12));  // haut, gauche, bas, droite


        setMinimumSize(new Dimension(100, 10));
        setPreferredSize(new Dimension(140, 25));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));  // ← hauteur plafonnée


        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBackground(focusBackground);
                setBorder(BorderFactory.createLineBorder(focusBorder, 2));
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBackground(normalBackground);
                setBorder(BorderFactory.createLineBorder(normalBorder, 2));
                repaint();
            }
        });
    }

    public void setMultilineText(String text) {
        this.setText("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
    }

    public void setFocusColors(Color background, Color border) {
        this.focusBackground = background;
        this.focusBorder = border;
    }

    public void setNormalColors(Color background, Color border) {
        this.normalBackground = background;
        this.normalBorder = border;
        setBackground(background);
        setBorder(BorderFactory.createLineBorder(border, 2));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // rayon des coins
        g2.dispose();
        super.paintComponent(g);
    }
}
