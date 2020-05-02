package com.mynote.resizable;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class ResizableBorder extends AbstractBorder {
    public static class BorderSection {

        public int cursor;
        public Shape shape;
        public Color foreground, background;
        boolean isVisible ;
        public BorderSection(Shape s, Color f, Color b) {
            this(s, f, b, -1);
        }

        public BorderSection(Shape s, Color f, Color b, int c) {
            shape = s;
            foreground = f;
            background = b;
            isVisible = true;
            cursor = c;
        }

        public BorderSection(Shape s, int c) {
            shape = s;
            isVisible = false;
            cursor = c;
        }
    }

    public int dist = 8;
    public boolean shouldBePainted = false;
    protected Color clearColor = new Color(0, true);

    public ResizableBorder(int dist) {
        this.dist = dist;
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(dist, dist, dist, dist);
    }

    @Override
    public void paintBorder(Component component, Graphics g, int x, int y,
                            int w, int h) {

        if (shouldBePainted) {
            for (BorderSection borderSection : getSections(x + 1, y + 1, w - 2, h - 2)) {
                if (borderSection.isVisible) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(borderSection.background);
                    g2.fill(borderSection.shape);
                    g2.setColor(borderSection.foreground);
                    g2.draw(borderSection.shape);
                }
            }
        }
    }

    protected Rectangle getPredefinedRect(int x, int y, int w, int h, int location) {

        switch (location) {

            case SwingConstants.NORTH:
                return new Rectangle(x + w / 2 - dist / 2, y, dist, dist - 1);

            case SwingConstants.SOUTH:
                return new Rectangle(x + w / 2 - dist / 2, y + h - dist + 1, dist, dist - 1);

            case SwingConstants.WEST:
                return new Rectangle(x, y + h / 2 - dist / 2, dist - 1, dist);

            case SwingConstants.EAST:
                return new Rectangle(x + w - dist + 1, y + h / 2 - dist / 2, dist - 1, dist);

            case SwingConstants.NORTH_WEST:
                return new Rectangle(x, y, dist - 1, dist - 1);

            case SwingConstants.NORTH_EAST:
                return new Rectangle(x + w - dist + 1, y, dist - 1, dist - 1);

            case SwingConstants.SOUTH_WEST:
                return new Rectangle(x, y + h - dist + 1, dist - 1, dist - 1);

            case SwingConstants.SOUTH_EAST:
                return new Rectangle(x + w - dist + 1, y + h - dist + 1, dist - 1, dist - 1);
        }
        return null;
    }
    protected ArrayList<BorderSection> getSections(int x, int y, int w, int h) {
        ArrayList<BorderSection> borderSections = new ArrayList<>();

        borderSections.add(new BorderSection(new Rectangle(x + dist / 2, y + dist / 2, w - dist, h - dist), Color.black, clearColor));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.NORTH_WEST), Color.black, Color.white, Cursor.NW_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.NORTH), Color.black, Color.white, Cursor.N_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.NORTH_EAST), Color.black, Color.white, Cursor.NE_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.EAST), Color.black, Color.white, Cursor.E_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH_EAST), Color.black, Color.white, Cursor.SE_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH), Color.black, Color.white, Cursor.S_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH_WEST), Color.black, Color.white, Cursor.SW_RESIZE_CURSOR));
        borderSections.add(new BorderSection(getPredefinedRect(x, y, w, h, SwingConstants.WEST), Color.black, Color.white, Cursor.W_RESIZE_CURSOR));

        return borderSections;
    }

    protected int getNotDefinedAreaCursor() {
        return Cursor.MOVE_CURSOR;
    }

    public int getCursor(MouseEvent me) {
        if (!shouldBePainted) {
            return getNotDefinedAreaCursor();
        }

        Component c = me.getComponent();
        int w = c.getWidth();
        int h = c.getHeight();

        for (BorderSection borderSection : getSections(0,0, w - 1, h - 1)) {
            Shape shape = borderSection.shape;

            if (borderSection.cursor != -1 && shape.contains(me.getPoint())) {
                return borderSection.cursor;
            }
        }

        return getNotDefinedAreaCursor();
    }
}