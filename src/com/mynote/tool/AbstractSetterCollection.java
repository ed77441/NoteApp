package com.mynote.tool;

import com.mynote.common.NoteUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;


public abstract class AbstractSetterCollection extends ArrayList<Component> {

    private Font customFont;

    public AbstractSetterCollection() {
        customFont = new Font("Amaranth", Font.PLAIN,  14);
    }

    public void setEnabled(boolean flag) {
        this.forEach(c -> c.setEnabled(flag));
    }

    protected void adjustSetterComponent() {
        for (Component component: this) {
            component.setFocusable(false);
            component.setFont(getSetterFont());
            component.setPreferredSize(new Dimension(component.getPreferredSize().width, 12));
        }
    }

    public static Font getSetterFont() {
        return new Font("JetBrains mono Medium", Font.PLAIN, 10);
    }

    public JPanel createSetterPane () {
        JPanel setterPane = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipady = 10;
        setConstraints(gbc, 0,0, 1 , 1);

        setterPane.add(get(0), gbc);
        gbc.insets = new Insets(0,3,0,0);
        int length = size();

        for (int i = 1; i < length; ++i) {
            setConstraints(gbc, i, 0 ,1 , 1);
            setterPane.add(get(i), gbc);
        }

        JLabel label = new JLabel(getSetterTitle());
        label.setFont(customFont);
        setConstraints(gbc, 0, 1, length,1);
        setterPane.add(label, gbc);

        return setterPane;
    }

    void setConstraints(GridBagConstraints gbc, int x, int y, int w, int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
    }

    protected abstract String getSetterTitle();
}
