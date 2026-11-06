package com.pos.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public final class UiUtil {

    private UiUtil() {}

    /** Read-only text that wraps inside a fixed box (works like a label). */
    public static JTextPane wrappedText(String text, Font font, Color color,
                                        int width, int height, boolean centered) {
        String value = (text == null) ? "" : text;

        JTextPane pane = new JTextPane();
        pane.setText(value);
        pane.setFont(font);
        pane.setForeground(color);
        pane.setEditable(false);
        pane.setFocusable(false);
        pane.setOpaque(false);
        pane.setBorder(null);
        pane.setCursor(Cursor.getDefaultCursor());
        pane.setToolTipText(value);

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs,
                centered ? StyleConstants.ALIGN_CENTER : StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);

        Dimension size = new Dimension(width, height);
        pane.setPreferredSize(size);
        pane.setMinimumSize(size);
        pane.setMaximumSize(size);
        pane.setAlignmentX(centered ? Component.CENTER_ALIGNMENT : Component.LEFT_ALIGNMENT);
        return pane;
    }
}