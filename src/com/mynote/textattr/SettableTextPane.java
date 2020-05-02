package com.mynote.textattr;

import static com.mynote.textattr.TextSetterCollection.collection;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import javax.swing.text.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class SettableTextPane extends WrapTextPane implements Settable {
    class TextAttrCaretListener implements CaretListener {
        int oldLength = 0;

        @Override
        public void caretUpdate(CaretEvent caretEvent) {
            /*Wait for insert update fire first*/
            SwingUtilities.invokeLater(() -> postponedCaretUpdate(caretEvent));
        }

        void postponedCaretUpdate (CaretEvent caretEvent) {
            int newLength = getText().length();

            /*isInputMethodEditor*/
            if (caretDisabled || isInputMethodEditor || oldLength != newLength) {
                oldLength = newLength;
                return;
            }

            int start = caretEvent.getMark();
            int end = caretEvent.getDot();

            if (start > end) {
                int tmp = start;
                start = end;
                end = tmp;
            }

            if (start == end) { /*no select*/
                if (newLength > 0) {
                    currentAttr = getAttributeSetAt(start > 0 ? start - 1 : 0);
                }
                collection.setCurrentState(currentAttr);
            } else {    /*text selected!*/
                ArrayList<MutableAttributeSet> selected = new ArrayList<>(getArrayOfAttributeSet(start, end));
                collection.setCurrentState(Settable.getSelectedTextAttributeSet(selected));
            }
        }
    }

    class TextAttrDocumentFilter extends DocumentFilter implements Serializable {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset,
                                 String text, AttributeSet attr)
                throws BadLocationException {
            super.insertString(fb, offset, text, currentAttr);
            isInputMethodEditor = true;
        }

        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException{
            if (!isInputMethodEditor) {
                System.out.println(offset);
                currentAttr = new SimpleAttributeSet(getStyledDocument().getCharacterElement(offset).getAttributes());
                collection.setCurrentState(currentAttr);
            }
            super.remove(fb, offset, length);
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset,
                            int length, String text, AttributeSet attrs)
                throws BadLocationException {
            super.remove(fb, offset, length);
            super.replace(fb, offset, length, text, currentAttr);
            isInputMethodEditor = false;
        }
    }
    public MutableAttributeSet currentAttr = Settable.getDefaultTextAttr();

    boolean isInputMethodEditor ;
    public boolean caretDisabled = false;
    TextAttrCaretListener caretListener = new TextAttrCaretListener();

    public SettableTextPane() {
        initSettableTextPane();
    }

    public SettableTextPane (String text,
                             MutableAttributeSet currentAttr,
                             ArrayList<RangeAttributeSet> sets) {
        this.currentAttr = currentAttr;
        setText(text);
        setArrayOfAttributeSet(sets);
        caretListener.oldLength = text.length();
        initSettableTextPane();
    }

    protected void initSettableTextPane() {
        setBackground(new Color(0 , true));
        setOpaque(false);
        addCaretListener(caretListener);
        AbstractDocument document = (AbstractDocument) getDocument();
        document.setDocumentFilter(new TextAttrDocumentFilter());
        addFocusListener(collection.settableFocusListener);
    }

    public ArrayList<RangeAttributeSet> getArrayOfAttributeSet(int start, int end) {
        ArrayList <RangeAttributeSet> sets = new ArrayList<>();
        MutableAttributeSet startAttr = getAttributeSetAt(start);
        int startPos = start;

        for (int i = start + 1; i < end; ++i) {
            MutableAttributeSet nextAttr = getAttributeSetAt(i);

            if (!startAttr.equals(nextAttr)) {
                sets.add(new RangeAttributeSet(startAttr, startPos, i - startPos));
                startPos = i;
                startAttr = nextAttr;
            }
        }
        sets.add(new RangeAttributeSet(startAttr, startPos, end - startPos));
        return start >= end ? new ArrayList<>() : sets;
    }

    public void setArrayOfAttributeSet(ArrayList<RangeAttributeSet> sets) {
        for (RangeAttributeSet set: sets) {
            getStyledDocument().setCharacterAttributes(set.start, set.length, set, false);
        }
    }

    MutableAttributeSet getAttributeSetAt(int pos) {
        return new SimpleAttributeSet(getStyledDocument().getCharacterElement(pos).getAttributes());
    }

    protected void resize () {}

    @Override
    public void setTextAttribute(Object o, Object o1) {
        if (getSelectedText() != null) {
            int start = getSelectionStart();
            int length = getSelectionEnd() - start;
            MutableAttributeSet set = new SimpleAttributeSet();
            set.addAttribute(o, o1);
            getStyledDocument().setCharacterAttributes(start, length, set, false);
            resize();
        }
        else {
            currentAttr.addAttribute(o, o1);
        }
    }

    @Override
    public void disableComponent() {
        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT"};
        for (String key : keys) {
            getInputMap().put(KeyStroke.getKeyStroke(key), "none");
        }
        setEditable(false);
    }

    @Override
    public void enableComponent() {
        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT"};
        InputMap defaultInputMap = new InputMap();

        for (String key : keys) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
            getInputMap().put(keyStroke, defaultInputMap.get(keyStroke));
        }
        setEditable(true);
    }

    @Override
    public MutableAttributeSet getFocusState() {
        MutableAttributeSet result = currentAttr;
        if (getSelectedText() != null) {
            ArrayList<MutableAttributeSet> selected = new ArrayList<>(getArrayOfAttributeSet(getSelectionStart(), getSelectionEnd()));
            result = Settable.getSelectedTextAttributeSet(selected);
        }
        return result;
    }
}
