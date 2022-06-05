package net.maustiptop100.ctrc.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class JavaMod {

    private final Path path;

    private final HashMap<String, Item> items;

    private Image catchImage(String path)
            throws IOException
    {
        AtomicReference<Image> reference = new AtomicReference<>(null);

        new ZIP(this.path).get(path, bytes -> {
            try {
                JSONObject json = (JSONObject) new JSONParser().parse(new String(bytes));
                if(!json.containsKey("parent")) return;
                if(json.containsKey("textures")) {
                    JSONObject textures = (JSONObject) json.get("textures");
                    if (textures.containsKey("layer0")) {
                        String imgPath;
                        if(((String) textures.get("layer0")).split(":").length < 2) {
                            imgPath = ("assets/minecraft/textures/" + textures.get("layer0") + ".png");
                        } else {
                            imgPath = ("assets/" + ((String) textures.get("layer0")).split(":")[0] + "/textures/" + ((String) textures.get("layer0")).split(":")[1] + ".png");
                        }
                        System.out.println(imgPath);
                        new ZIP(this.path).get(imgPath, imgData -> {
                            try {
                                reference.set(ImageIO.read(new ByteArrayInputStream(imgData)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } else if (!json.get("parent").equals("minecraft:item/generated")) {
                    String parentPath;
                    if(((String) json.get("parent")).split(":").length < 2) {
                        parentPath = ("assets/minecraft/models/" + json.get("parent") + ".json");
                    } else {
                        parentPath = ("assets/" + ((String) json.get("parent")).split(":")[0] + "/models/" + ((String) json.get("parent")).split(":")[1] + ".json");
                    }
                        new ZIP(this.path).get(parentPath, content -> {
                            try {
                                JSONObject parentJSON = (JSONObject) new JSONParser().parse(new String(content));
                                if (parentJSON.containsKey("textures")) {
                                    JSONObject textures = (JSONObject) parentJSON.get("textures");
                                    String firstKey = (String) textures.keySet().stream().findFirst().get();
                                    if (textures.containsKey(firstKey)) {
                                        String imgPath;
                                        if (((String) textures.get(firstKey)).split(":").length < 2) {
                                            imgPath = ("assets/minecraft/textures/" + textures.get(firstKey) + ".png");
                                        } else {
                                            imgPath = ("assets/" + ((String) textures.get(firstKey)).split(":")[0] + "/textures/" + ((String) textures.get(firstKey)).split(":")[1] + ".png");
                                        }
                                        System.out.println(imgPath);
                                            new ZIP(this.path).get(imgPath, imgData -> {
                                                try {
                                                    reference.set(ImageIO.read(new ByteArrayInputStream(imgData)));
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            });
                                    } else {
                                        throw new RuntimeException("Texture doesnt exists");
                                    }
                                }
                            } catch (ParseException | IOException e) {
                                System.err.println("Invalid texture: " + path);
                            }
                        });
                }
            } catch (ParseException | IOException | ClassCastException  e) {
                System.err.println("Invalid texture: " + path);
            }
        });

        return reference.get();
    }

    private void init() throws IOException {
        new ZIP(this.path).forEach(entry -> {
            if(entry.getName().startsWith("assets/") && entry.getName().contains("/models/item/") && entry.getName().split("/models/item/").length > 1) {
                String itemName = entry.getName().replace("assets/", "").replace("/models/item/", ":").replace(".json", "");
                Image image;
                System.out.print(itemName + " -> ");
                try {
                    image = this.catchImage(entry.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                items.put(itemName, new Item(itemName, image));
            }
        });
    }

    public JavaMod(Path path) throws IOException {
        this.items = new HashMap<>();
        this.path = path;
        this.init();
    }

    public HashMap<String, Item> getItems() {
        return this.items;
    }

    public static class Item {
        private final String namespace;
        private final String name;
        private final Image img;

        public Item(String fullName, Image img) {
            this.namespace = fullName.split(":")[0];
            this.name = fullName.split(":")[1];
            this.img = img;
        }

        public Item(String namespace, String name, Image img) {
            this.namespace = namespace;
            this.name = name;
            this.img = img;
        }

        public String getNamespace() {
            return this.namespace;
        }

        public String getName() {
            return this.name;
        }

        public Image getImage() {
            return this.img;
        }

        public String getFullName() {
            return this.namespace + ":" + this.name;
        }
    }

}
