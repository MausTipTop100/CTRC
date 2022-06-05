package net.maustiptop100.ctrc.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Resources {

    public static Image getIcon() throws URISyntaxException, IOException {
        File file = new File(new File(Resources.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile().getParent(), "assets/icon.png");
        return ImageIO.read(file);
    }

    public static File getAsset(String name) throws URISyntaxException {
        return new File(new File(Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent(), "assets/" + name);
    }
}
