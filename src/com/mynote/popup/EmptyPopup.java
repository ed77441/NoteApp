package com.mynote.popup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;


public class EmptyPopup extends JPopupMenu {

    class PreventReopenListener implements PopupMenuListener {
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) { }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PopupBinder.BooleanWrapper isPressed =
                            (PopupBinder.BooleanWrapper)getClientProperty("buttonFlag");
                    isShowing = isPressed.flag; /*Override*/
                }
            });
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e){
            isShowing = true;
        }
    }

    static class BottomBorder implements Border {
        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(1, 1, 1,  1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component component, Graphics g, int x, int y,
                                int w, int h) {
            g.setColor(Color.darkGray);
            int BottomY = y + h - 1;
            g.drawLine(x + 10, BottomY, x + w - 11, BottomY);
        }
    }

    public boolean isShowing = false;
    private JLabel title ;

    public EmptyPopup(String titleText) {
        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        title = new JLabel(titleText) ;
        title.setFont(new Font("Ink Free", Font.BOLD, 18));
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        title.setBorder(new BottomBorder());

        addPopupMenuListener(new PreventReopenListener());

        add(title);

        setPopUpWindowSize(150, 200);
    }

    public void setPopUpWindowSize(int width, int height) {
        title.setMaximumSize(new Dimension(width, 30));
        title.setPreferredSize(new Dimension(width, 30));
        setPopupSize(width, height);
    }
}
