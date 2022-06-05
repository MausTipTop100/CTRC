package net.maustiptop100.ctrc.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Editor {

    private final List<Thread> threads;

    private final Map<String, JavaMod.Item> items;

    private final JTabbedPane recipePanes;

    private final List<CraftingGUI> craftingGUIs;

    private final String zsFile;
    private final String mcInstallation;
    private final String modFolder;

    public Editor(String zsFile, String mcInstallation, String modFolder) throws IOException, URISyntaxException {
        this.items = new TreeMap<>();
        this.items.putAll(new JavaMod(Paths.get(mcInstallation)).getItems());

        this.threads = new Vector<>();

        File[] files = Paths.get(modFolder).toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        assert files != null;
        Arrays.stream(files).forEach(file -> threads.add(new Thread(() -> {
            try {
                this.items.putAll(new JavaMod(file.toPath()).getItems());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })));

        this.threads.forEach(Thread::start);
        this.threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.items.forEach((k, v) -> System.out.println(k));

        JFrame frame = new JFrame("CTRC | Editor");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Resources.getIcon());

        this.recipePanes = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        this.craftingGUIs = new Vector<>();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem addItem = new JMenuItem("Add Recipe");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem applyItem = new JMenuItem("Generate ZS File");
        menu.add(addItem);
        menu.add(saveItem);
        menu.add(applyItem);
        menuBar.add(menu);

        addItem.addActionListener(action -> {
            String name = JOptionPane.showInputDialog(null, "Enter recipe name", "CTRC | Add Recipe", JOptionPane.QUESTION_MESSAGE);
            CraftingGUI gui = new CraftingGUI(this.items, name);
            this.craftingGUIs.add(gui);
            this.recipePanes.add(gui, name);
        });

        saveItem.addActionListener(action -> {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("CTRC project files", "ctrc.json", "ctrc", "json"));
            chooser.setAcceptAllFileFilterUsed(false);
            int status = chooser.showSaveDialog(frame);
            if(status == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try(FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(this.getJSON().toJSONString().getBytes(StandardCharsets.UTF_8));
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        applyItem.addActionListener(action -> {
            try(FileOutputStream fos = new FileOutputStream(zsFile)) {
                new PrintStream(fos).print(ZenScriptParser.createZSFile(this.getJSON()));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        });

        frame.setJMenuBar(menuBar);
        frame.add(this.recipePanes);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.zsFile = zsFile;
        this.mcInstallation = mcInstallation;
        this.modFolder = modFolder;
    }

    public Editor(JSONObject json) throws IOException, URISyntaxException {
        this((String) json.get("zs-file"), (String) json.get("mc-installation"), (String) json.get("mod-folder"));
        ((JSONArray)json.get("recipes")).forEach(recipe -> {
            CraftingGUI gui = new CraftingGUI(this.items, (String) ((JSONObject)recipe).get("name"));
            gui.loadJSON((JSONObject) recipe);
            this.craftingGUIs.add(gui);
            this.recipePanes.add(gui, ((JSONObject)recipe).get("name"));
        });
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        json.put("zs-file", this.zsFile);
        json.put("mc-installation", this.mcInstallation);
        json.put("mod-folder", this.modFolder);
        JSONArray recipes = new JSONArray();
        this.craftingGUIs.forEach(gui -> recipes.add(gui.getJSON()));
        json.put("recipes", recipes);
        return json;
    }

    public static class CraftingGUI extends JPanel {

        private final JComboBox<String> recItemSelection;
        private final JLabel recItemLabel;
        private final JComboBox<String> itemSelection;
        private final JLabel itemLabel;
        private final JCheckBox shaped;
        private final JLabel arrow;
        private final CraftingPanelElement product;
        private final JComboBox<Integer> amount;
        private final Map<String, JavaMod.Item> items;

        private final String recipeName;

        private CraftingPanel panel;

        public CraftingGUI(Map<String, JavaMod.Item> items, String recipeName) {
            this.items = items;
            this.recipeName = recipeName;
            this.product = new CraftingPanelElement();
            this.product.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            this.amount = new JComboBox<>();

            for(int i = 1; i <= 64; i++) this.amount.addItem(i);

            this.recItemLabel = new JLabel();
            this.recItemSelection = new JComboBox<>();
            this.recItemSelection.setEditable(true);
            this.recItemSelection.addActionListener(action -> {
                //System.out.println(this.recItemSelection.getSelectedItem());
                try {
                    JavaMod.Item item = items.get(Objects.requireNonNull(this.recItemSelection.getSelectedItem()).toString());
                    this.recItemLabel.setIcon(new ImageIcon(item.getImage()));
                    this.product.setItem(item);
                } catch(NullPointerException e) {
                    // do nothing
                }
            });
            items.forEach((k, v) -> this.recItemSelection.addItem(k));

            this.itemLabel = new JLabel();
            this.itemSelection = new JComboBox<>();
            this.itemSelection.setEditable(true);
            this.itemSelection.addActionListener(action -> {
                //System.out.println(this.itemSelection.getSelectedItem());
                JavaMod.Item item;
                try {
                    if(Objects.requireNonNull(this.itemSelection.getSelectedItem()).toString().equals("minecraft:air")) {
                        item = new JavaMod.Item("minecraft:air", null);
                        //System.out.println("Selected minecraft:air");
                    } else item = items.get(Objects.requireNonNull(this.itemSelection.getSelectedItem()).toString());
                    this.itemLabel.setIcon(new ImageIcon(item.getImage()));
                    this.panel.selectItem(item);
                } catch(NullPointerException e) {
                    // do nothing
                }
            });
            items.forEach((k, v) -> this.itemSelection.addItem(k));

            this.shaped = new JCheckBox("Shaped");

            this.panel = new CraftingPanel();

            this.arrow = new JLabel("=>");

            this.add(this.recItemSelection);
            this.add(this.recItemLabel);
            this.add(this.itemSelection);
            this.add(this.itemLabel);
            this.add(this.shaped);
            this.add(this.panel);
            this.add(this.arrow);

            this.product.add(this.amount);
            this.add(this.product);
        }

        @Override
        public void paintComponent(Graphics g) {
            this.setLayout(null);
            this.recItemSelection.setBounds(10, 10, this.getWidth()-40, 20);
            this.recItemLabel.setBounds(this.getWidth()-30, 10, 20, 20);
            this.itemSelection.setBounds(10, 40, this.getWidth()-40, 20);
            this.itemLabel.setBounds(this.getWidth()-30, 40, 20, 20);
            this.shaped.setBounds(10, 70, this.getWidth()-20, 20);
            this.panel.setBounds(10, 100, (this.getWidth()-20)/2, this.getHeight()-20);

            int l = (this.panel.getWidth() / 3) - 20;

            this.arrow.setBounds(this.panel.getWidth() + 30, 80 + (this.panel.getWidth()/3) + 10, l, l);
            this.arrow.setFont(new Font(this.arrow.getFont().getName(), Font.PLAIN, l/2));
            this.product.setBounds(this.panel.getWidth() + 30 + l, 80 + (this.panel.getWidth()/3) + 10, l, l);

            this.amount.setBounds(0, 0, 40, 20);

            super.paintComponent(g);
        }

        public JSONObject getJSON() {
            JSONObject json = new JSONObject();
            json.put("name", this.recipeName);
            json.put("shaped", this.shaped.isSelected());
            json.put("product", Objects.requireNonNull(this.recItemSelection.getSelectedItem()).toString());
            json.put("amount", this.amount.getSelectedItem());
            json.put("recipe", this.panel.getJSON());
            return json;
        }

        public void loadJSON(JSONObject json) {
            this.shaped.setSelected((boolean) json.get("shaped"));
            this.recItemSelection.setSelectedItem(json.get("product"));
            this.amount.setSelectedIndex((int) ((Long)json.get("amount")-1));
            this.panel.loadJSON((JSONArray) json.get("recipe"), this.items);
        }
    }

    public static class CraftingPanel extends JPanel {

        private JavaMod.Item selectedItem;
        private final CraftingPanelElement[][] elements = new CraftingPanelElement[3][3];

        public CraftingPanel() {
            this.selectedItem = new JavaMod.Item("minecraft:air", null);

            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    elements[i][j] = new CraftingPanelElement();
                    int finalI = i;
                    int finalJ = j;
                    elements[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            //System.out.println(e.getButton());
                            if(e.getButton() == MouseEvent.BUTTON1) {
                                elements[finalI][finalJ].setItem(selectedItem);
                                //System.out.printf("%d | %d => %s\n", finalI, finalJ, selectedItem.getName());
                            } else {
                                elements[finalI][finalJ].setItem(new JavaMod.Item("minecraft:air", null));
                                //System.out.printf("%d | %d => %s\n", finalI, finalJ, "removed");
                            }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                        }
                    });
                    this.add(this.elements[i][j]);
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            //System.out.println("Repainting...");
            //System.out.println("Width: " + this.getWidth());
            this.setLayout(null);
            int l = (this.getWidth() / 3) - 20;
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    elements[i][j].setBounds(i*l + 10, j*l + 10, l, l);
                    elements[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                }
            }
            super.paintComponent(g);
        }

        public void selectItem(JavaMod.Item item) {
            //System.out.println("item = " + item.getName());
            this.selectedItem = item;
        }

        public JSONArray getJSON() {
            JSONArray parentArray = new JSONArray();
            for(int i = 0; i < 3; i++) {
                JSONArray array = new JSONArray();
                for(int j = 0; j < 3; j++) {
                    array.add(elements[j][i].getItem().getFullName());
                }
                parentArray.add(array);
            }
            return parentArray;
        }

        public void loadJSON(JSONArray json, Map<String, JavaMod.Item> items) {
            for(int i = 0; i < 3; i++) {
                JSONArray childArray = (JSONArray) json.get(i);
                for(int j = 0; j < 3; j++) elements[j][i].setItem(items.get((String) childArray.get(j)));
            }
        }
    }

    public static class CraftingPanelElement extends JPanel {

        private JavaMod.Item item = new JavaMod.Item("minecraft:air", null);

        public CraftingPanelElement() {
            this.setLayout(null);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                g.drawImage(item.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
            } catch(NullPointerException e) {
                g.drawString(item.getName(), 0, 0);
            }
            this.repaint();
        }

        public void setItem(JavaMod.Item item) {
            this.item = item;
        }

        public JavaMod.Item getItem() {
            return this.item;
        }
    }
}
