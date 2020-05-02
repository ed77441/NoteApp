package com.mynote.resizable;

import java.awt.*;
import java.io.Serializable;

public class ResizablePolicy implements Serializable {

    protected int minWidth, minHeight;
    protected Point currentPos, nextPos;

    public ResizablePolicy (int mW, int mH) {
        minWidth = mW;
        minHeight = mH;
    }

    protected final int restrictN (ResizableWrapper wrapper, int dy) {
        int remaining =  wrapper.getHeight() - minHeight;
        return Math.min(dy, remaining);
    }

    protected final int restrictS (ResizableWrapper wrapper, int dy) {
        int remaining = wrapper.getHeight() - minHeight;
        return Math.max(dy, -remaining);
    }

    protected final int restrictW (ResizableWrapper wrapper, int dx) {
        int remaining = wrapper.getWidth() - minWidth;
        return Math.min(dx, remaining);
    }

    protected final int restrictE (ResizableWrapper wrapper, int dx) {
        int remaining = wrapper.getWidth() - minWidth;
        return Math.max(dx, -remaining);
    }


    protected final void implResizeN(ResizableWrapper wrapper, int dy) {
        wrapper.setBounds(wrapper.getX(), wrapper.getY() + dy, wrapper.getWidth(), wrapper.getHeight() - dy);
    }

    protected final void implResizeS(ResizableWrapper wrapper, int dy) {
        wrapper.setBounds(wrapper.getX(), wrapper.getY(), wrapper.getWidth(), wrapper.getHeight() + dy);
    }

    protected final void implResizeW(ResizableWrapper wrapper, int dx) {
        wrapper.setBounds(wrapper.getX() + dx, wrapper.getY(), wrapper.getWidth() - dx, wrapper.getHeight());
    }

    protected final void implResizeE(ResizableWrapper wrapper, int dx) {
        wrapper.setBounds(wrapper.getX(), wrapper.getY(), wrapper.getWidth() + dx, wrapper.getHeight());
    }

    public void restrictAndResizeN(ResizableWrapper wrapper, int dy) {
        resizeN(wrapper, restrictN(wrapper, dy));
    }

    public void restrictAndResizeS(ResizableWrapper wrapper, int dy) {
        int offSetY = restrictS(wrapper, dy);
        resizeS(wrapper, offSetY);
        currentPos.y += offSetY;
    }

    public void restrictAndResizeW(ResizableWrapper wrapper, int dx) {
        resizeW(wrapper, restrictW(wrapper, dx));
    }

    public void restrictAndResizeE(ResizableWrapper wrapper, int dx) {
        int offSetX = restrictE(wrapper, dx);
        resizeE(wrapper, offSetX);
        currentPos.x += offSetX;
    }

    public void restrictAndResizeNW(ResizableWrapper wrapper, int dx, int dy) {
        resizeNW(wrapper, restrictW(wrapper, dx) , restrictN(wrapper, dy));
    }

    public void restrictAndResizeNE(ResizableWrapper wrapper, int dx, int dy) {
        int offSetX = restrictE(wrapper, dx);
        resizeNE(wrapper, offSetX, restrictN(wrapper, dy));
        currentPos.x += offSetX;
    }

    public void restrictAndResizeSE(ResizableWrapper wrapper, int dx, int dy) {
        int offSetX = restrictE(wrapper, dx);
        int offSetY = restrictS(wrapper, dy);
        resizeSE(wrapper, offSetX, offSetY);
        currentPos.translate(offSetX, offSetY);
    }


    public void restrictAndResizeSW(ResizableWrapper wrapper, int dx, int dy) {
        int offSetY = restrictS(wrapper, dy);
        resizeSW(wrapper, restrictW(wrapper, dx) , offSetY);
        currentPos.y += offSetY;
    }


    public void resizeN(ResizableWrapper wrapper, int dy) {
        implResizeN(wrapper, dy);
    }

    public void resizeS(ResizableWrapper wrapper, int dy) {
        implResizeS(wrapper, dy);
    }


    public void resizeW(ResizableWrapper wrapper, int dx) {
        implResizeW(wrapper, dx);
    }

    public void resizeE(ResizableWrapper wrapper, int dx) {
        implResizeE(wrapper, dx);
    }

    public void resizeNW(ResizableWrapper wrapper, int dx, int dy) {
        implResizeN(wrapper, dy);
        implResizeW(wrapper, dx);
    }

    public void resizeNE(ResizableWrapper wrapper, int dx, int dy) {
        implResizeN(wrapper, dy);
        implResizeE(wrapper, dx);
    }

    public void resizeSE(ResizableWrapper wrapper, int dx, int dy) {
        implResizeS(wrapper, dy);
        implResizeE(wrapper, dx);
    }
    public void resizeSW(ResizableWrapper wrapper, int dx, int dy) {
        implResizeS(wrapper, dy);
        implResizeW(wrapper, dx);
    }

    public void refreshCurrentAndNext(Point c, Point n) {
        currentPos = c;
        nextPos = n;
    }

    public void shifting(ResizableWrapper wrapper, int dx, int dy) {
        if (dx != 0 || dy != 0) {
            Rectangle bounds = wrapper.getBounds();
            bounds.translate(dx, dy);
            wrapper.setBounds(bounds);
        }
    }

    public void resizeFinished(ResizableWrapper wrapper, int previousCursor) { }
}
