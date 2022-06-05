package net.maustiptop100.ctrc.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoadingScreen {

    private final JFrame frame;

    public LoadingScreen() throws URISyntaxException, IOException {
        this.frame = new JFrame("CTRC");
        this.frame.setSize(500, 300);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.add(new LoadingScreenPanel());
        this.frame.setIconImage(Resources.getIcon());
        this.frame.setVisible(true);
    }

    public void end() {
        this.frame.dispose();
    }

    private static class LoadingScreenPanel extends JPanel {

        private final JProgressBar progressBar;
        private final Image background;

        public LoadingScreenPanel() throws URISyntaxException, IOException {
            this.setLayout(null);
            this.progressBar = new JProgressBar();
            this.background = ImageIO.read(Resources.getAsset("background.jpg"));
            this.progressBar.setIndeterminate(true);
            this.add(this.progressBar);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(this.background, 0, 0, this.getWidth(), this.getHeight(), null);
            g.setColor(Color.WHITE);
            g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 20));
            g.drawString("Loading resources, please wait...", 10, this.getHeight()-50);
            this.progressBar.setBounds(10, this.getHeight()-30, this.getWidth()-20, 20);
        }

    }

}
