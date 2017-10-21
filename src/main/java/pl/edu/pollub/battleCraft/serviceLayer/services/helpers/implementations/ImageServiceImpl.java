package pl.edu.pollub.battleCraft.serviceLayer.services.helpers.implementations;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.ImageService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Component
public class ImageServiceImpl implements ImageService {

    public BufferedImage resizeImageFromFile(File imageFile, Dimension boundary) throws IOException {
        BufferedImage imageToScale = ImageIO.read(imageFile);
        if(imageToScale.getWidth()<boundary.getWidth() && imageToScale.getHeight()<boundary.getHeight()){
            return imageToScale;
        }
        else {
            Dimension dimensionToScale = this.getScaledDimension(
                    new Dimension(imageToScale.getWidth(), imageToScale.getHeight()),
                    boundary
            );
            return this.scaleImage(imageToScale, dimensionToScale);
        }
    }

    @Override
    public byte[] convertBufferedImageToByteArray(BufferedImage bufferedImage, String extension) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( bufferedImage, extension, baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    private BufferedImage scaleImage(BufferedImage img, Dimension dimension) {

        int targetWidth = (int) dimension.getWidth();

        int targetHeight = (int) dimension.getHeight();

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetWidth) {
                w /= 2;
                w = (w < targetWidth) ? targetWidth : w;
            }

            if (h > targetHeight) {
                h /= 2;
                h = (h < targetHeight) ? targetHeight : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);

        g2.dispose();

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;

    }
}
