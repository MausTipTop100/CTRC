package net.maustiptop100.ctrc.commands;

import com.google.common.io.ByteStreams;
import net.maustiptop100.ctrc.util.Editor;
import net.maustiptop100.ctrc.util.LoadingScreen;
import net.maustiptop100.ctrc.util.Resources;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

public class OPEN implements CommandExecutor {
    private JFrame frame;
    @Override
    public void runCommand(String[] args) {
        this.frame = new JFrame();
        this.frame.setSize(600, 200);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel jsonLabel = new JLabel("Project File Path");
        panel.add(jsonLabel);
        jsonLabel.setBounds(20, 10, 530, 20);
        JTextField jsonFile = new JTextField();
        jsonFile.setBounds(20, 30, 500, 20);
        panel.add(jsonFile);
        JButton jsonFileChooserBtn = new JButton("...");
        jsonFileChooserBtn.setBounds(530, 30, 30, 20);
        panel.add(jsonFileChooserBtn);
        jsonFileChooserBtn.addActionListener(action -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("CTRC project files", "ctrc.json", "ctrc", "json"));
            chooser.setAcceptAllFileFilterUsed(false);
            int status = chooser.showOpenDialog(this.frame);
            if(status == JFileChooser.APPROVE_OPTION) {
                jsonFile.setText(chooser.getSelectedFile().getPath());
            }
        });

        JButton continueBtn = new JButton("Continue");
        continueBtn.setBounds(450, 110, 100, 30);
        panel.add(continueBtn);
        this.frame.add(panel);
        this.frame.setVisible(true);

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        continueBtn.addActionListener(action -> {
            this.frame.dispose();
            /*AtomicReference<JFrame> lf = new AtomicReference<>();
            Thread t1 = new Thread(() -> {
                lf.set(new JFrame("CTRC"));
                JPanel lp = new JPanel();
                lp.setLayout(null);
                lf.get().setSize(450, 130);
                JLabel ll = new JLabel("Loading resources, please wait...");
                ll.setBounds(10, 10, 380, 30);
                lp.add(ll);
                JProgressBar lpr = new JProgressBar();
                lpr.setIndeterminate(true);
                lpr.setBounds(10, 50, 410, 20);
                lp.add(lpr);
                lf.get().add(lp);
                try {
                    lf.get().setIconImage(Resources.getIcon());
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }
                lf.get().setResizable(false);
                lf.get().setVisible(true);
                lf.get().setLocationRelativeTo(null);
                lf.get().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            });*/
            LoadingScreen ls = null;
            try {
                ls = new LoadingScreen();
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }

            Thread t2 = new Thread(() -> {
                try {
                    String json;
                    try(FileInputStream fis = new FileInputStream(jsonFile.getText())) {
                        json = new String(ByteStreams.toByteArray(fis));
                    }
                    new Editor((JSONObject) new JSONParser().parse(json));
                } catch (IOException | URISyntaxException | ParseException e) {
                    throw new RuntimeException(e);
                }
            });
            t2.start();
            LoadingScreen finalLs = ls;
            new Thread(() -> {
                try {
                    t2.join();
                    finalLs.end();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}
