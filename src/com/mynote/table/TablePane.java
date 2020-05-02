package com.mynote.table;

import com.mynote.common.NoteUtil;
import com.mynote.file.ComponentInfo;
import com.mynote.file.InfoExtractable;
import com.mynote.common.Wrappable;
import com.mynote.resizable.ResizableWrapper;
import com.mynote.textattr.RangeAttributeSet;
import com.mynote.textattr.Settable;
import com.mynote.textattr.TextSetterCollection;


import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TablePane extends JPanel implements Settable, InfoExtractable, Wrappable {
    class SelectionLoseListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            grids.forEach(TableGrid::setSelectedBackground);
        }

        @Override
        public void focusLost(FocusEvent e) {
            grids.forEach(TableGrid::resetBackground);
        }
    }

    public final ArrayList<TableGrid> grids = new ArrayList<>();
    public final int row, col;
    public final int minWidth = 50;
    ResizableWrapper wrapper;

    public TablePane(int row, int col) {
        this.row = row; this.col = col;
        initTablePane();

        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                TableGrid grid = new TableGrid(this, i, j);
                grids.add(grid); add(grid);
            }
        }

        ArrayList<Integer> initWidths = new ArrayList<>();
        for (int i = 0; i < col; ++i) {
            initWidths.add(minWidth);
        }
        adjustGridBounds(initWidths);

        revalidate();
    }

    void initTablePane() {
        setLayout(null);
        addFocusListener(new SelectionLoseListener());
        addFocusListener(TextSetterCollection.collection.settableFocusListener);
        NoteUtil.setTransparent(this);
    }

    TablePane(int row, int col, ArrayList<String> texts, ArrayList<MutableAttributeSet> currentAttrs,
                     ArrayList<ArrayList<RangeAttributeSet>> arrayOfSets) {
        this.row = row; this.col = col;
        initTablePane();
        Iterator<String> textIterator = texts.iterator();
        Iterator<MutableAttributeSet> currentAttrIterator = currentAttrs.iterator();
        Iterator<ArrayList<RangeAttributeSet>> arrayOfSetsIterator = arrayOfSets.iterator();

        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                TableGrid grid = new TableGrid(this, i, j, textIterator.next()
                        , currentAttrIterator.next(), arrayOfSetsIterator.next());
                grids.add(grid); add(grid);
            }
        }

        revalidate();
    }


    ArrayList<Integer> getIntArrayList(IntStream intStream) {
        return intStream.boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<TableGrid> getCol(int n) {
        return grids.stream()
                .filter(grid -> grids.indexOf(grid) % col == n)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<TableGrid> getRow(int n) {
        int startPos = col * n;
        return new ArrayList<>(grids.subList(startPos, startPos + col));
    }

    public void adjustGridBounds(ArrayList<Integer> colWidths) {
        int y = 0;
        for (int i = 0; i < row ; ++i) {
            ArrayList<TableGrid> row = getRow(i);
            int highestHeight = row.stream()
                    .mapToInt(grid -> grid.getPreferredSize().height)
                    .max().orElse(0);

            int height = Math.max(18, highestHeight);

            int x = 0;
            for (int j = 0; j < col; ++j) {
                int width = colWidths.get(j);
                row.get(j).setBounds(x, y, width, height);
                x += width;
            }
            y += height;
        }
    }

    public ArrayList<Integer> getNewWidths() {
        int totalWidth = getWidth() - col * minWidth - 1;
        ArrayList<Integer> colMaxWidths = new ArrayList<>();
        ArrayList<Integer> newWidths = new ArrayList<>();

        for (int i = 0; i < col; ++i) {
            ArrayList<TableGrid> col = getCol(i);
            int widestWidth = col.stream()
                    .mapToInt(g -> g.getPreferredSize().width)
                    .max().orElse(0);
            colMaxWidths.add(Math.max(widestWidth - minWidth, 0));
        }

        int sum = colMaxWidths.stream()
                .mapToInt(Integer::intValue).sum();

        IntStream intStream = IntStream.range(0, col);
        if (sum == 0) {
            intStream = intStream.map(i -> (int)(1.0 / col * totalWidth));
        }
        else {
            intStream = intStream.map(i -> (int)((double)colMaxWidths.get(i) / sum * totalWidth));
        }

        ArrayList<Integer> percentageWidths = getIntArrayList(intStream);

        int remaining = totalWidth - percentageWidths.stream()
                .mapToInt(Integer::intValue).sum();

        for (int i = 0; i < col; ++i) {
            newWidths.add(percentageWidths.get(i) + minWidth);
        }

        int counter = 0;
        while (remaining-- > 0) {
            int index = counter++ % col;
            newWidths.set(index, newWidths.get(index) + 1);
        }

        return newWidths;
    }

    public void resizeParentHeight () {
        int parentHeight = getCol(0).stream()
                .mapToInt(Component::getHeight).sum();
        wrapper.setSize(wrapper.getWidth(), parentHeight + 16);
    }

    ArrayList<MutableAttributeSet> geAttributeFromGrid(TableGrid grid) {
        ArrayList<MutableAttributeSet> result =
                new ArrayList<>(grid.getArrayOfAttributeSet(0, grid.getText().length()));
        if (result.isEmpty()) {
            result.add(grid.currentAttr);
        }
        return result;
    }

    MutableAttributeSet getMultiSelectedState() {
        ArrayList<MutableAttributeSet> multiSelected = new ArrayList<>();
        grids.stream()
                .filter(grid -> grid.selected)
                .forEach(grid -> multiSelected.addAll(geAttributeFromGrid(grid)));
        return Settable.getSelectedTextAttributeSet(multiSelected);
    }

    @Override
    public void disableComponent() {}

    @Override
    public void enableComponent() {}

    @Override
    public MutableAttributeSet getFocusState() {
        return getMultiSelectedState();
    }

    @Override
    public void setTextAttribute(Object o, Object o1) {
        grids.stream()
                .filter(grid -> grid.selected)
                .forEach(grid -> setGridTextAttribute(grid, o, o1));
    }

    private void setGridTextAttribute (TableGrid grid, Object o, Object o1) {
        grid.caretDisabled = true;
        grid.selectAll();
        grid.setTextAttribute(o, o1);
        SwingUtilities.invokeLater(() -> grid.caretDisabled = false);
    }

    public ResizableWrapper createWrapper(Dimension size) {
        int minParentWidth = 16 + col * minWidth;
        ResizableWrapper wrapper = new ResizableWrapper(this,  new TablePaneBorder()
                , new TablePanePolicy(minParentWidth, 0));
        this.wrapper = wrapper;
        wrapper.setSize(size);
        setSize(size.width - 16, size.height - 16);

        adjustGridBounds(getNewWidths());
        adjustGridBounds(getNewWidths());
        resizeParentHeight();

        return wrapper;
    }

    @Override
    public ComponentInfo convertToInfoObject() {
        TablePaneInfo componentInfo = new TablePaneInfo();

        componentInfo.row = row;
        componentInfo.col = col;
        componentInfo.texts = grids.stream()
                .map(JTextPane::getText)
                .collect(Collectors.toCollection(ArrayList::new));

        componentInfo.currentAttrs = grids.stream()
                .map(grid -> grid.currentAttr)
                .collect(Collectors.toCollection(ArrayList::new));

        componentInfo.arrayOfSets = grids.stream()
                .map(grid -> grid.getArrayOfAttributeSet(0, grid.getText().length()))
                .collect(Collectors.toCollection(ArrayList::new));

        return componentInfo;
    }
}

