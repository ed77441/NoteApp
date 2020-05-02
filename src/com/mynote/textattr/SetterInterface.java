package com.mynote.textattr;

import java.awt.event.ActionListener;

public interface SetterInterface {
    Object invalidState = new Object();
    void addAction(ActionListener a);
    void removeAction(ActionListener a);
    void setGivenValue(Object o);
    Object getKey ();
    Object getVal ();
}
