package com.mynote.text;

import com.mynote.file.ComponentInfo;
import com.mynote.file.InfoExtractable;
import com.mynote.common.Wrappable;
import com.mynote.textattr.RangeAttributeSet;
import com.mynote.textattr.SettableTextPane;
import com.mynote.resizable.ResizableWrapper;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class TextPane extends SettableTextPane implements InfoExtractable, Wrappable {
    class WrapperSizeDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            resizeWrapper();
            adjustParentWindow();
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            resizeWrapper();
            adjustParentWindow();
        }
        @Override
        public void changedUpdate(DocumentEvent e) { }
    }

    class RemoveWhenLoseFocus extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            if (getText().isEmpty()) {
                ResizableWrapper wrapper = (ResizableWrapper) getParent();
                Container contentPanel = wrapper.getParent();
                contentPanel.remove(wrapper);
                contentPanel.repaint();
                wrapper.stopMouseRefresher();
            }
        }
    }

    public TextPane() {
        addListeners();
    }

    TextPane (String text, MutableAttributeSet currentAttr, ArrayList<RangeAttributeSet> sets) {
        super(text, currentAttr, sets);
        addListeners();
    }

    void addListeners () {
        getDocument().addDocumentListener(new WrapperSizeDocumentListener());
        addFocusListener(new RemoveWhenLoseFocus());
    }

    public void resizeWrapper () {
        ResizableWrapper wrapper = (ResizableWrapper)getParent();
        TextPanePolicy policy = (TextPanePolicy)wrapper.getPolicy();
        policy.resizeAccordingToChild(wrapper, this);
    }

    void adjustParentWindow() {
        ResizableWrapper wrapper = (ResizableWrapper)getParent();
        TextPanePolicy policy = (TextPanePolicy)wrapper.getPolicy();
        policy.adjustParentWindowSize(wrapper);
    }

    @Override
    protected void resize() {
        resizeWrapper();
    }

    @Override
    public ComponentInfo convertToInfoObject() {
        TextPaneInfo componentInfo = new TextPaneInfo();
        componentInfo.currentAttr = currentAttr;
        String text = getText();
        componentInfo.text = text;
        componentInfo.sets = getArrayOfAttributeSet(0, text.length());
        return componentInfo;
    }

    public ResizableWrapper createWrapper(Dimension size) {
        ResizableWrapper wrapper = new ResizableWrapper(this,
                new TextPaneBorder(), new TextPanePolicy(150, 0));
        wrapper.setSize(size);
        return wrapper;
    }
}
