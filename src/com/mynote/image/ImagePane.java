package com.mynote.image;

import com.mynote.file.ComponentInfo;
import com.mynote.file.InfoExtractable;
import com.mynote.common.Wrappable;
import com.mynote.resizable.ResizableWrapper;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImagePane extends JPanel implements Wrappable, InfoExtractable {
    static class PassEventToParentListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            passToParent(e.getComponent(), e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            passToParent(e.getComponent(), e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            passToParent(e.getComponent(), e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            passToParent(e.getComponent(), e);
        }

        void passToParent(Component child, MouseEvent mouseEvent) {
            Component parent = child.getParent();
            mouseEvent = SwingUtilities.convertMouseEvent(child, mouseEvent, parent);
            parent.dispatchEvent(mouseEvent);
        }
    }
    BufferedImage image, resized;
    Dimension currentSize;
    String extension;

    public ImagePane(BufferedImage image, String extension) {
        this.extension = extension;
        this.image = image;
        resized = image;
        currentSize = getSize();
        PassEventToParentListener passEventToParentListener = new PassEventToParentListener();
        setLayout(new BorderLayout());
        addMouseListener(passEventToParentListener);
        addMouseMotionListener(passEventToParentListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);

        if (shouldResizeImage()) {
            resizeImageToFitTheWindow();
        }

        g2.drawImage(resized, 0, 0, getWidth(), getHeight(),this);
    }

    boolean shouldResizeImage() {
        return Math.abs(currentSize.width - getWidth()) > 75 || Math.abs(currentSize.height - getHeight()) > 75;
    }

    void resizeImageToFitTheWindow() {
        try {
            resized = Thumbnails.of(image).size(getWidth(), getHeight()).asBufferedImage();
            currentSize = getSize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ResizableWrapper createWrapper(Dimension size) {
        ResizableWrapper wrapper = new ResizableWrapper(this,
                new ImagePaneBorder(), new ImagePanePolicy(size.width, size.height));
        wrapper.setSize(size);
        return wrapper;
    }

    @Override
    public ComponentInfo convertToInfoObject() {
        ImagePaneInfo componentInfo = new ImagePaneInfo();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, extension, baos);
            componentInfo.extension = extension;
            componentInfo.imageArray = baos.toByteArray();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return componentInfo;
    }
}
