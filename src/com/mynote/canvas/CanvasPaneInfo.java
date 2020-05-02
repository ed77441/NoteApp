package com.mynote.canvas;

import com.mynote.file.ComponentInfo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class CanvasPaneInfo implements Serializable, ComponentInfo {

    private static final long serialVersionUID = 6529685098267757692L;

    Color color;
    int thickness;
    ArrayList <PolyLine> polyLines;

    @Override
    public Component restoreFromObjectInfo() {
        return new CanvasPane(color, thickness, polyLines);
    }
}
