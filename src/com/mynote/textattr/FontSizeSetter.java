package com.mynote.textattr;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.stream.IntStream;

public class FontSizeSetter extends ComboBoxSetter<Integer> {
    FontSizeSetter() {
        super(getFontSizeList(), StyleConstants.FontSize);
        setMinimumSize(new Dimension(50, 15));
        setPreferredSize(new Dimension(50, 15));
    }

    private static Integer[] getFontSizeList() {
        return IntStream.range(10, 65).boxed().toArray(Integer[]::new);
    }
}