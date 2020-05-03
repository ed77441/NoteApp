package com.mynote.content;

import com.mynote.edit.EditCollection;
import com.mynote.canvas.CanvasPane;
import com.mynote.canvas.CanvasPanePolicy;
import com.mynote.text.TextPane;
import com.mynote.resizable.ResizableWrapper;
import com.mynote.text.TextPanePolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ContentPane extends JScrollPane {

    class CreateTextPaneListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            TextPane textPane = new TextPane();
            addComponent(textPane.createWrapper(new Dimension(150, 31)),
                    new Point(e.getX() - 10, e.getY() - 20));
            textPane.grabFocus();
            textPane.resizeWrapper();
        }
    }

    public JPanel innerPane = new JPanel();
    public ContentPane() {
        getViewport().setView(innerPane);

        innerPane.setLayout(null);
        innerPane.addMouseListener(new CreateTextPaneListener());
        innerPane.setPreferredSize(contentWindowMinimumSize);
        innerPane.setFocusable(false);
    }

    static final Dimension contentWindowMinimumSize;

    static {
        Rectangle rectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        contentWindowMinimumSize = new Dimension(rectangle.width, rectangle.height - 130);
    }


    public Insets insets = new Insets(0, 0, 0, 0);

    public void adjustScrollbar(ResizableWrapper wrapper, int action) {
        Rectangle viewBox = getViewport().getViewRect();

        Point mousePoint =  MouseInfo.getPointerInfo().getLocation();
        Point topLeft = getLocationOnScreen();
        Point bottomRight = new Point(topLeft);
        bottomRight.translate(getWidth(), getHeight());
        JScrollBar hScrollBar = getHorizontalScrollBar();
        JScrollBar vScrollBar = getVerticalScrollBar();

        ArrayList<Integer> wActions = new ArrayList<>(Arrays.asList(Cursor.W_RESIZE_CURSOR,
                Cursor.SW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR));
        ArrayList<Integer> nActions = new ArrayList<>(Arrays.asList(Cursor.N_RESIZE_CURSOR,
                Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR));
        ArrayList<Integer> eActions = new ArrayList<>(Arrays.asList(Cursor.E_RESIZE_CURSOR,
                Cursor.NE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR));
        ArrayList<Integer> sActions = new ArrayList<>(Arrays.asList(Cursor.S_RESIZE_CURSOR,
                Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR));

        if (bottomRight.x - 20 < mousePoint.x &&
                (wrapper.getX() + wrapper.getWidth() > viewBox.x + viewBox.width  || !wActions.contains(action))) {
            if (hScrollBar.getValue() + hScrollBar.getVisibleAmount() ==
                    hScrollBar.getMaximum()) {
                adjustPreferredWidth(10);
                insets.right += 10;
            }
            hScrollBar.setValue(hScrollBar.getValue() + 10);
        }

        if (mousePoint.x < topLeft.x + 20 &&
                (wrapper.getX()  <  viewBox.x  || !eActions.contains(action))) {
            if (hScrollBar.getValue() == 0) {
                adjustPreferredWidth(10);
                insets.left += 10;

                getComponentList().stream()
                        .filter(comp -> comp != wrapper)
                        .forEach(comp -> comp.setLocation(comp.getX() + 10, comp.getY()));
            }

            if (wActions.contains(action)) {
                wrapper.setSize(wrapper.getWidth() + 10 , wrapper.getHeight());
                if (wrapper.comp instanceof CanvasPane) {
                    ((CanvasPanePolicy)(wrapper.getPolicy())).shiftPolyLines(wrapper, 10, 0);
                }
            }

            hScrollBar.setValue(hScrollBar.getValue() - 10);
        }

        if (bottomRight.y - 10 < mousePoint.y &&
                (wrapper.getY() + wrapper.getHeight() > viewBox.y + viewBox.height  || !nActions.contains(action))) {
            if (vScrollBar.getValue() + vScrollBar.getVisibleAmount() ==
                    vScrollBar.getMaximum()) {
                adjustPreferredHeight(10);
                insets.bottom += 10;
            }
            vScrollBar.setValue(vScrollBar.getValue() + 10);
        }


        if (mousePoint.y < topLeft.y + 10 &&
                (wrapper.getY()  <  viewBox.y  || !sActions.contains(action))) {
            if (vScrollBar.getValue() == 0) {
                adjustPreferredHeight(10);
                insets.top += 10;

                getComponentList().stream()
                        .filter(comp -> comp != wrapper)
                        .forEach(comp -> comp.setLocation(comp.getX(), comp.getY() + 10));
            }

            if (nActions.contains(action)) {
                wrapper.setSize(wrapper.getWidth(), wrapper.getHeight() + 10);
                if (wrapper.comp instanceof CanvasPane) {
                    ((CanvasPanePolicy)(wrapper.getPolicy())).shiftPolyLines(wrapper, 0, 10);
                }
            }

            vScrollBar.setValue(vScrollBar.getValue() - 10);
        }

        innerPane.revalidate();
    }

    public void adjustSize() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        Component [] components = innerPane.getComponents();

        for (Component component: components) {
            int x = component.getX();
            int y = component.getY();
            int w = x + component.getWidth();
            int h = y + component.getHeight();
            minX = Math.min(x, minX);
            minY = Math.min(y, minY);
            maxX = Math.max(w, maxX);
            maxY = Math.max(h, maxY);
        }

        if (minX < 0 ) {
            shiftComponents(-minX, 0);
            adjustPreferredWidth(-minX);
            insets.left -= minX;
        }
        else {
            int minValue = Math.min(insets.left, minX);
            shiftComponents(-minValue, 0);
            adjustPreferredWidth(-minValue);
            insets.left -= minValue;
        }

        if (minY < 0 ) {
            shiftComponents(0, -minY);
            adjustPreferredHeight(-minY);
            insets.top -= minY;
        }
        else {
            int minValue = Math.min(insets.top, minY);
            shiftComponents(0, -minValue);
            adjustPreferredHeight(-minValue);
            insets.top -= minValue;
        }

        if (maxX > innerPane.getPreferredSize().width) {
            int diffX = maxX - innerPane.getPreferredSize().width;
            adjustPreferredWidth(diffX);
            insets.right += diffX;
        }
        else {
            int minValue = Math.min(insets.right, innerPane.getPreferredSize().width - maxX);
            adjustPreferredWidth(-minValue);
            insets.right -= minValue;
        }

        if (maxY > innerPane.getPreferredSize().height) {
            int diffY = maxY - innerPane.getPreferredSize().height;
            adjustPreferredHeight(diffY);
            insets.bottom += diffY;
        }
        else {
            int minValue = Math.min(insets.bottom, innerPane.getPreferredSize().height - maxY);
            adjustPreferredHeight(-minValue);
            insets.bottom -= minValue;
        }

        innerPane.repaint();
    }

    private void adjustPreferredWidth(int offsetX) {
        Dimension preferredSize =  innerPane.getPreferredSize();
        int newWidth = preferredSize.width + offsetX;
        newWidth = Math.max(contentWindowMinimumSize.width, newWidth);
        innerPane.setPreferredSize(new Dimension(newWidth, preferredSize.height));
    }

    private void adjustPreferredHeight (int offsetY) {
        Dimension preferredSize =  innerPane.getPreferredSize();
        int newHeight = preferredSize.height + offsetY;
        newHeight = Math.max(contentWindowMinimumSize.height, newHeight);
        innerPane.setPreferredSize(new Dimension(preferredSize.width, newHeight));
    }

    private ArrayList<Component> getComponentList() {
        return new ArrayList<>(Arrays.asList(innerPane.getComponents()));
    }

    private void shiftComponents(int dx, int dy) {
        getComponentList().
                forEach(comp -> comp.setLocation(comp.getX() + dx, comp.getY() + dy));
    }

    public void addComponent(ResizableWrapper wrapper, Point position) {

        if (position == null) {
            Rectangle viewBox = getViewport().getViewRect();
            Point targetPoint = viewBox.getLocation();
            targetPoint.translate(100,100);
            position = targetPoint;
        }

        wrapper.setLocation(position);
        innerPane.add(wrapper);
        innerPane.revalidate();
        innerPane.repaint();
        wrapper.grabFocus();
    }
}
