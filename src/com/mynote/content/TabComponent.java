package com.mynote.content;

import com.mynote.common.NoteUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabComponent extends JPanel {
    class CloseButton extends JButton {

        class CloseListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to delete this tab?","Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    tabbedPane.remove(tabbedPane.indexOfTabComponent(TabComponent.this));
                }
            }
        }
        class IconChangeListener extends MouseAdapter {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(hovered);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(unpressed);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        CloseButton() {
            super(unpressed);
            addActionListener(new CloseListener());
            addMouseListener(new IconChangeListener());
            setBorder(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g2);
        }
    }

    class PassEventToParentListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            passToParent(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            passToParent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            passToParent(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            passToParent(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            passToParent(e);
        }

        void passToParent(MouseEvent e) {
            EditableLabel label = (EditableLabel) e.getSource();

            if (!label.active) {
                JTabbedPane parent = tabbedPane;
                e = SwingUtilities.convertMouseEvent(label, e, parent);
                parent.dispatchEvent(e);
            }
        }
    }

    JTabbedPane tabbedPane;
    static final ImageIcon unpressed = NoteUtil.loadImageIcon("/close.png", 10, 10);
    static final ImageIcon hovered = NoteUtil.loadImageIcon("/hover.png", 10, 10);

    public TabComponent(JTabbedPane tabbedPane, String title) {
        super(new BorderLayout());
        this.tabbedPane = tabbedPane;

        EditableLabel label = new EditableLabel(title);
        PassEventToParentListener toParentListener = new PassEventToParentListener();
        label.addMouseListener(toParentListener);
        label.addMouseMotionListener(toParentListener);
        label.setFont(new Font("Dialog", Font.BOLD ,12));
        add(label, BorderLayout.WEST);

        JPanel buttonWrapper = new JPanel(new GridBagLayout());
        JButton closeButton = new CloseButton();
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(10, 10));
        buttonWrapper.add(closeButton);
        add(buttonWrapper, BorderLayout.EAST);

        NoteUtil.setTransparent(label);
        NoteUtil.setTransparent(closeButton);
        NoteUtil.setTransparent(buttonWrapper);
        NoteUtil.setTransparent(this);
    }

    public EditableLabel getTitle() {
        return (EditableLabel) getComponent(0);
    }

    public int getPreferredWidth() {
        EditableLabel title = getTitle();
        Dimension currentPreferred = title.getPreferredSize();
        title.setPreferredSize(null);
        int actualPreferredWidth = title.getPreferredSize().width + 15;
        title.setPreferredSize(currentPreferred);
        return actualPreferredWidth;
    }

    public void setPreferredWidth(int preferredWidth) {
        setPreferredSize(new Dimension(preferredWidth, 20));
        getTitle().setPreferredSize(new Dimension(preferredWidth - 15, 20));
    }
}