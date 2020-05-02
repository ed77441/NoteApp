package com.mynote.table;

import com.mynote.text.TextPaneBorder;

import java.awt.*;
import java.util.ArrayList;

public class TablePaneBorder extends TextPaneBorder {
    @Override
    protected ArrayList<BorderSection> getSections(int x, int y, int w, int h) {
        ArrayList<BorderSection> borderSections = new ArrayList<>();
        Color customGray = new Color(175, 175, 175);
        borderSections.add(new BorderSection(new Rectangle(x + dist - 1, y - dist + 1, w - 2 * dist + 2, h), customGray, clearColor));
        borderSections.add(new BorderSection(new Rectangle(x + dist - 1, y, w - 2 * dist + 2, dist), clearColor, customGray, Cursor.MOVE_CURSOR));
        borderSections.add(new BorderSection(new Rectangle(x + w - dist, y , dist / 2, h - dist + 2), Cursor.E_RESIZE_CURSOR));

        return borderSections;
    }
}
