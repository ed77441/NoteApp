package com.mynote.canvas;

import com.mynote.tool.AbstractSetterCollection;
import com.mynote.common.HighQualityIconButton;
import com.mynote.common.NoteUtil;
import com.mynote.color.ColorSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class CanvasSetterCollection extends AbstractSetterCollection {
    class SwitchEventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton button = (JButton) actionEvent.getSource();
            currentFocus.setCurrentState(button.getText());
        }
    }

    class ChangeStrokeSizeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CanvasPane canvasPane = currentFocus;
            Integer thickness = (Integer) strokeSizeSelector.getSelectedItem();

            ArrayList<PolyLine> selected = canvasPane.selectedPolyLines;
            if (selected.isEmpty()) {
                canvasPane.thickness = thickness;
            }
            else {
                selected.forEach(line -> line.thickness = thickness);
                canvasPane.repaint();
            }
        }
    }

    class CanvasFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            currentFocus = (CanvasPane) focusEvent.getSource();
            setEnabled(true);
            setCurrentState(currentFocus.color, currentFocus.thickness);
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            currentFocus.resetSelected();
            currentFocus = null;
            setEnabled(false);
            setCurrentState(Color.black, 1);
        }
    }

    public JComboBox<Integer> strokeSizeSelector = new JComboBox<>(IntStream.range(1, 16).boxed().toArray(Integer[]::new));
    public ColorSelector colorSelector = new ColorSelector("Stroke Color") {
        @Override
        public void doSomethingWithColor(Color color) {
            CanvasPane canvasPane = currentFocus;
            ArrayList<PolyLine> selected = canvasPane.selectedPolyLines;
            if (selected.isEmpty()) {
                canvasPane.color = color;
            }
            else {
                selected.forEach(line -> line.color = color);
                canvasPane.repaint();
            }
        }
    };

    final FocusListener canvasFocusListener = new CanvasFocusListener();
    private CanvasPane currentFocus;
    private ActionListener changeStrokeSizeListener = new ChangeStrokeSizeListener();
    public static final CanvasSetterCollection collection = new CanvasSetterCollection();
    private JButton drawButton = new HighQualityIconButton("DRAW");
    private JButton editButton = new HighQualityIconButton("EDIT");
    private JButton eraseButton = new HighQualityIconButton("ERASE");

    private CanvasSetterCollection () {
        addAll(Arrays.asList(drawButton, editButton, eraseButton, strokeSizeSelector, colorSelector));
        drawButton.setIcon(NoteUtil.loadImageIcon("/draw.png", 15, 15));
        editButton.setIcon(NoteUtil.loadImageIcon("/edit.png", 15, 15));
        eraseButton.setIcon(NoteUtil.loadImageIcon("/erase.png", 15, 15));

        ActionListener switchEventListener = new SwitchEventListener();
        drawButton.addActionListener(switchEventListener);
        editButton.addActionListener(switchEventListener);
        eraseButton.addActionListener(switchEventListener);
        strokeSizeSelector.addActionListener(changeStrokeSizeListener);
        strokeSizeSelector.setPreferredSize(new Dimension(50, 15));

        adjustSetterComponent();
        setEnabled(false);
    }

    @Override
    protected String getSetterTitle() {
        return "Drawing Tools";
    }

    public void setCurrentState(Color o, int thickness) {
        setColorState(o);
        setThicknessState(thickness);
    }

    public void setThicknessState(int thickness) {
        strokeSizeSelector.removeActionListener(changeStrokeSizeListener);
        if (thickness != -1) {
            strokeSizeSelector.setSelectedItem(thickness);
        }
        else {
            strokeSizeSelector.setSelectedIndex(-1);
        }
        strokeSizeSelector.addActionListener(changeStrokeSizeListener);
    }

    public void setColorState(Object o) {
        colorSelector.displayPane.setBackgroundColor(o);
    }
}
