package com.mynote.textattr;

import com.mynote.tool.AbstractSetterCollection;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FontFamilySetter extends ComboBoxSetter<String> {

    private void setFontOrFallBack(Component component, String string, Font font) {
        if (font.canDisplayUpTo(string) != -1) {
            component.setFont(new Font("Dialog", Font.PLAIN, 10));
        } else {
            component.setFont(font);
        }
    }

    private class TextFieldChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String selected = (String) getSelectedItem();
            if (selected != null) {
                Font font = AbstractSetterCollection.getSetterFont();
                setFontOrFallBack(FontFamilySetter.this, selected, font);
                setToolTipText(selected);
            }
        }
    }

    private class FontFamilyRenderer extends DefaultListCellRenderer {
        private Font baseFont = new JLabel("Test").getFont();
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String fontName = (String) value;
            Font font = new Font(fontName, baseFont.getStyle(), 10);
            setFontOrFallBack(this, fontName, font);
            setText(fontName);
            return this;
        }
    }

    FontFamilySetter() {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
                , StyleConstants.FontFamily);
        setRenderer(new FontFamilyRenderer());
        addActionListener(new TextFieldChangeListener());
        setPreferredSize(new Dimension(170, 15));
        setMinimumSize(new Dimension(170, 15));

        JComponent popup = (JComponent) getUI().getAccessibleChild(this, 0);
        popup.setPreferredSize(new Dimension(getPreferredSize().width, popup.getPreferredSize().height + 8));

        popup.repaint();
    }
}