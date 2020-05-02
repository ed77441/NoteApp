package com.mynote.textattr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToggleButtonSetter extends JToggleButton implements SetterInterface {
    Object key;

    ToggleButtonSetter(String title, Object key) {
        super(title);
        this.key = key;
        setPreferredSize(new Dimension(20, 15));
        setMinimumSize(new Dimension(20, 15));
    }

    @Override
    public void setGivenValue(Object o) {
        setSelected((Boolean)o);
    }

    @Override
    public void addAction(ActionListener a) {
        super.addActionListener(a);
    }

    @Override
    public void removeAction(ActionListener a) {
        super.removeActionListener(a);
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getVal() {return isSelected();}
}