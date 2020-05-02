package com.mynote.image;

import com.mynote.resizable.ResizablePolicy;
import com.mynote.resizable.ResizableWrapper;

public class ImagePanePolicy extends ResizablePolicy {

    int imageWidth, imageHeight;

    public ImagePanePolicy(int imgWidth, int imgHeight) {
        super((int)(imgWidth * 0.2), (int)(imgHeight * 0.2));
        imageWidth = imgWidth;
        imageHeight = imgHeight;
    }

    @Override
    public void resizeSE(ResizableWrapper wrapper, int dx, int dy) {
        int width = nextPos.x + 5;
        int height = nextPos.y + 5;
        int resultWidth, resultHeight;

        if (width > multiplyRatio(height)) {
            resultWidth = width;
            resultHeight = divideByRatio(width);
        }
        else {
            resultWidth = multiplyRatio(height);
            resultHeight = height;
        }

        if (isLegal(resultWidth, resultHeight)) {
            wrapper.setSize(resultWidth, resultHeight);
        }
    }

    @Override
    public void resizeNE(ResizableWrapper wrapper, int dx, int dy) {

        int width = nextPos.x + 5;
        int resultWidth, resultHeight;

        resultWidth = width;
        resultHeight = divideByRatio(width);

        int offsetHeight = wrapper.getHeight() - dy;
        int offsetWidth = multiplyRatio(offsetHeight);

        if (offsetWidth > resultWidth) {
            resultWidth = offsetWidth;
            resultHeight = offsetHeight;
        }

        if (isLegal(resultWidth, resultHeight)) {
            int resultY = wrapper.getY() - (resultHeight - wrapper.getHeight());
            wrapper.setBounds(wrapper.getX(), resultY, resultWidth, resultHeight);

        }
    }

    @Override
    public void resizeSW(ResizableWrapper wrapper, int dx, int dy) {
        int height = nextPos.y + 5;
        int resultWidth, resultHeight;

        resultWidth = multiplyRatio(height);
        resultHeight = height;

        int offsetWidth = wrapper.getWidth() - dx;
        int offsetHeight = divideByRatio(offsetWidth);

        if (offsetHeight > resultHeight) {
            resultWidth = offsetWidth;
            resultHeight = offsetHeight;
        }

        if (isLegal(resultWidth, resultHeight)) {
            int resultX = wrapper.getX() - (resultWidth - wrapper.getWidth());
            wrapper.setBounds(resultX, wrapper.getY(), resultWidth, resultHeight);
        }
    }

    @Override
    public void resizeNW(ResizableWrapper wrapper, int dx, int dy) {
        int resultWidth, resultHeight;

        int wOffsetWidth = wrapper.getWidth() - dx;
        int wOffsetHeight = divideByRatio(wOffsetWidth);

        int nOffsetHeight = wrapper.getHeight() - dy;
        int nOffsetWidth = multiplyRatio(nOffsetHeight);

        if (wOffsetWidth < nOffsetWidth) {
            resultWidth = nOffsetWidth;
            resultHeight = nOffsetHeight;
        }
        else {
            resultWidth = wOffsetWidth;
            resultHeight = wOffsetHeight;
        }

        if (isLegal(resultWidth, resultHeight)) {
            int resultX = wrapper.getX() - (resultWidth - wrapper.getWidth());
            int resultY = wrapper.getY() - (resultHeight - wrapper.getHeight());
            wrapper.setBounds(resultX, resultY, resultWidth, resultHeight);
        }
    }

    @Override
    public void resizeN(ResizableWrapper wrapper, int dy) {
        super.resizeN(wrapper, dy);
        updateImageWidthAndHeight(wrapper);
    }

    @Override
    public void resizeS(ResizableWrapper wrapper, int dy) {
        super.resizeS(wrapper, dy);
        updateImageWidthAndHeight(wrapper);
    }

    @Override
    public void resizeW(ResizableWrapper wrapper, int dx) {
        super.resizeW(wrapper, dx);
        updateImageWidthAndHeight(wrapper);
    }

    @Override
    public void resizeE(ResizableWrapper wrapper, int dx) {
        super.resizeE(wrapper, dx);
        updateImageWidthAndHeight(wrapper);
    }

    private void updateImageWidthAndHeight(ResizableWrapper wrapper) {
        imageWidth = wrapper.getWidth();
        imageHeight = wrapper.getHeight();
    }


    private boolean isLegal(int w, int h) {
        return w >= minWidth && h >= minHeight;
    }

    private double getRatio() {
        return (double) imageWidth / imageHeight;
    }

    private int multiplyRatio(int val) {
        return (int)(val * getRatio());
    }

    private int divideByRatio(int val) {
        return (int)(val / getRatio());
    }


    @Override
    public void resizeFinished(ResizableWrapper wrapper, int previousCursor) {
        ImagePane imagePane = (ImagePane) wrapper.comp;
        imagePane.resizeImageToFitTheWindow();
        imagePane.repaint();
    }
}
