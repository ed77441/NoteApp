package com.mynote.common;

import com.mynote.resizable.ResizableWrapper;
import java.awt.Dimension;

public interface Wrappable {
    ResizableWrapper createWrapper(Dimension size);
}
