package com.mynote.color;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.mynote.popup.PopupBinder;
import com.mynote.textattr.SetterInterface;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public abstract class ColorSelector extends JPanel {

    public static class ColorDisplay extends JPanel {
        private Object currentVal = Color.white;

        @Override
        protected void paintComponent(Graphics g) {
            drawPattern(g);
        }

        private void drawPattern(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (SetterInterface.invalidState.equals(currentVal)) { /*no color*/
                paintBackground(g, new Color(238, 238, 238));
            } else {
                Color color = (Color) currentVal;
                if (color.getAlpha() == 0) {    /*transparent (slash)*/
                    paintBackground(g, Color.white);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.red);
                    g2.setStroke(new BasicStroke(3));
                    g2.draw(new Line2D.Float(1, 1, w - 3, h - 3));
                }
                else {  /*fill rect*/
                    paintBackground(g, color);
                }
            }
        }

        private void paintBackground(Graphics g, Color color) {
            g.setColor(color);
            g.fillRoundRect(1,1, getWidth() - 3, getHeight() - 3, 4, 4);
        }

        public void setBackgroundColor (Object o) {
            currentVal = o;
            repaint();
            getParent().repaint();
        }
    }

    private class MouseTransferListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            ActionEvent ae = new ActionEvent(arrowButton, 0, "");
            arrowButton.getActionListeners()[0].actionPerformed(ae);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            passEventToArrowButton(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            passEventToArrowButton(e);
        }

        private void passEventToArrowButton(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent(displayPane, e, arrowButton);
            arrowButton.dispatchEvent(e);
        }
    }

    private MouseTransferListener transferListener = new MouseTransferListener();

    public final ColorDisplay displayPane = new ColorDisplay();

    public final FlatArrowButton arrowButton = new FlatArrowButton(BasicArrowButton.SOUTH, "chevron",
            new Color(104, 104, 104), new Color(172, 172, 172),
            new Color(155, 155, 155), new Color(0, true));

    public final ColorPopup colorPopup;

    public ColorSelector(String title) {
        super(new BorderLayout());
        colorPopup = new ColorPopup(title) {
            @Override
            protected void doSomethingWithColor(Color color) {
                displayPane.setBackgroundColor(color);
                ColorSelector.this.doSomethingWithColor(color);
            }
        };

        setBackground(Color.white);

        setPreferredSize(new Dimension(45, 12));
        setMinimumSize(new Dimension(45, 12));
        displayPane.setPreferredSize(new Dimension(25, 11));
        displayPane.addMouseListener(transferListener);
        PopupBinder.bind(arrowButton, colorPopup, this);

        add(displayPane, BorderLayout.WEST);
        add(arrowButton, BorderLayout.CENTER);
    }

    public abstract void doSomethingWithColor(Color color);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.lightGray);
        g2.drawRoundRect(0,0, getWidth() - 1, getHeight() - 1, 4, 4);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setBackground(enabled ? Color.white : new Color(242, 242, 242));
        arrowButton.setEnabled(enabled);

        int listenerCount = displayPane.getMouseListeners().length;

        if (enabled) {
            if (listenerCount == 0) {
                displayPane.addMouseListener(transferListener);
            }
        }
        else {
            if (listenerCount > 0) {
                displayPane.removeMouseListener(transferListener);
            }
        }
    }
}
