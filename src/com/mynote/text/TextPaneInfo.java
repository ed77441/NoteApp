package com.mynote.text;

import com.mynote.file.ComponentInfo;
import com.mynote.textattr.RangeAttributeSet;

import javax.swing.text.MutableAttributeSet;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class TextPaneInfo implements Serializable, ComponentInfo {

    private static final long serialVersionUID = 6529685098267757689L;

    String text;
    MutableAttributeSet currentAttr;
    ArrayList<RangeAttributeSet> sets;

    @Override
    public Component restoreFromObjectInfo() {
        return new TextPane(text, currentAttr, sets);
    }
}
