package com.mynote.canvas;

import com.mynote.resizable.ResizablePolicy;
import com.mynote.resizable.ResizableWrapper;

public class CanvasPanePolicy extends ResizablePolicy {
    CanvasPanePolicy() {
        super(200, 100);
    }

    @Override
    public void resizeN(ResizableWrapper wrapper, int dy) {
        super.resizeN(wrapper, dy);
        shiftPolyLines(wrapper, 0, -dy);
    }

    @Override
    public void resizeW(ResizableWrapper wrapper, int dx) {
        super.resizeW(wrapper, dx);
        shiftPolyLines(wrapper, -dx, 0);
    }

    @Override
    public void resizeNW(ResizableWrapper wrapper, int dx, int dy) {
        super.resizeNW(wrapper, dx, dy);
        shiftPolyLines(wrapper, -dx, -dy);
    }

    @Override
    public void resizeNE(ResizableWrapper wrapper, int dx, int dy) {
        super.resizeNE(wrapper, dx, dy);
        shiftPolyLines(wrapper, 0, -dy);
    }

    @Override
    public void resizeSW(ResizableWrapper wrapper, int dx, int dy) {
        super.resizeSW(wrapper, dx, dy);
        shiftPolyLines(wrapper, -dx, 0);
    }

    public void shiftPolyLines(ResizableWrapper wrapper, int dx, int dy) {
        ((CanvasPane)wrapper.comp).shiftPolyLines(dx, dy);
    }
}
