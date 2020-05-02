package com.mynote.content;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.mynote.common.NoteUtil;
import com.mynote.file.FileMenu;
import com.mynote.main.NoteApp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.util.IllegalFormatException;
import java.util.List;
import static com.mynote.main.NoteApp.mainWindow;

public class TabContainer extends JTabbedPane {
    class ResizeTabListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            TabContainer.this.resizeTabs();
        }
    }

    class DragAndAddListener extends MouseAdapter {
        int src;
        @Override
        public void mousePressed(MouseEvent e) {
            src = positionInTab(e);
            SwingUtilities.invokeLater(() -> testAndCreateNewPage(e));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int dest = positionInTab(e);
            rearrangeTabOrder(src, dest);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (src != -1 && src != getTabCount() - 1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        void testAndCreateNewPage(MouseEvent e) {
            int clickedTab = positionInTab(e);
            int lastOne = getTabCount() - 1;

            if (clickedTab != -1 && clickedTab == lastOne) {
                TabComponent tabComponent = new TabComponent(TabContainer.this, "Untitled");
                createNewPage(tabComponent, new ContentPane());
                src = -1;
            }

            if (clickedTab != -1) {
                ((ContentPane)getSelectedComponent()).innerPane.grabFocus();
            }
        }

    }

    static class InsertLabel extends JLabel {
        InsertLabel() {
            NoteUtil.setTransparent(this);
            setPreferredSize(new Dimension(12,20));
            setIcon(NoteUtil.loadImageIcon("/plus.png", 12, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
        }
    }

    static class NoteDropTarget extends DropTarget {
        @Override
        public synchronized void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>)
                        evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                if (droppedFiles.size() > 1) {
                    throw new IllegalArgumentException();
                }

                File file = droppedFiles.get(0);

                String fileName = file.getName();
                int dotPos = fileName.lastIndexOf('.');

                if (!fileName.substring(dotPos + 1).equals("note")) {
                    throw new IllegalArgumentException();
                }

                ((FileMenu)mainWindow.getJMenuBar()).openAction(file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow,
                        "The number of file should be at most one, and which file extension must be 'note'"
                        , "Illegal Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    JLabel plusLabel = new InsertLabel();

    public TabContainer() {
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setBackground(new Color(230,230,230));

        JPanel content = new JPanel(new GridBagLayout());
        JLabel textLabel = new JLabel("Press + to continue...");
        textLabel.setFont(new Font("Droid Sans Mono Dotted", Font.ITALIC,40));
        textLabel.setForeground(Color.lightGray);
        content.add(textLabel, new GridBagConstraints());

        add("", new JScrollPane(content));
        setTabComponentAt(0, plusLabel);

        setFocusable(false);
        addComponentListener(new ResizeTabListener());

        MouseAdapter dragTabListener = new DragAndAddListener();
        addMouseListener(dragTabListener);
        addMouseMotionListener(dragTabListener);

        setDropTarget(new NoteDropTarget());
    }

    TabComponent getTabComp(int pos) {
        return (TabComponent) getTabComponentAt(pos);
    }

    private void resizeTabs() {
        int tabCount = getTabCount() - 1;

        if (tabCount > 0) {
            int tabAreaWidth = getWidth() - 22 - 5;
            int [] preferredTabCompWidths = new int[tabCount];
            int [] preferredTabWidths = new int[tabCount];
            int preferredTabWidthSum = 0;

            for (int i = 0; i < tabCount; ++i) {
                int compWidth = Math.max(Math.min(getTabComp(i).getPreferredWidth(), 120), 40);
                int tabWidth = compWidth + 10;
                preferredTabCompWidths[i] = compWidth;
                preferredTabWidths[i] = tabWidth;
                preferredTabWidthSum += tabWidth;
            }

            int []finalTabWidths = preferredTabCompWidths;

            if (preferredTabWidthSum > tabAreaWidth) { /*total width is not enough, so we need to distribute the width based on percentage*/
                int [] percentageTabCompWidths = new int[tabCount];
                int remainingWidth = tabAreaWidth;

                for (int i = 0; i < tabCount; ++i) {
                    double percentage = (double) preferredTabWidths[i] / preferredTabWidthSum;
                    int result = (int) (percentage * tabAreaWidth);
                    percentageTabCompWidths[i] = result - 10;
                    remainingWidth -= result;
                }

                int index = 0;

                while (remainingWidth-- > 0) {
                    percentageTabCompWidths[index++]++;
                    index %= tabCount;
                }
                finalTabWidths = percentageTabCompWidths;
            }

            for (int i = 0; i < tabCount; ++i) {
                getTabComp(i).setPreferredWidth(finalTabWidths[i]);
            }
        }
    }

    @Override
    public void remove(int index) {
        super.remove(index);
        resizeTabs();

        if (getTabCount() > 1 &&
                getSelectedIndex() == getTabCount() - 1) {
            setSelectedIndex(getSelectedIndex() - 1);
        }

        if (getTabCount() == 1) {
            InsertButtonCollection.collection.setEnabled(false);
        }
    }

    private int positionInTab(MouseEvent e) {
        return getUI().tabForCoordinate(this, e.getX(), e.getY());
    }

    private void rearrangeTabOrder(int src, int dest) {
        int lastOne = getTabCount() - 1;

        if (src != -1 && dest != -1 && src != dest &&
                src != lastOne && dest != lastOne) {

            Component srcTab = getTabComponentAt(src);
            Component srcContent = getComponentAt(src);

            super.remove(src);
            add(srcContent, dest);
            setTabComponentAt(dest, srcTab);
            setSelectedIndex(dest);
        }
    }

    public void createNewPage (TabComponent tabComponent, ContentPane contentPane) {
        EditableLabel label = tabComponent.getTitle();
        label.setBorder(BorderFactory.createEmptyBorder(0,3,0,3));
        label.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                resizeTabs();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                resizeTabs();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {}
        });
        int last = getTabCount() - 1;

        add(contentPane, last);
        setTabComponentAt(last, tabComponent);
        setSelectedIndex(last);
        resizeTabs();

        InsertButtonCollection.collection.setEnabled(true);
    }
}
