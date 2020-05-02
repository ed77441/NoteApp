package com.mynote.content;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Highlighter;
import java.awt.event.*;

public class EditableLabel extends JTextField {
    public boolean active = false;

    private static final MouseAdapter doubleClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2) {
                EditableLabel label = (EditableLabel) mouseEvent.getComponent();
                if (label.active) {
                    label.selectAll();
                }
                else {
                    label.allowEdit(true);
                    label.grabFocus();
                }
            }
        }
    };

    private static final Action pressEnterListener = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            setDisableAndToolTip(actionEvent.getSource());
        }
    };

    private static final FocusAdapter loseFocusListener = new FocusAdapter() {
        public void focusLost(FocusEvent focusEvent) {
            setDisableAndToolTip(focusEvent.getSource());
        }
    };

    class CaretMoveListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            if (!active) {
                getHorizontalVisibility().setValue(0);
            }
        }
    }

    Highlighter highlighter;

    public EditableLabel(String t) {
        super(t);
        highlighter = getHighlighter();
        allowEdit(false);
        setBorder(null);
        addMouseListener(doubleClickListener);
        addFocusListener(loseFocusListener);
        addActionListener(pressEnterListener);
        getHorizontalVisibility().addChangeListener(new CaretMoveListener());
        setToolTipText(t);
    }

    private void allowEdit (boolean enable) {
        active = enable;
        setEditable(enable);
        setCaretPosition(enable ? getText().length() : 0);
        getCaret().setVisible(enable);
        setHighlighter(enable ? highlighter : null);
    }

    private static void setDisableAndToolTip(Object comp) {
        EditableLabel label = (EditableLabel) comp;
        label.allowEdit(false);
        label.setToolTipText(label.getText());
    }
}
