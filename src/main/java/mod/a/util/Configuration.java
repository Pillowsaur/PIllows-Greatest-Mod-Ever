package mod.a.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Configuration {
    private JsonObject config;
    private final File file = new File(Loader.instance().getConfigDir(), "testmod.json");

    public Configuration() {
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (file.createNewFile()) {
                config = new JsonObject();
            } else {
                config = new JsonParser().parse(FileUtils.readFileToString(file)).getAsJsonObject();
            }
        } catch (Exception e) {
            config = new JsonObject();
        }


        APIHelper.apiKey = initStr("apiKey", "");

        saveConfig();
    }

    public boolean initBoolean(String name, boolean defaultValue) {
        if (!this.config.has(name)) {
            this.config.addProperty(name, defaultValue);
            return defaultValue;
        } else {
            return this.config.get(name).getAsBoolean();
        }
    }

    public int initInt(String name, int defaultValue) {
        if (!this.config.has(name)) {
            this.config.addProperty(name, defaultValue);
            return defaultValue;
        } else {
            return this.config.get(name).getAsInt();
        }
    }

    public char initChar(String name, char defaultValue) {
        if (!this.config.has(name)) {
            this.config.addProperty(name, defaultValue);
            return defaultValue;
        } else {
            return this.config.get(name).getAsCharacter();
        }
    }

    public String initStr(String name, String defaultValue) {
        if (!this.config.has(name)) {
            this.config.addProperty(name, defaultValue);
            return defaultValue;
        } else {
            return this.config.get(name).getAsString();
        }
    }

    public String[] initArr(String name, String[] defaultValue) {
        if (!this.config.has(name)) {
            setValue(name, defaultValue);
            return defaultValue;
        } else {
            JsonArray arr = this.config.get(name).getAsJsonArray();
            String[] toReturn = new String[arr.size()];

            for (int i = 0; i < arr.size(); i++) {
                toReturn[i] = arr.get(i).getAsString();
            }
            return toReturn;
        }
    }

    public HashSet<String> initStrSet(String name, HashSet<String> defaultValue) {
        if (!this.config.has(name)) {
            setValue(name, defaultValue);
            return defaultValue;
        } else {
            JsonArray arr = this.config.get(name).getAsJsonArray();
            HashSet<String> toReturn = new HashSet<>();

            for (int i = 0; i < arr.size(); i++) {
                toReturn.add(arr.get(i).getAsString());
            }

            return toReturn;
        }
    }

    public ArrayList<String> initStrArr(String name, ArrayList<String> defaultValue) {
        if (!this.config.has(name)) {
            setValue(name, defaultValue);
            return defaultValue;
        } else {
            JsonArray arr = this.config.get(name).getAsJsonArray();
            ArrayList<String> toReturn = new ArrayList<>();

            for (int i = 0; i < arr.size(); i++) {
                toReturn.add(arr.get(i).getAsString());
            }

            return toReturn;
        }
    }

    public void setValue(String name, boolean value) {
        this.config.addProperty(name, value);
        saveConfig();
    }

    public void setValue(String name, int value) {
        this.config.addProperty(name, value);
        saveConfig();
    }

    public void setValue(String name, char value) {
        this.config.addProperty(name, value);
        saveConfig();
    }

    public void setValue(String name, String value) {
        this.config.addProperty(name, value);
        saveConfig();
    }

    public void setValue(String name, String[] value) {
        JsonArray arr = new JsonArray();
        for (String i : value) {
            arr.add(new JsonPrimitive(i));
        }

        this.config.add(name, arr);
        saveConfig();
    }

    public void setValue(String name, HashSet<String> value) {
        JsonArray arr = new JsonArray();
        for (String i : value) {
            arr.add(new JsonPrimitive(i));
        }

        this.config.add(name, arr);
        saveConfig();
    }

    public void setValue(String name, ArrayList<String> value) {
        JsonArray arr = new JsonArray();
        for (String i : value) {
            arr.add(new JsonPrimitive(i));
        }

        this.config.add(name, arr);
        saveConfig();
    }

    public void saveConfig() {
        try {
            FileUtils.writeStringToFile(file, this.config.toString(), "utf-8");
        } catch (IOException e) {
            System.out.println("Failed to save configuration.");
        }
    }
}