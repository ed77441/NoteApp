package com.mynote.image;

import com.mynote.resizable.ResizableBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ImagePaneBorder extends ResizableBorder {
    public ImagePaneBorder() {
        super(10);
    }

    @Override
    protected ArrayList<BorderSection> getSections(int x, int y, int w, int h) {
        ArrayList<BorderSection> borderSections = new ArrayList<>();

        Point upperLeft = getPredefinedRect(x, y, w, h, SwingConstants.NORTH_WEST).getLocation();
        upperLeft.translate(dist / 2, dist / 2);
        Point bottomRight = getPredefinedRect(x, y, w, h, SwingConstants.SOUTH_EAST).getLocation();
        bottomRight.translate(dist / 2, dist / 2);

        Color customGrey = new Color(175, 175, 175);
        Color customWhite  = new Color(220, 220, 220);

        Rectangle outline = new Rectangle(upperLeft, new Dimension(bottomRight.x - upperLeft.x, bottomRight.y - upperLeft.y));
        borderSections.add(new BorderSection(outline, customGrey, clearColor));

        Rectangle outer = new Rectangle(outline.x - 1, outline.y - 1, outline.width + 2, outline.height + 2);
        Rectangle inner = new Rectangle(outline.x + 1, outline.y + 1, outline.width - 2, outline.height - 2);

        borderSections.add(new BorderSection(outer, customWhite, clearColor));
        borderSections.add(new BorderSection(inner, customWhite, clearColor));

        RoundRectangle2D rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.NORTH));

        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.N_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.S_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.WEST));
        borderSections.add(new BorderSection(rr, customWhite,  customGrey, Cursor.W_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.EAST));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.E_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.NORTH_WEST));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.NW_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.NORTH_EAST));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.NE_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH_EAST));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.SE_RESIZE_CURSOR));
        rr = convertToRR(getPredefinedRect(x, y, w, h, SwingConstants.SOUTH_WEST));
        borderSections.add(new BorderSection(rr, customWhite, customGrey, Cursor.SW_RESIZE_CURSOR));

        return borderSections;
    }

    @Override
    protected int getNotDefinedAreaCursor() {
        return Cursor.MOVE_CURSOR;
    }

    private RoundRectangle2D convertToRR(Rectangle r) {
        return new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 3, 3);
    }
}
