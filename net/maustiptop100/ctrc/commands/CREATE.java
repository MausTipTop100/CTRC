package net.maustiptop100.ctrc.commands;

import net.maustiptop100.ctrc.util.Editor;
import net.maustiptop100.ctrc.util.LoadingScreen;
import net.maustiptop100.ctrc.util.Resources;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.net.URISyntaxException;

public class CREATE implements CommandExecutor {

    private JFrame frame;

    @Override
    public void runCommand(String[] args) {
        this.frame = new JFrame("CTRC | Create");
        this.frame.setSize(600, 300);
        this.frame.setResizable(false);
        try {
            this.frame.setIconImage(Resources.getIcon());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton continueBtn = new JButton("Continue");
        continueBtn.setBounds(450, 210, 100, 30);

        JLabel zsLabel = new JLabel("ZS-File Path");
        panel.add(zsLabel);
        zsLabel.setBounds(20, 30, 530, 20);
        JTextField zsFile = new JTextField();
        zsFile.setBounds(20, 50, 500, 20);
        panel.add(zsFile);
        JButton zsFileChooserBtn = new JButton("...");
        zsFileChooserBtn.setBounds(530, 50, 30, 20);
        panel.add(zsFileChooserBtn);

        zsFileChooserBtn.addActionListener(action -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("ZS-Files", "zs"));
            int status = chooser.showOpenDialog(this.frame);
            if(status == JFileChooser.APPROVE_OPTION) {
                zsFile.setText(chooser.getSelectedFile().getPath());
            }
        });

        JLabel mcJarLabel = new JLabel("Minecraft Version File");
        panel.add(mcJarLabel);
        mcJarLabel.setBounds(20, 70, 530, 20);
        JTextField mcJarFile = new JTextField();
        mcJarFile.setBounds(20, 90, 500, 20);
        panel.add(mcJarFile);
        JButton mcJarFileChooserBtn = new JButton("...");
        mcJarFileChooserBtn.setBounds(530, 90, 30, 20);
        panel.add(mcJarFileChooserBtn);

        mcJarFileChooserBtn.addActionListener(action -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("Executable JAR Files", "jar"));
            int status = chooser.showOpenDialog(this.frame);
            if(status == JFileChooser.APPROVE_OPTION) {
                mcJarFile.setText(chooser.getSelectedFile().getPath());
            }
        });

        JLabel modFolderLabel = new JLabel("Minecraft Mod Folder");
        panel.add(modFolderLabel);
        modFolderLabel.setBounds(20, 110, 530, 20);
        JTextField modFolder = new JTextField();
        modFolder.setBounds(20, 130, 500, 20);
        panel.add(modFolder);
        JButton modFolderFileChooserBtn = new JButton("...");
        modFolderFileChooserBtn.setBounds(530, 130, 30, 20);
        panel.add(modFolderFileChooserBtn);

        modFolderFileChooserBtn.addActionListener(action -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int status = chooser.showOpenDialog(this.frame);
            if(status == JFileChooser.APPROVE_OPTION) {
                modFolderFileChooserBtn.setText(chooser.getSelectedFile().getPath());
            }
        });

        panel.add(continueBtn);
        this.frame.add(panel);
        this.frame.setVisible(true);
        this.frame.setLocationRelativeTo(null);

        continueBtn.addActionListener(action -> {

            this.frame.dispose();

            LoadingScreen ls = null;
            try {
                ls = new LoadingScreen();
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }

            Thread t2 = new Thread(() -> {
                try {
                    new Editor(zsFile.getText(), mcJarFile.getText(), modFolder.getText());
                } catch (IOException | URISyntaxException e) {
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
