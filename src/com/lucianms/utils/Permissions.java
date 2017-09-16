package com.lucianms.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IDiscordObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class Permissions {

    private static final Logger LOGGER = LoggerFactory.getLogger(Permissions.class);

    private final IDiscordObject object;
    private ConcurrentHashMap<Long, ArrayList<String>> permissions = new ConcurrentHashMap<>();

    public Permissions(IDiscordObject object) {
        this.object = object;
    }

    public void save() {
        String dir = "permissions/" + this.object.getClass().getSimpleName();
        if (new File(dir).mkdirs()) {
            LOGGER.info("Created directory {}", dir);
        }
        String file = dir + "/" + this.object.getLongID() + ".json";

        JSONObject object = new JSONObject();
        JSONObject perms = new JSONObject();
        for (Map.Entry<Long, ArrayList<String>> entry : permissions.entrySet()) {
            JSONArray array = new JSONArray();
            for (String s : entry.getValue()) {
                array.put(s);
            }
            perms.put(entry.getKey().toString(), array);
        }
        object.put("permissions", perms);

        String output = object.toString(4);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(output.getBytes());
            fos.flush();
        } catch (IOException e) {
            LOGGER.info("Unable to save permissions for '{}'", this.object.getLongID());
            e.printStackTrace();
        }
    }

    public void load() {
        String dir = "permissions/" + this.object.getClass().getSimpleName();
        if (new File(dir).mkdirs()) {
            LOGGER.info("Created directory {}", dir);
        }
        String file = dir + "/" + this.object.getLongID() + ".json";
        try {
            File p = new File(file);
            if (!p.exists()) {
                return;
            }

            try (FileReader reader = new FileReader(file)) {
                JSONObject object = new JSONObject(new JSONTokener(reader));
                JSONObject perms = (JSONObject) object.get("permissions");
                Iterator iter = perms.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    JSONArray array = (JSONArray) perms.get(key);
                    for (int i = 0; i < array.length(); i++) {
                        give(Long.parseLong(key), (String) array.get(i));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to load permissions for '{}'", this.object.getLongID());
            e.printStackTrace();
        }
    }

    public IDiscordObject getObject() {
        return object;
    }

    private ArrayList<String> getByKey(Long key) {
        permissions.putIfAbsent(key, new ArrayList<>());
        return permissions.get(key);
    }

    public boolean contains(Long key, String permission) {
        return getByKey(key).contains(permission);
    }

    public boolean give(Long key, String permission) {
        ArrayList<String> perms = getByKey(key);
        if (!perms.contains(permission)) {
            return perms.add(permission);
        } else {
            LOGGER.warn("'{}' {} already has permission {} in key", object.getClass().getSimpleName(), object.getLongID(), permission, key);
        }
        return false;
    }

    public boolean revoke(Long key, String permission) {
        return getByKey(key).remove(permission);
    }
}
