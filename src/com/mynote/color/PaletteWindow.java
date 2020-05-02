package com.mynote.color;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class PaletteWindow extends JPanel {
    private class SliderChangeListener implements ChangeListener {
        int [] rgb = new int[] {255, 255, 255};

        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            JSlider slider = (JSlider) changeEvent.getSource();
            slider.getParent().repaint();
            Integer i = (Integer) slider.getClientProperty("index");
            rgb[i] = slider.getValue();

            colorValues.setText("(" + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ")");
            colorResult.setBackground(new Color(rgb[0], rgb[1], rgb[2]));
            colorResult.setOpaque(true);
        }
    }

    private class SliderPane extends JPanel {
        SliderPane(int index, String colorName, Color color) {
            super(new BorderLayout());
            setPreferredSize(new Dimension(0, 40));

            add(createTitle(colorName, color), BorderLayout.WEST);
            add(createSlider(index), BorderLayout.CENTER);
        }

        private JLabel createTitle(String colorName, Color color) {
            JLabel label = new JLabel(colorName);
            label.setOpaque(true);
            label.setForeground(color);
            label.setFont(new Font("Fira Code Retina", Font.BOLD, 11));

            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(40,0));
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0,0));
            return label;
        }

        private JSlider createSlider(int index) {
            JSlider slider = new JSlider(0, 255, 255);
            slider.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 10));
            slider.putClientProperty("index", index);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setMajorTickSpacing(51);
            slider.setMinorTickSpacing(17);
            slider.addChangeListener(changeListener);
            slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0 , 5));

            return slider;
        }
    }

    private SliderChangeListener changeListener = new SliderChangeListener();

    JPanel colorResult;
    JLabel colorValues;

    PaletteWindow() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 120));
        JPanel slidePane = createSlidePane();
        JPanel resultPane = createResultPane();

        slidePane.setPreferredSize(new Dimension(300, 120));
        resultPane.setPreferredSize(new Dimension(100, 120));

        add(slidePane, BorderLayout.WEST);
        add(resultPane, BorderLayout.EAST);
    }

    private JPanel createSlidePane () {

        JPanel slidePane = new JPanel();
        slidePane.setLayout(new BoxLayout(slidePane, BoxLayout.Y_AXIS));

        TitledBorder border = BorderFactory.createTitledBorder("Palette");
        border.setTitleFont(new Font("JetBrains mono Medium", Font.PLAIN, 12));
        slidePane.setBorder(border);

        String [] colorNames = new String[] {"RED", "GREEN", "BLUE"};
        Color [] colors = new Color[] {Color.red, Color.green, Color.blue};

        for (int i = 0; i < 3; ++i) {
            SliderPane sliderPane = new SliderPane(i, colorNames[i], colors[i]);
            slidePane.add(sliderPane);
        }

        return slidePane;
    }

    private JPanel createResultPane () {
        JPanel resultPane = new JPanel(new BorderLayout());
        colorValues = new JLabel("(255, 255, 255)");
        colorValues.setPreferredSize(new Dimension(0, 15));
        colorValues.setHorizontalAlignment(SwingConstants.CENTER);
        colorValues.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 11));

        JPanel emptyBorderWrapper = new JPanel(new BorderLayout());
        emptyBorderWrapper.setBorder(BorderFactory.createLineBorder(new Color(238, 238, 238), 15));

        colorResult = new JPanel();
        colorResult.setBackground(Color.white);
        colorResult.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        emptyBorderWrapper.add(colorResult);

        resultPane.add(colorValues, BorderLayout.NORTH);
        resultPane.add(emptyBorderWrapper, BorderLayout.CENTER);

        TitledBorder border = BorderFactory.createTitledBorder("Result");
        border.setTitleFont(new Font("JetBrains mono Medium", Font.PLAIN, 12));
        resultPane.setBorder(border);

        return resultPane;
    }
}