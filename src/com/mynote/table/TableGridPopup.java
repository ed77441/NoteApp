package com.mynote.table;

import com.mynote.common.NoteUtil;
import com.mynote.content.ContentPane;
import com.mynote.popup.EmptyPopup;
import com.mynote.main.NoteApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TableGridPopup extends EmptyPopup {

     class GridPane extends JPanel {
        class HoverListener extends MouseAdapter {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = (JButton) e.getComponent();
                int row = (int)hovered.getClientProperty("row");
                int col = (int)hovered.getClientProperty("col");

                sizeIndicator.setText(row + " x " + col);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = null;
                sizeIndicator.setText(" ");
                repaint();
            }
        }

        class CreateTableListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton button = (JButton) actionEvent.getSource();
                int row = (int) button.getClientProperty("row");
                int col = (int) button.getClientProperty("col");

                ContentPane scrollPane = NoteApp.mainWindow.getActiveContentPane();
                TablePane tablePane = new TablePane(row, col);
                int parentMinWidth = 16 + col * tablePane.minWidth;
                scrollPane.addComponent(
                        tablePane.createWrapper(new Dimension(parentMinWidth , 0)), null);

                TableGridPopup.this.setVisible(false);
                hovered = null;
                repaint();
            }
        }

        JButton [][]grids;
        JButton hovered = null;

        GridPane(int row, int col) {
            super(new GridLayout(row, col, 0, 0));
            grids = new JButton[row][col];
            MouseListener hoverListener = new HoverListener();
            ActionListener createTableListener = new CreateTableListener();

            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    JButton grid = grids[i][j] = new JButton();
                    grid.setPreferredSize(new Dimension(15,15));
                    grid.addMouseListener(hoverListener);
                    grid.addActionListener(createTableListener);
                    grid.putClientProperty("row", i + 1);
                    grid.putClientProperty("col", j + 1);
                    add(grid);
                }
            }

            NoteUtil.setTransparent(this);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8 , 8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hovered != null) {
                Point upperLeft = grids[0][0].getLocation();
                Point rightBottom = hovered.getLocation();
                rightBottom.translate(hovered.getWidth(), hovered.getHeight());
                Rectangle rect = new Rectangle(upperLeft);
                rect.add(rightBottom);
                g.setColor(new Color(222, 222, 222));
                g.fillRoundRect(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 5, 5);
            }
        }
    }

    JLabel sizeIndicator = new JLabel(" ");

    public TableGridPopup() {
        super("Table Grid");
        setPopUpWindowSize(180, 160);
        add(new GridPane(5, 8));
        sizeIndicator.setAlignmentX(CENTER_ALIGNMENT);
        sizeIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        sizeIndicator.setVerticalAlignment(SwingConstants.CENTER);
        sizeIndicator.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        sizeIndicator.setBackground(Color.red);
        add(sizeIndicator);
    }
}
