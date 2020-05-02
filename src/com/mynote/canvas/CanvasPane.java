package com.mynote.canvas;

import com.mynote.common.NoteUtil;
import com.mynote.file.ComponentInfo;
import com.mynote.file.InfoExtractable;
import com.mynote.common.Wrappable;
import com.mynote.resizable.ResizableWrapper;
import com.mynote.textattr.SetterInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Collectors;

import static com.mynote.canvas.CanvasSetterCollection.collection;

public class CanvasPane extends JPanel implements Wrappable, InfoExtractable {
    abstract class MouseFocusAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            collection.setEnabled(true);
            collection.setCurrentState(color, thickness);
            if (hasFocus()) {
                mousePressedFocused(e);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (hasFocus()) {
                mouseDraggedFocused(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            grabFocus();
            if (currentListener == drawListener) {
                setCursor(drawCursor);
            }
            else if(currentListener == editListener) {
                setCursor(editCursor);
            }
            else {
                setCursor(eraseCursor);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (hasFocus()) {
                mouseEnteredFocused(e);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getDefaultCursor());
        }

        public abstract void mousePressedFocused(MouseEvent e);
        public abstract void mouseDraggedFocused(MouseEvent e);
        public abstract void mouseEnteredFocused(MouseEvent e);
    }

    class DrawListener extends MouseFocusAdapter {
        PolyLine polyLine ;

        @Override
        public void mousePressedFocused(MouseEvent e) {
            polyLine = new PolyLine(color, thickness);
            polyLine.add(e.getPoint());
            polyLine.add(e.getPoint());
            polyLines.add(polyLine);
            repaint();
        }

        @Override
        public void mouseDraggedFocused(MouseEvent e) {
            polyLine.addAndRemoveRedundantPoint(e.getPoint());
            repaint();
        }

        @Override
        public void mouseEnteredFocused(MouseEvent e) {
            setCursor(drawCursor);
        }
    }

    class EditListener extends MouseFocusAdapter {

        Point start;
        boolean isNotSelected;

        @Override
        public void mousePressedFocused(MouseEvent e) {
            start = e.getPoint();
            isNotSelected = false;

            if (selectedPolyLines.isEmpty()) {
                addSelected(start);
            }
            else {
                if (selectedPolyLines.stream()
                        .noneMatch(line -> line.contains(start))) {
                    selectedPolyLines.clear();
                    addSelected(start);
                }
            }

            if (selectedPolyLines.isEmpty()) {
                isNotSelected = true;
            }
            else if (selectedPolyLines.size() == 1) {
                PolyLine selected = selectedPolyLines.get(0);
                collection.setCurrentState(selected.color, selected.thickness);
            }

            repaint();
        }

        @Override
        public void mouseDraggedFocused(MouseEvent e) {

            if (isNotSelected) {
                Point next = e.getPoint();
                Point topLeft = new Point(Math.min(start.x, next.x), Math.min(start.y, next.y));
                Point bottomRight = new Point(Math.max(start.x, next.x), Math.max(start.y, next.y));
                Rectangle rect = new Rectangle(topLeft);
                rect.add(bottomRight);
                if (rect.width == 0 || rect.height == 0) {
                    rect.setSize(Math.max(1, rect.width),  Math.max(1, rect.height));
                }

                selectedRect = rect;
                selectedPolyLines.clear();
                ArrayList <PolyLine> intersectWithRect = polyLines.stream()
                        .filter(line -> line.intersects(selectedRect))
                        .collect(Collectors.toCollection(ArrayList::new));

                if (intersectWithRect.isEmpty()) {
                    collection.setCurrentState(color, thickness);
                }
                else {
                    PolyLine first = intersectWithRect.get(0);
                    Color fColor = first.color;
                    int fThickness = first.thickness;

                    if (intersectWithRect.stream()
                            .allMatch(line -> line.color.equals(fColor))) {
                        collection.setColorState(fColor);
                    }
                    else {
                        collection.setColorState(SetterInterface.invalidState);
                    }

                    if (intersectWithRect.stream()
                            .allMatch(line -> line.thickness == fThickness)) {
                        collection.setThicknessState(fThickness);
                    }
                    else {
                        collection.setThicknessState(-1);
                    }
                }

                selectedPolyLines.addAll(intersectWithRect);
                repaint();
            }
            else {
                Point next = e.getPoint();
                selectedPolyLines.forEach(line -> line.shift(next.x - start.x, next.y - start.y));
                start = next;
                repaint();
            }
        }

        @Override
        public void mouseEnteredFocused(MouseEvent e) {
            setCursor(editCursor);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            selectedRect = null;
            repaint();
        }
    }

    class EraseListener extends MouseFocusAdapter {
        private void removeAt(Point point) {
            Rectangle rectangle = new Rectangle(point.x - 2, point.y - 2, 4, 4);
            ArrayList<PolyLine> intersects = polyLines.stream()
                    .filter(line -> line.intersects(rectangle))
                    .collect(Collectors.toCollection(ArrayList::new));
            polyLines.removeAll(intersects);
            repaint();
        }

        @Override
        public void mousePressedFocused(MouseEvent e) {
            removeAt(e.getPoint());
        }

        @Override
        public void mouseDraggedFocused(MouseEvent e) {
            removeAt(e.getPoint());
        }

        @Override
        public void mouseEnteredFocused(MouseEvent e) {
            setCursor(eraseCursor);
        }
    }

    static final Cursor drawCursor = NoteUtil.createCustomCursor("/draw.png", new Dimension(20, 20));
    static final Cursor editCursor = NoteUtil.createCustomCursor("/tongs.png", new Dimension(25, 25));
    static final Cursor eraseCursor = NoteUtil.createCustomCursor("/erase.png", new Dimension(20, 20));

    ArrayList<PolyLine> polyLines = new ArrayList<>();
    ArrayList<PolyLine> selectedPolyLines = new ArrayList<>();
    Rectangle2D selectedRect = null;
    MouseAdapter drawListener = new DrawListener();
    MouseAdapter editListener = new EditListener();
    MouseAdapter eraseListener = new EraseListener();
    MouseAdapter currentListener = drawListener;

    Color color = Color.black;
    int thickness = 1;

    public CanvasPane() {
        addMouseListener(currentListener);
        addMouseMotionListener(currentListener);
        addFocusListener(collection.canvasFocusListener);
        NoteUtil.setTransparent(this);
    }

    CanvasPane(Color color, int thickness, ArrayList<PolyLine> polyLines) {
        this();
        this.color = color;
        this.thickness = thickness;
        this.polyLines = polyLines;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        selectedPolyLines.forEach(line -> line.drawShadow(g2));
        polyLines.forEach(polyLine -> polyLine.draw(g2));

        if (selectedRect != null) {
            g2.setColor(new Color(180, 180, 180, 128));
            g2.fill(selectedRect);
        }
    }

    void shiftPolyLines(int dx, int dy) {
        for (PolyLine polyLine: polyLines) {
            polyLine.shift(dx, dy);
        }
    }

    public void setCurrentState(String name) {
        switch (name) {
            case "DRAW":
                setListener(drawListener);
                resetSelected();
                break;
            case "EDIT":
                setListener(editListener);
                break;
            case "ERASE":
                setListener(eraseListener);
                resetSelected();
                break;
        }
    }

    private void setListener(MouseAdapter listener) {
        removeMouseListener(currentListener);
        removeMouseMotionListener(currentListener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        currentListener = listener;
    }

    void resetSelected() {
        selectedPolyLines.clear();
        repaint();
    }

    private void addSelected(Point point) {
        PolyLine clicked = polyLines.stream()
                .filter(line -> line.contains(point))
                .findFirst()
                .orElse(null);

        if (clicked != null) {
            selectedPolyLines.add(clicked);
            repaint();
        }
    }

    @Override
    public ResizableWrapper createWrapper(Dimension size) {
        ResizableWrapper wrapper = new ResizableWrapper(this, new CanvasPanePolicy());
        wrapper.setSize(size);
        return wrapper;
    }

    @Override
    public ComponentInfo convertToInfoObject() {
        CanvasPaneInfo info = new CanvasPaneInfo();
        info.color = color;
        info.thickness = thickness;
        info.polyLines = polyLines;
        return info;
    }
}

