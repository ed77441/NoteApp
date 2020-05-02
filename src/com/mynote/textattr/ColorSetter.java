package com.mynote.textattr;

import com.mynote.color.ColorSelector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorSetter extends ColorSelector implements SetterInterface {
    Object key;
    Color color;
    ActionListener actionListener;

    ColorSetter(String title, Object key) {
        super(title);
        this.key = key;
    }

    public void addAction(ActionListener a) {
        actionListener = a;
    }
    public void removeAction(ActionListener a) {
        actionListener = null;
    }
    public void setGivenValue(Object o) {
        displayPane.setBackgroundColor(o);
    }
    public Object getKey () {return key;}
    public Object getVal () {return color;}

    @Override
    public void doSomethingWithColor(Color color) {
        this.color = color;
        actionListener.actionPerformed(new ActionEvent(this, 0,""));
    }
}
