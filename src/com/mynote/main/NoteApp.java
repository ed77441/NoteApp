package com.mynote.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.mynote.content.InsertButtonCollection;
import com.mynote.tool.ToolBar;
import com.mynote.common.KeyHandler;
import com.mynote.common.NoteUtil;
import com.mynote.content.ContentPane;
import com.mynote.content.TabContainer;
import com.mynote.file.FileMenu;

import javax.swing.*;
import java.awt.*;

public class NoteApp extends JFrame {

    static {
        FlatLightLaf.install();
        UIManager.put("TabbedPane.tabHeight", 25);
        UIManager.put("TabbedPane.tabInsets", new Insets(0,5,0, 5));
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        NoteUtil.registerCustomFont("/GothamRounded-Medium.otf");
        NoteUtil.registerCustomFont("/Amaranth-Bold.otf");
        NoteUtil.registerCustomFont("/JetBrainsMono-Medium.ttf");
        UIManager.put("Button.margin", new Insets(2,5,2,5));
        UIManager.put("Menu.font", new Font("JetBrains mono Medium", Font.PLAIN, 12));
        UIManager.put("MenuItem.font", new Font("JetBrains mono Medium", Font.PLAIN, 12));
    }

    private TabContainer contentSection;
    public static final NoteApp mainWindow = new NoteApp();
    public static final KeyHandler keyHandler = new KeyHandler();

    private void createGUIAndRun() {
        contentSection = new TabContainer();
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyHandler);

        mainWindow.setTitle("Untitled" + FileMenu.titleSuffix);
        mainWindow.setLayout(new BorderLayout());

        mainWindow.setSize(1280,720);

        mainWindow.add(new ToolBar(), BorderLayout.NORTH);
        mainWindow.add(contentSection, BorderLayout.CENTER);
        mainWindow.setJMenuBar(new FileMenu());
        mainWindow.setIconImage(NoteUtil.loadImage("/logo.png", 20, 20));

        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainWindow::createGUIAndRun);
    }

    public ContentPane getActiveContentPane () {
        return (ContentPane) contentSection.getSelectedComponent();
    }

    public void swapContentSection(TabContainer tabContainer) {
        remove(contentSection);
        contentSection = tabContainer;
        add(contentSection, BorderLayout.CENTER);

        if (contentSection.getTabCount() == 1) {
            InsertButtonCollection.collection.setEnabled(false);
        }

        revalidate();
        repaint();
    }

    public TabContainer getContentSection() {
        return contentSection;
    }
}
