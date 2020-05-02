package com.mynote.color;

import com.mynote.common.NoteUtil;
import com.mynote.popup.EmptyPopup;
import com.mynote.main.NoteApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public abstract class ColorPopup extends EmptyPopup {

    abstract protected void doSomethingWithColor(Color color);

    private class DismissActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setVisible(false);
        }
    }

    private class ButtonHoverListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private class PredefinedColorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Component component = (Component) actionEvent.getSource();
            doSomethingWithColor(component.getBackground());
        }
    }

    private class CustomColorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            PaletteWindow palette = new PaletteWindow();
            JOptionPane optionPane = new JOptionPane(palette, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
            palette.colorResult.setOpaque(true);

            JDialog dialog = optionPane.createDialog(NoteApp.mainWindow, "Custom Color Palette");
            dialog.setFont(new Font("Jetbrains mono", Font.PLAIN, 12));
            dialog.setFocusableWindowState(false);
            dialog.setVisible(true);

            if (optionPane.getValue() != null &&
                    optionPane.getValue().equals(JOptionPane.OK_OPTION)) {
                doSomethingWithColor(palette.colorResult.getBackground());
            }
        }
    }

    private ActionListener dismissActionListener = new DismissActionListener();
    private MouseListener buttonHoverListener = new ButtonHoverListener();
    private ActionListener predefinedColorListener = new PredefinedColorListener();

    private Color [] colors = new Color[] {
        Color.black,
        new Color(51,51,51),
        Color.gray,
        Color.lightGray,
        Color.white,

        Color.red,
        Color.magenta,
        Color.orange,
        Color.yellow,
        Color.pink,

        Color.green,
        Color.cyan,
        Color.blue,
        new Color(153, 255, 153),
        new Color(0, 153, 51) ,

        new Color(204, 255, 102),
        new Color(0, 204, 255),
        new Color(153, 51, 255),
        new Color(153, 0, 204),
        new Color(102, 0, 102),

        new Color(255, 204, 204),
        new Color(255, 255, 204),
        new Color(204, 255, 255),
        new Color(204, 204, 255),
        new Color(255, 204, 255)
    };

    private final int windowWidth = 250;

    ColorPopup(String titleText) {
        super(titleText);

        setPopUpWindowSize(windowWidth, 250);
        add(createButtonPane());

        JMenuItem transparentColorOption = new JMenuItem("TRANSPARENT COLOR");
        transparentColorOption.setPreferredSize(new Dimension(windowWidth, 25));
        transparentColorOption.addActionListener(predefinedColorListener);
        transparentColorOption.setBackground(new Color(0, true));
        transparentColorOption.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        add(transparentColorOption);

        JMenuItem customColorOption = new JMenuItem("CUSTOM COLOR");
        customColorOption.addActionListener(new CustomColorListener());
        customColorOption.setPreferredSize(new Dimension(windowWidth, 25));
        customColorOption.setFont(new Font("Comic Sans MS", Font.BOLD, 12));

        add(customColorOption);
    }


    private JPanel createButtonPane() {
        JPanel buttonPane = new JPanel(new GridLayout(5,5,20,10));
        buttonPane.setPreferredSize(new Dimension(windowWidth, 220));
        buttonPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        NoteUtil.setTransparent(buttonPane);

        for (Color color: colors) {
            JButton button = new JButton();
            button.setMaximumSize(new Dimension(50, 50));
            button.setBackground(color);
            button.setFocusable(false);
            button.addActionListener(dismissActionListener);
            button.addActionListener(predefinedColorListener);
            button.addMouseListener(buttonHoverListener);
            buttonPane.add(button);
        }

        return buttonPane;
    }
}
