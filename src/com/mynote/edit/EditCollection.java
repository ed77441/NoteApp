package com.mynote.edit;

import com.mynote.tool.AbstractSetterCollection;
import com.mynote.common.HighQualityIconButton;
import com.mynote.common.NoteUtil;
import com.mynote.common.Wrappable;
import com.mynote.resizable.ResizableWrapper;
import com.mynote.file.InfoExtractable;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

import static com.mynote.main.NoteApp.keyHandler;
public class EditCollection extends AbstractSetterCollection {
    public class WrapperFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            setEnabled(true);
            currentFocus = (ResizableWrapper) focusEvent.getSource();
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            setEnabled(false);
            currentFocus = null;
        }
    }

    public final FocusListener wrapperFocusListener = new WrapperFocusListener();
    ResizableWrapper  currentFocus = null;
    private JButton deleteButton = new HighQualityIconButton("DELETE");
    private JButton cutButton = new HighQualityIconButton("CUT");
    private JButton copyButton = new HighQualityIconButton("COPY");

    public static final EditCollection collection = new EditCollection();

    private EditCollection() {
        addAll(Arrays.asList(deleteButton, cutButton, copyButton));

        deleteButton.setIcon(NoteUtil.loadImageIcon("/delete.png", 15, 15));
        deleteButton.addActionListener((ActionEvent actionEvent) -> delete());
        cutButton.setIcon(NoteUtil.loadImageIcon("/cut.png", 15, 15));
        cutButton.setToolTipText("Ctrl + X");
        cutButton.addActionListener((ActionEvent actionEvent) -> cut());
        copyButton.setIcon(NoteUtil.loadImageIcon("/copy.png", 15, 15));
        copyButton.setToolTipText("Ctrl + C");
        copyButton.addActionListener((ActionEvent actionEvent) -> copy());

        keyHandler.bind("Delete", this::delete);
        keyHandler.bind("Ctrl X", this::cut);
        keyHandler.bind("Ctrl C", this::copy);

        adjustSetterComponent();
        setEnabled(false);
    }

    private void delete() {
        if (currentFocus != null) {
            JPanel parent = (JPanel) currentFocus.getParent();
            parent.remove(currentFocus);
            parent.repaint();
            currentFocus = null;
        }
    }

    private void copy() {
        if (currentFocus != null) {
            InfoExtractable extractable = (InfoExtractable) currentFocus.comp;
            Wrappable comp = (Wrappable) extractable.convertToInfoObject().restoreFromObjectInfo();
            PasteCollection pCollection = PasteCollection.collection;
            pCollection.temporaryHeld = comp.createWrapper(currentFocus.getSize());
            pCollection.setEnabled(true);
            currentFocus = null;
        }
    }

    private void cut() {
        if (currentFocus != null) {
            JPanel parent = (JPanel) currentFocus.getParent();
            parent.remove(currentFocus);
            parent.repaint();
            PasteCollection pCollection = PasteCollection.collection;
            pCollection.temporaryHeld = currentFocus;
            pCollection.setEnabled(true);
            currentFocus = null;
        }
    }

    @Override
    protected String getSetterTitle() {
        return "Edit Action";
    }
}
