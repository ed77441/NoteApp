package com.mynote.textattr;

import com.mynote.tool.AbstractSetterCollection;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ComboBoxSetter<T> extends JComboBox<T> implements SetterInterface {
    Object key ;

    ComboBoxSetter(T [] items, Object key) {
        super(items);
        this.key = key;
        setFont(AbstractSetterCollection.getSetterFont());
    }

    @Override
    public void setGivenValue(Object o) {
        if (invalidState.equals(o)) {
            setSelectedIndex(-1);
        }
        else {
            setSelectedItem(o);
        }
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
    public Object getVal() {return getSelectedItem();}
}
