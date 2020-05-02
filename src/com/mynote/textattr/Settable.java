package com.mynote.textattr;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public interface Settable {
    void setTextAttribute(Object o, Object o1);
    void disableComponent();
    void enableComponent();
    MutableAttributeSet getFocusState();

    static MutableAttributeSet getDefaultTextAttr() {
        MutableAttributeSet defaultTextAttr = new SimpleAttributeSet();
        Object [] keys = new Object[] { StyleConstants.Foreground, StyleConstants.Background,
                StyleConstants.FontSize, StyleConstants.FontFamily,
                StyleConstants.Bold, StyleConstants.Italic, StyleConstants.Underline};

        Object [] vals = new Object[] { new Color(51), new Color(0, true),
                12, "Gotham Rounded Medium",
                false, false, false};

        for (int i = 0; i < keys.length; ++i) {
            defaultTextAttr.addAttribute(keys[i], vals[i]);
        }

        return defaultTextAttr;
    }

    private static ArrayList<Object> extractAttribute(Object o, ArrayList<MutableAttributeSet> sets) {
        ArrayList<Object> specificAttr = new ArrayList<>();
        for (MutableAttributeSet s: sets) {
            specificAttr.add(s.getAttribute(o));
        }
        return specificAttr;
    }

    private static boolean isAllEquals(ArrayList<Object> list) {
        Object first = list.get(0);
        return Collections.frequency(list, first) == list.size();
    }

    private static boolean isAllTrue(ArrayList<Object> list) {
        return !list.contains(false);
    }

    static MutableAttributeSet getSelectedTextAttributeSet(ArrayList<MutableAttributeSet> selected) {
        MutableAttributeSet newState = new SimpleAttributeSet();
        if (selected.isEmpty()) {
            return getDefaultTextAttr();
        }

        Object[] keys = new Object[]{StyleConstants.FontSize, StyleConstants.FontFamily,
                StyleConstants.Foreground, StyleConstants.Background};
        for (Object key : keys) {
            ArrayList<Object> specificAttrs = Settable.extractAttribute(key, selected);
            newState.addAttribute(key, Settable.isAllEquals(specificAttrs) ?
                    specificAttrs.get(0) : SetterInterface.invalidState);
        }

        keys = new Object[]{StyleConstants.Bold, StyleConstants.Italic, StyleConstants.Underline};
        for (Object key : keys) {
            ArrayList<Object> specificAttrs = Settable.extractAttribute(key, selected);
            newState.addAttribute(key, Settable.isAllTrue(specificAttrs));
        }

        return newState;
    }
}
