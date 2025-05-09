package com.ttpc.ges.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TTPCComboBox<T> extends JComboBox<T> {

    private Color normalBackground = new Color(245, 250, 255);
    private Color normalBorder = new Color(180, 200, 220);
    private Color focusBackground = new Color(230, 240, 255);
    private Color focusBorder = new Color(80, 140, 200);

    public TTPCComboBox(T[] items) {
        super(items);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setFocusable(true);
        setBackground(normalBackground);
        setBorder(new LineBorder(normalBorder, 2));
        setOpaque(true);

        setMaximumRowCount(10);
        setPreferredSize(new Dimension(140, 32));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

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

    public TTPCComboBox() {
		super();
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
