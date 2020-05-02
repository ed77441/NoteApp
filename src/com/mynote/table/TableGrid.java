package com.mynote.table;

import com.mynote.common.NoteUtil;
import com.mynote.textattr.RangeAttributeSet;
import com.mynote.textattr.SettableTextPane;
import static com.mynote.textattr.TextSetterCollection.collection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class TableGrid  extends SettableTextPane {

    class ResizeListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            shared();
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            shared();
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {}

        void shared() {
            SwingUtilities.invokeLater(() -> {
                table.adjustGridBounds(table.getNewWidths());
                table.resizeParentHeight();
            });
        }
    }

    class SelectionListener extends MouseAdapter {
        Point startPos;
        TableGrid startTextPane;

        @Override
        public void mousePressed(MouseEvent e) {
            startTextPane = (TableGrid) e.getComponent();
            e = convertToParentPoint(e);
            startPos = e.getPoint();
            table.grids.forEach(TableGrid::resetSelected);
        }

        public void mouseDragged(MouseEvent e) {
            ArrayList<TableGrid> grids = table.grids;
            e = convertToParentPoint(e);
            Point nextPos = e.getPoint();

            Point upperLeft = new Point(Math.min(startPos.x, nextPos.x), Math.min(startPos.y, nextPos.y));
            Point bottomRight = new Point(Math.max(startPos.x, nextPos.x), Math.max(startPos.y, nextPos.y));
            Rectangle rect = new Rectangle(upperLeft);
            rect.add(bottomRight);

            rect.width = rect.width == 0 ? 1 : rect.width;
            rect.height = rect.height == 0 ? 1 : rect.height;

            grids.forEach(TableGrid::resetBackground);
            grids.forEach(TableGrid::resetSelected);
            grids.stream()
                    .filter(grid -> rect.intersects(grid.getBounds()))
                    .forEach(grid -> grid.selected = true);

            if (grids.stream()
                    .filter(grid -> grid.selected).count() >= 2) {
                startTextPane.caretDisabled = true;
                grids.forEach(TableGrid::setSelectedBackground);

                if (table.hasFocus()) {
                    collection.setCurrentState(table.getMultiSelectedState());
                }
                else {
                    table.grabFocus();
                }
            }
            else {
                startTextPane.caretDisabled = false;
                startTextPane.grabFocus();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            SwingUtilities.invokeLater(() -> startTextPane.caretDisabled = false);
        }

        MouseEvent convertToParentPoint(MouseEvent e) {
            Component component = e.getComponent();
            return SwingUtilities.convertMouseEvent(component, e, component.getParent());
        }
    }

    class GridBorder implements Border {

        int dist = 6;

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(dist, dist, dist, dist);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int i, int i1, int i2, int i3) {
            TableGrid grid = (TableGrid) component;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.gray);
            g2.setStroke(new BasicStroke(1));

            i += 1;
            i1 += 1;

            g2.drawLine(i, i1, i, i1 + i3);
            g2.drawLine(i, i1, i + i2, i1);

            i -= 2;
            i1 -= 2;

            if (grid.j == table.col - 1) {
                g2.drawLine(i + i2, i1, i + i2, i1 + i3);
            }

            if (grid.i == table.row - 1) {
                g2.drawLine(i , i1 + i3, i + i2, i1 + i3);
            }
        }
    }

    TablePane table;
    boolean selected = false;
    final int i, j;

    void initGrid () {
        MouseAdapter selectionListener = new SelectionListener();
        getDocument().addDocumentListener(new ResizeListener());
        addMouseListener(selectionListener);
        addMouseMotionListener(selectionListener);
        setBorder(new GridBorder());
    }

    TableGrid(TablePane table, int i, int j) {
        this.table = table;
        this.i = i; this.j = j;
        initGrid();
    }

    TableGrid(TablePane table, int i, int j, String text, MutableAttributeSet currentAttr, ArrayList<RangeAttributeSet> sets) {
        super(text, currentAttr, sets);
        this.table = table;
        this.i = i; this.j = j;
        initGrid();
    }

    @Override
    protected void resize() {
        table.adjustGridBounds(table.getIntArrayList(
                table.getRow(0).stream().mapToInt(Component::getWidth)
        ));
        table.resizeParentHeight();
    }

    void resetSelected() {
        selected = false;
    }

    void resetBackground() {
        NoteUtil.setTransparent(this);
    }

    void setSelectedBackground() {
        if (selected) {
            setOpaque(true);
            setBackground(new Color(200, 200, 200));
        }
    }
}
