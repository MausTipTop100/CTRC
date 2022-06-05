package net.maustiptop100.ctrc.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ZenScriptParser {

    public static String createZSFile(JSONObject object) {
        StringBuilder sb = new StringBuilder();
        sb.append("/*\n" +
                "*  AUTO GENERATED FILE BY CTRC 1.0\n" +
                "*\n" +
                "*  https://github.io/ctrc\n" +
                "*/\n\n");

        ((JSONArray) object.get("recipes")).forEach(recipe -> sb.append(getRecipeString((JSONObject) recipe)).append("\n\n"));

        return sb.toString();
    }

    public static String getRecipeString(JSONObject object) {
        StringBuilder sb = new StringBuilder();
        sb.append("craftingTable.add");
        if((boolean) object.get("shaped")) {
            sb.append("Shaped(\"");
            sb.append((String) object.get("name"));
            sb.append("\", <item:");
            sb.append((String) object.get("product"));
            sb.append("> * ");
            sb.append(object.get("amount"));
            sb.append(", ");
            sb.append(getRawShapedRecipeString((JSONArray) object.get("recipe")));
            sb.append(");");
        } else {
            sb.append("Shapeless(\"");
            sb.append((String) object.get("name"));
            sb.append("\", <item:");
            sb.append((String) object.get("product"));
            sb.append("> * ");
            sb.append(object.get("amount"));
            sb.append(", ");
            sb.append(getRawShapelessRecipeString((JSONArray) object.get("recipe")));
            sb.append(");");
        }
        return sb.toString();
    }

    public static String getRawShapelessRecipeString(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        array.forEach(child -> ((JSONArray)child).forEach(item -> {
            if(!item.equals("minecraft:air")) {
                sb.append("<item:");
                sb.append(item);
                sb.append(">, ");
            }
        }));
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    public static String getRawShapedRecipeString(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        array.forEach(child -> {
            if(!((JSONArray)child).stream().allMatch(str -> str.equals("minecraft:air"))) {
                sb.append("[");
                for(int i = 0; i < ((JSONArray)child).size(); i++)
                    if(!(i == ((JSONArray)child).size()-1 && ((JSONArray)child).get(i).equals("minecraft:air")))
                        sb.append("<item:").append(((JSONArray)child).get(i)).append(">, ");
                sb.setLength(sb.length() - 2);
                sb.append("], ");
            }
        });
        sb.setLength(sb.length()-2);
        sb.append("]");
        return sb.toString();
    }
}
