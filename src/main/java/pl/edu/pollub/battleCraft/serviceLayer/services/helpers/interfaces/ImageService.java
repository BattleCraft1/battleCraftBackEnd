package pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageService {
    BufferedImage resizeImageFromFile(File imageFile, Dimension boundary) throws IOException;
    byte[] convertBufferedImageToByteArray(BufferedImage bufferedImage,String extension) throws IOException;
}
