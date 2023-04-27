package controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    public void saveToFile(BufferedImage img) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();
        File out = new File("output\\output_file_" + formatter.format(date) + ".bmp");
        ImageIO.write(img, "bmp", out);
    }

    public BufferedImage openFromFile(String name) {
        try {
            return ImageIO.read(new File(name));
        } catch (IOException ex) {
            System.exit(-1);
            return null;
        }
    }
}
