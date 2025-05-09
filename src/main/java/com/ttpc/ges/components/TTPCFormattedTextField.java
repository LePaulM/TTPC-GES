package com.ttpc.ges.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.MaskFormatter;

public class TTPCFormattedTextField extends JFormattedTextField {

    private Color normalBackground = new Color(255, 255, 255);
    private Color normalBorder = new Color(200, 200, 200);
    private Color focusBackground = new Color(250, 250, 255);
    private Color focusBorder = new Color(100, 150, 220);

    public TTPCFormattedTextField() {
        super(createDateFormatter());
        initStyle();
        setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
    }

    private static MaskFormatter createDateFormatter() {
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            formatter.setPlaceholderCharacter('_');
            return formatter;
        } catch (ParseException e) {
            throw new RuntimeException("Erreur de format pour le champ date");
        }
    }

    private void initStyle() {
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setOpaque(true);
        setBackground(normalBackground);
        setBorder(new LineBorder(normalBorder, 2));
        setPreferredSize(new Dimension(160, 30));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        setMinimumSize(new Dimension(100, 28));

        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                setBackground(focusBackground);
                setBorder(new LineBorder(focusBorder, 2));
                repaint();
            }

            public void focusLost(FocusEvent e) {
                setBackground(normalBackground);
                setBorder(new LineBorder(normalBorder, 2));
                repaint();
            }
        });
    }

    public void setFocusColors(Color bg, Color border) {
        this.focusBackground = bg;
        this.focusBorder = border;
    }

    public void setNormalColors(Color bg, Color border) {
        this.normalBackground = bg;
        this.normalBorder = border;
        setBackground(bg);
        setBorder(new LineBorder(border, 2));
        repaint();
    }
}
