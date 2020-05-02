package com.mynote.popup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupBinder {

    static class BooleanWrapper {
        public boolean flag = false;
    }

    public static void bind(JButton targetButton, EmptyPopup popup, Component refPosition) {
        BooleanWrapper isPressed = new BooleanWrapper();

        targetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (popup.isShowing) {
                    popup.setVisible(false);
                    popup.isShowing = false;
                }
                else {
                    popup.show(refPosition, 0, refPosition.getHeight());
                }
            }
        });

        targetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed.flag = popup.isShowing;
            }
        });

        targetButton.setFocusable(false);
        popup.setFocusable(false);
        popup.putClientProperty("buttonFlag", isPressed);
    }
}
