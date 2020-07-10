package com.mynote.resizable;

import com.mynote.content.ContentPane;
import com.mynote.edit.EditCollection;

import javax.swing.*;

import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import java.awt.Component;
import java.awt.event.*;
import java.awt.*;

import static com.mynote.main.NoteApp.mainWindow;

public class ResizableWrapper extends JComponent {
    private ResizablePolicy policy;
    private boolean isFocused = false;
    static private boolean isDragging = false;
    public Component comp ;
    private volatile boolean condition;
    LoseFocusListener loseFocusListener = new LoseFocusListener();

    public ResizableWrapper(Component component) {
        this(component, new ResizableBorder(8), new ResizablePolicy(200,100));
    }

    public ResizableWrapper(Component component, ResizablePolicy plc) {
        this(component, new ResizableBorder(8), plc);
    }

    public ResizableWrapper(Component component, ResizableBorder bd) {
        this(component, bd, new ResizablePolicy(200,100));
    }

    public ResizableWrapper(Component component, ResizableBorder border, ResizablePolicy policy) {
        comp = component;
        setLayout(new BorderLayout());
        add(comp, BorderLayout.CENTER);
        comp.setBounds(0,0,200,100);

        setBorder(border);
        setPolicy(policy);

        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);

        addMouseListener(mouseMIMOListener);
        comp.addMouseListener(mouseMIMOListener);

        addFocusListener(loseFocusListener);
        addFocusListener(EditCollection.collection.wrapperFocusListener);
        comp.addFocusListener(loseFocusListener);
        comp.setFocusable(true);
        recursiveAddListener(comp);
    }

    void recursiveAddListener(Component current) {
        if (current instanceof Container) {
            Component [] children = ((Container)current).getComponents();
            for (Component child: children) {
                child.setFocusable(true);
                child.addFocusListener(loseFocusListener);
                child.addMouseListener(mouseMIMOListener);
                recursiveAddListener(child);
            }
        }
    }

    public void setPolicy(ResizablePolicy plc) {
        policy = plc;
    }

    public ResizablePolicy getPolicy() {
        return policy;
    }

    public ResizableBorder getResizableBorder() {
        return (ResizableBorder) getBorder();
    }

    public void resize() {
        Component parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }

    class LoseFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            getResizableBorder().shouldBePainted = isFocused = true;
            resize();
            getParent().setComponentZOrder(ResizableWrapper.this, 0);
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            getResizableBorder().shouldBePainted  = isFocused = false;
            resize();
        }
    }

    MouseAdapter mouseMIMOListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (!isDragging) {
                getResizableBorder().shouldBePainted = true;
                resize();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (!isDragging) {
                getResizableBorder().shouldBePainted = isFocused;
                resize();
            }
        }
    };

    private int cursor;
    private Point startPos = null;

    MouseInputListener resizeListener = new MouseInputAdapter() {

        @Override
        public void mouseMoved(MouseEvent me) {
            ResizableBorder resizableBorder = (ResizableBorder) getBorder();
            setCursor(Cursor.getPredefinedCursor(resizableBorder.getCursor(me)));
        }

        @Override
        public void mousePressed(MouseEvent me) {
            Thread mouseRefresher = new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    while(condition) {
                        try {
                            Robot robot = new Robot();
                            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove(mousePoint.x + 1, mousePoint.y);
                            robot.mouseMove(mousePoint.x, mousePoint.y);
                            Thread.sleep(40);
                        }
                        catch (InterruptedException | AWTException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            condition = true;
            mouseRefresher.start();

            ResizableBorder resizableBorder = getResizableBorder();
            cursor = resizableBorder.getCursor(me);

            startPos = me.getPoint();

            isDragging = true;
            requestFocus();
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (startPos != null) {
                int dx = me.getX() - startPos.x;
                int dy = me.getY() - startPos.y;

                ResizableWrapper target = ResizableWrapper.this;
                policy.refreshCurrentAndNext(startPos, me.getPoint());

                switch (cursor) {

                    case Cursor.N_RESIZE_CURSOR:
                        policy.restrictAndResizeN(target, dy);
                        break;

                    case Cursor.S_RESIZE_CURSOR:
                        policy.restrictAndResizeS(target, dy);
                        break;

                    case Cursor.W_RESIZE_CURSOR:
                        policy.restrictAndResizeW(target, dx);
                        break;

                    case Cursor.E_RESIZE_CURSOR:
                        policy.restrictAndResizeE(target, dx);
                        break;

                    case Cursor.NW_RESIZE_CURSOR:
                        policy.restrictAndResizeNW(target, dx, dy);
                        break;

                    case Cursor.NE_RESIZE_CURSOR:
                        policy.restrictAndResizeNE(target, dx, dy);
                        break;

                    case Cursor.SW_RESIZE_CURSOR:
                        policy.restrictAndResizeSW(target, dx, dy);
                        break;

                    case Cursor.SE_RESIZE_CURSOR:
                        policy.restrictAndResizeSE(target, dx, dy);
                        break;

                    case Cursor.MOVE_CURSOR:
                        policy.shifting(target, dx, dy);
                        break;
                }

                mainWindow.getActiveContentPane().adjustScrollbar(ResizableWrapper.this, cursor);
                resize();
                setCursor(Cursor.getPredefinedCursor(cursor));
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            startPos = null;
            isDragging = false;
            policy.resizeFinished(ResizableWrapper.this, cursor);
            stopMouseRefresher();
            mainWindow.getActiveContentPane().adjustSize();
        }
    };

    public void stopMouseRefresher() {
        condition = false;
    }
}