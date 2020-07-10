package com.mynote.text;

import com.mynote.content.ContentPane;
import com.mynote.resizable.ResizablePolicy;
import com.mynote.textattr.SettableTextPane;
import com.mynote.resizable.ResizableWrapper;

import javax.swing.*;

public class TextPanePolicy extends ResizablePolicy {

    public TextPanePolicy(int mW, int mH) {
        super(mW, mH);
    }

    @Override
    public void shifting(ResizableWrapper wrapper, int dx, int dy) {
        super.shifting(wrapper, dx, dy);
    }

    @Override
    public void resizeE(ResizableWrapper wrapper, int dx) {
        super.resizeE(wrapper, dx);
        share(wrapper);
    }

    @Override
    public void resizeW(ResizableWrapper wrapper, int dx) {
        super.resizeW(wrapper, dx);
        share(wrapper);
    }

    private void share(ResizableWrapper wrapper) {
        SettableTextPane child = (SettableTextPane)wrapper.comp;
        resizeAccordingToChild(wrapper, child);
    }

    public void resizeAccordingToChild(ResizableWrapper wrapper, SettableTextPane child) {
        int topDownHeight = wrapper.getResizableBorder().dist * 2;
        SwingUtilities.invokeLater(() -> {
            wrapper.setSize(wrapper.getWidth(), topDownHeight + child.getPreferredSize().height);
            wrapper.revalidate();
        });
    }

    public void adjustParentWindowSize(ResizableWrapper wrapper) {
        ContentPane parent = (ContentPane) wrapper.getParent().getParent().getParent();
        parent.adjustSize();
    }
}