package com.mynote.image;

import com.mynote.file.ComponentInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

public class ImagePaneInfo implements Serializable, ComponentInfo {

    private static final long serialVersionUID = 6529685098267757691L;

    byte [] imageArray;
    String extension;

    @Override
    public Component restoreFromObjectInfo() {
        ImagePane imagePane = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageArray)){
            BufferedImage image = ImageIO.read(bais);
            imagePane = new ImagePane(image, extension);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return imagePane;
    }
}
