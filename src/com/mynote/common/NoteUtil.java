package com.mynote.common;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class NoteUtil {
    public static Image makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };


        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static BufferedImage loadImage(String path, int width, int height) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(NoteUtil.class.getResourceAsStream(path));
            bufferedImage = Thumbnails.of(bufferedImage)
                    .size(width, height)
                    .asBufferedImage();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return bufferedImage;
    }

    public static ImageIcon loadImageIcon(String path, int width, int height) {
        return new ImageIcon(makeColorTransparent(loadImage(path, width, height), Color.white));
    }

    public static void setTransparent(JComponent component) {
        component.setOpaque(false);
        component.setBackground(new Color(0, true));
    }

    public static Cursor createCustomCursor(String path, Dimension preferredSize) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int preferredWidth = preferredSize.width, preferredHeight = preferredSize.height;
        Dimension bestSize = toolkit.getBestCursorSize(preferredWidth, preferredHeight);
        BufferedImage image = new BufferedImage(bestSize.width, bestSize.height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage cursorImage = loadImage(path, preferredWidth, preferredHeight);

        int cursorX = bestSize.width - preferredWidth, cursorY = bestSize.height - preferredHeight;
        Graphics g = image.getGraphics();
        g.drawImage(cursorImage, cursorX, cursorY, null);

        return toolkit.createCustomCursor( makeColorTransparent(image, Color.white)
                , new Point(cursorX,bestSize.height - 1)
                , path.substring(0, path.lastIndexOf('.') + 1));
    }

    public static void registerCustomFont (String path) {
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, NoteUtil.class.getResourceAsStream(path)));
        } catch (IOException |FontFormatException e) {
            e.printStackTrace();
        }
    }
}
