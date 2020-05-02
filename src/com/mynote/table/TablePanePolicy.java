package com.mynote.table;

import com.mynote.resizable.ResizablePolicy;
import com.mynote.resizable.ResizableWrapper;

import javax.swing.*;

public class TablePanePolicy extends ResizablePolicy {
    TablePanePolicy(int mW, int mH) {
        super(mW, mH);
    }

    @Override
    public void resizeE(ResizableWrapper wrapper, int dx) {
        super.resizeE(wrapper, dx);
        TablePane tp = (TablePane) wrapper.comp;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tp.adjustGridBounds(tp.getNewWidths());
                tp.resizeParentHeight();
            }
        });
    }
}
