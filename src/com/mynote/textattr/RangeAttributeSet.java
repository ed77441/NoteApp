package com.mynote.textattr;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

public class RangeAttributeSet extends SimpleAttributeSet {
    public int start, length;

    RangeAttributeSet(MutableAttributeSet set, int start, int length) {
        super(set);
        this.start = start;
        this.length = length;
    }
}
