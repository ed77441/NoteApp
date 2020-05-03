package com.mynote.file;
import com.mynote.common.NoteUtil;
import com.mynote.content.TabContainer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import static com.mynote.main.NoteApp.*;


public class FileMenu extends JMenuBar {
    void showWarningMessage() {
        JOptionPane.showMessageDialog(mainWindow,
                "File name may contains illegal character or extension."
                        , "Warning", JOptionPane.WARNING_MESSAGE);
    }

    boolean dialogIsOpen = false;

    private void openSaveFileDialog () {
        if (!dialogIsOpen) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle("Save Note File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Note Document", "note"));

            dialogIsOpen = true;
            int userSelection = fileChooser.showSaveDialog(null);
            dialogIsOpen = false;

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String fullPath = fileToSave.getAbsolutePath();
                int lastSlash = fullPath.lastIndexOf(File.separatorChar);

                String fileName = fullPath.substring(lastSlash + 1);

                if (!fileName.chars()
                        .allMatch(ch -> Character.isDigit(ch) || Character.isLetter(ch) || ch == '.')) {
                    showWarningMessage();
                } else {
                    int pos = fileName.lastIndexOf('.');

                    if (pos != -1) {
                        String extension = fileName.substring(pos + 1);

                        if (!extension.equals("note")) {
                            showWarningMessage();
                        } else {
                            currentFilePointer = fullPath;
                            SaveFileManager.writeObjectToDisk(fullPath);
                            mainWindow.setTitle(fileName + titleSuffix);
                        }
                    } else {
                        fullPath += ".note";
                        currentFilePointer = fullPath;
                        SaveFileManager.writeObjectToDisk(fullPath);
                        mainWindow.setTitle(fileName + ".note");
                    }
                }
            }
        }
    }

    private void openExistingFileAction () {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Open Note File");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Note Document", "note");
        fileChooser.setFileFilter(filter);
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            openAction(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public void openAction(String path) {
        mainWindow.swapContentSection(SaveFileManager.getSavedComponents(path));
        String fileName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        currentFilePointer = path;
        mainWindow.setTitle(fileName + titleSuffix);
    }

    void saveAction() {
        if (currentFilePointer == null) {
            openSaveFileDialog();
        }
        else {
            SaveFileManager.writeObjectToDisk(currentFilePointer);
        }
    }

    private void newNoteAction() {
        mainWindow.swapContentSection(new TabContainer());
        mainWindow.setTitle("Untitled" + titleSuffix);
        currentFilePointer = null;
    }

    static class AboutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JLabel infoLabel = new JLabel (
                          "<html>" +
                          "1. Click on the border of the component to delete, copy and cut it.<br/>" +
                          "2. Click on the content section to paste your component.<br/>" +
                          "3. Right click on the content section to add a new TextPane.<br/>" +
                          "4. Click on TextPane or TablePane to set some text attributes.<br/>" +
                          "5. Multi-select TablePane to change selected cell text attribute.<br/>" +
                          "6. Click on CanvasPane to draw, move, edit and erase strokes on it.<br/>" +
                          "7. Double-click on content's title to do some edit.<br/>" +
                          "8. Drag a tab to another tab will cause the relocation of the tab.<br/>" +
                          "9. Press on the border of the component to relocate and resize it.<br/>" +
                          "10.While a component is doing above action, if cursor is out of window<br/>" +
                          "it will cause content section to expand itself.<br/>" +
                          "</html>"
            );
            infoLabel.setFont(new Font("Gotham Rounded Medium" , Font.PLAIN, 14));
            JOptionPane.showMessageDialog(mainWindow, infoLabel, "How to use this application?", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static String currentFilePointer = null;
    public static final String titleSuffix = " - Note Application";

    public FileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newNoteOption = new JMenuItem("New");
        newNoteOption.setToolTipText("Ctrl + N");
        newNoteOption.addActionListener((ActionEvent) -> newNoteAction());
        fileMenu.add(newNoteOption);
        keyHandler.bind("Ctrl N", this::newNoteAction);

        JMenuItem openOption = new JMenuItem("Open");
        openOption.setToolTipText("Ctrl + O");
        openOption.addActionListener((ActionEvent) -> openExistingFileAction());
        fileMenu.add(openOption);
        keyHandler.bind("Ctrl O", this::openExistingFileAction);

        JMenuItem saveOption = new JMenuItem("Save");
        saveOption.setToolTipText("Ctrl + S");
        saveOption.addActionListener((ActionEvent) -> saveAction());
        fileMenu.add(saveOption);
        keyHandler.bind("Ctrl S", this::saveAction);

        JMenuItem saveAsOption = new JMenuItem("Save As");
        saveAsOption.setToolTipText("Alt + S");
        saveAsOption.addActionListener((ActionEvent actionEvent) -> openSaveFileDialog());
        keyHandler.bind("Alt S", this::openSaveFileDialog);

        fileMenu.add(saveAsOption);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new AboutListener());
        helpMenu.add(aboutItem);

        add(fileMenu);
        add(helpMenu);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getX() + getWidth();
        int topY = getY();
        int bottomY = getY() + getHeight();

        Point2D.Float t = new Point2D.Float(centerX, topY);
        Point2D.Float b = new Point2D.Float(centerX, bottomY);
        float [] dist = new float[] {0.0f, 0.5f, 1.0f};
        Color ww = new Color(245, 245, 245);
        Color [] colors = new Color[] {ww, Color.white, ww};
        LinearGradientPaint paint = new LinearGradientPaint(t, b, dist, colors);
        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(paint);
        g2.fillRect(0,0, getWidth(), getHeight());
    }
}
