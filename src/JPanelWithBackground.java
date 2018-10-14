import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Michael Haines on 10/12/2018.
 */
public class JPanelWithBackground extends JPanel {

    private Image backgroundImage;

    // Some code to initialize the background image.
    // Here, we use the constructor to load the image. This
    // can vary depending on the use case of the panel.
    public JPanelWithBackground(String fileName) throws IOException {
        backgroundImage = ImageIO.read(new File(fileName));
        /*Dimension size = new Dimension(backgroundImage.getWidth(null), backgroundImage.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);*/
    }

    public Dimension getPreferredSize() {
        Dimension superSize = super.getPreferredSize();
        if (isPreferredSizeSet() || backgroundImage == null) {
            return superSize;
        }
        int prefW = Math.max(backgroundImage.getWidth(null), superSize.width);
        int prefH = Math.max(backgroundImage.getHeight(null), superSize.height);
        return new Dimension(prefW, prefH);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage!= null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }
}