package com.mynote.edit;

import com.mynote.tool.AbstractSetterCollection;
import com.mynote.common.HighQualityIconButton;
import com.mynote.common.NoteUtil;
import com.mynote.content.ContentPane;
import com.mynote.main.NoteApp;
import com.mynote.resizable.ResizableWrapper;
import static com.mynote.main.NoteApp.keyHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class PasteCollection extends AbstractSetterCollection {
    private JButton pasteButton = new HighQualityIconButton("PASTE");
    ResizableWrapper temporaryHeld = null;
    public static final PasteCollection collection = new PasteCollection();

    private PasteCollection() {
        addAll(Arrays.asList(pasteButton));
        pasteButton.setIcon(NoteUtil.loadImageIcon("/paste.png", 15, 15));
        pasteButton.setToolTipText("Ctrl + V");
        pasteButton.addActionListener((ActionEvent actionEvent) -> paste());
        keyHandler.bind("Ctrl V", this::paste);
        adjustSetterComponent();
        setEnabled(false);
    }

    @Override
    protected String getSetterTitle() {
        return "Paste Action";
    }

    private void paste() {
        if (temporaryHeld != null) {
            ContentPane contentPane = NoteApp.mainWindow.getActiveContentPane();
            contentPane.addComponent(temporaryHeld, null);
            temporaryHeld = null;
            pasteButton.setEnabled(false);
        }
    }
}
