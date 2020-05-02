package com.mynote.content;

import com.mynote.tool.AbstractSetterCollection;
import com.mynote.common.HighQualityIconButton;
import com.mynote.common.NoteUtil;
import com.mynote.canvas.CanvasPane;
import com.mynote.image.ImagePane;
import com.mynote.table.TableGridPopup;
import com.mynote.popup.PopupBinder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static com.mynote.main.NoteApp.mainWindow;

public class InsertButtonCollection extends AbstractSetterCollection {
    static class InsertImageListener implements ActionListener {
        private Dimension resizeToFit(Dimension original) {
            if (original.width > 500) {
                original.height = (int)(500.0 / original.width * original.height);
                original.width = 500;
            }

            if (original.height > 500) {
                original.width = (int)(500.0 / original.height *  original.width);
                original.height = 500;
            }

            return original;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ContentPane scrollPane = mainWindow.getActiveContentPane();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "JPG & GIF Images", "jpg", "png", "gif");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(mainWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String fullPath = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    BufferedImage image = ImageIO.read(new File(fullPath));
                    Dimension resizedSize = resizeToFit(new Dimension(image.getWidth(), image.getHeight()));

                    int dotPosition = fullPath.lastIndexOf('.');
                    ImagePane imagePane = new ImagePane(image, fullPath.substring(dotPosition + 1));
                    scrollPane.addComponent(imagePane.createWrapper(resizedSize), null);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    static class InsertCanvasListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ContentPane scrollPane = mainWindow.getActiveContentPane();
            CanvasPane canvasPane = new CanvasPane();
            scrollPane.addComponent(canvasPane.createWrapper(new Dimension(200, 200)), null);
        }
    }

    public final static InsertButtonCollection collection = new InsertButtonCollection();

    private JButton imageButton = new HighQualityIconButton("IMAGE");
    private JButton tableButton = new HighQualityIconButton("TABLE");
    private JButton canvasButton = new HighQualityIconButton("CANVAS");

    private InsertButtonCollection() {
        addAll(Arrays.asList(imageButton, tableButton, canvasButton));

        imageButton.addActionListener(new InsertImageListener());
        PopupBinder.bind(tableButton, new TableGridPopup(), tableButton);
        canvasButton.addActionListener(new InsertCanvasListener());

        imageButton.setIcon(NoteUtil.loadImageIcon("/image.png", 15, 15));
        tableButton.setIcon(NoteUtil.loadImageIcon("/table.png", 15, 15));
        canvasButton.setIcon(NoteUtil.loadImageIcon("/canvas.png", 15, 15));

        adjustSetterComponent();
        setEnabled(false);
    }

    @Override
    protected String getSetterTitle() {
        return "Insert Component";
    }
}
