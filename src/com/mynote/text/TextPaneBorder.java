package com.mynote.text;

import com.mynote.resizable.ResizableBorder;

import java.awt.*;
import java.util.ArrayList;

public class TextPaneBorder extends ResizableBorder {
    public TextPaneBorder() {
        super(8);
    }

    @Override
    protected ArrayList<BorderSection> getSections(int x, int y, int w, int h) {
        ArrayList<BorderSection> borderSections = new ArrayList<>();

        borderSections.add(new BorderSection(new Rectangle(x + dist / 2, y, w - dist , h), Color.lightGray, clearColor));
        borderSections.add(new BorderSection(new Rectangle(x + dist / 2, y, w - dist, dist), clearColor, Color.lightGray, Cursor.MOVE_CURSOR));
        borderSections.add(new BorderSection(new Rectangle(x, y + dist, dist, h - dist), Cursor.W_RESIZE_CURSOR));
        borderSections.add(new BorderSection(new Rectangle(x + w - dist, y + dist, dist, h - dist), Cursor.E_RESIZE_CURSOR));

        return borderSections;
    }

    @Override
    protected int getNotDefinedAreaCursor() {
        return Cursor.DEFAULT_CURSOR;
    }

}
