package com.mynote.textattr;

import com.mynote.tool.AbstractSetterCollection;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.Map;

public class TextSetterCollection extends AbstractSetterCollection {
    class StopTextPaneListener implements PopupMenuListener {
        @Override
        public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) { }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
            target.disableComponent();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
            target.enableComponent();
        }
    }

    class SettableFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            target = (Settable) focusEvent.getSource();
            setCurrentState(target.getFocusState());
            setEnabled(true);
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            setCurrentState(Settable.getDefaultTextAttr());
            setEnabled(false);
        }
    }

    public final FocusListener settableFocusListener = new SettableFocusListener();

    private FontSizeSetter fontSizeSetter = new FontSizeSetter();
    private FontFamilySetter fontFamilySetter = new FontFamilySetter();
    private ToggleButtonSetter boldSetter = new ToggleButtonSetter("B", StyleConstants.Bold);
    private ToggleButtonSetter italicSetter = new ToggleButtonSetter("I", StyleConstants.Italic);
    private ToggleButtonSetter underlineSetter = new ToggleButtonSetter("U", StyleConstants.Underline);
    private ColorSetter foregroundSetter = new ColorSetter("Foreground Color", StyleConstants.Foreground);
    private ColorSetter backgroundSetter = new ColorSetter("Background Color", StyleConstants.Background);

    public Settable target = null;

    private final ActionListener setterActionListener = (ActionEvent actionEvent) -> {
        SetterInterface si = (SetterInterface) actionEvent.getSource();
        target.setTextAttribute(si.getKey(), si.getVal());
    };

    public static TextSetterCollection collection = new TextSetterCollection();

    private TextSetterCollection() {
        addAll(Arrays.asList(fontSizeSetter,
            fontFamilySetter, boldSetter, italicSetter, underlineSetter, foregroundSetter, backgroundSetter));

        forEach(c -> ((SetterInterface) c).addAction(setterActionListener));
        adjustSetterComponent();

        PopupMenuListener stopTextPaneListener = new StopTextPaneListener();
        foregroundSetter.colorPopup.addPopupMenuListener(stopTextPaneListener);
        backgroundSetter.colorPopup.addPopupMenuListener(stopTextPaneListener);
        fontSizeSetter.addPopupMenuListener(stopTextPaneListener);
        fontFamilySetter.addPopupMenuListener(stopTextPaneListener);

        Font setterFont = AbstractSetterCollection.getSetterFont();
        boldSetter.setFont(setterFont.deriveFont(Font.BOLD));
        italicSetter.setFont(setterFont.deriveFont(Font.ITALIC));

        Map attributes = setterFont.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlineSetter.setFont(setterFont.deriveFont(attributes));
        setEnabled(false);
    }

    public void setCurrentState(MutableAttributeSet set) {
        for (Component setter: this) {
            SetterInterface si = (SetterInterface) setter;
            si.removeAction(setterActionListener);
            si.setGivenValue(set.getAttribute(si.getKey()));
            si.addAction(setterActionListener);
        }
    }

    @Override
    protected String getSetterTitle() {
        return "Text Attribute";
    }
}
