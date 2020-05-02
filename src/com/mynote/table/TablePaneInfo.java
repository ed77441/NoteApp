package com.mynote.table;

import com.mynote.file.ComponentInfo;
import com.mynote.textattr.RangeAttributeSet;

import javax.swing.text.MutableAttributeSet;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class TablePaneInfo implements Serializable, ComponentInfo {

    private static final long serialVersionUID = 6529685098267757690L;

    int row, col;
    ArrayList<String> texts;
    ArrayList<MutableAttributeSet> currentAttrs;
    ArrayList<ArrayList<RangeAttributeSet>> arrayOfSets;

    @Override
    public Component restoreFromObjectInfo() {
        return new TablePane(row, col, texts, currentAttrs, arrayOfSets);
    }
}
