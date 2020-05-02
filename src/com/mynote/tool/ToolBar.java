package com.mynote.tool;

import com.mynote.edit.EditCollection;
import com.mynote.canvas.CanvasSetterCollection;
import com.mynote.content.InsertButtonCollection;
import com.mynote.edit.PasteCollection;
import com.mynote.textattr.TextSetterCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ToolBar extends JScrollPane {
    static class Separator extends JPanel {
        Separator() {
            setPreferredSize(new Dimension(20, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.lightGray);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int center = getWidth() / 2;
            g2.drawLine(center,   2,  center,   getHeight() - 4);
        }
    }

    public ToolBar() {

        setPreferredSize(new Dimension(0, 60));
        setBorder(null);

        JPanel innerPane = new JPanel();
        BoxLayout boxLayout = new BoxLayout(innerPane, BoxLayout.X_AXIS);
        innerPane.setLayout(boxLayout);
        getViewport().setView(innerPane);

        innerPane.add(EditCollection.collection.createSetterPane());
        ArrayList<AbstractSetterCollection> collections = new ArrayList<>(Arrays.asList(
                PasteCollection.collection, TextSetterCollection.collection,
                InsertButtonCollection.collection, CanvasSetterCollection.collection
        ));

        for (AbstractSetterCollection collection : collections) {
            innerPane.add(new Separator());
            innerPane.add(collection.createSetterPane());
        }
    }
}
