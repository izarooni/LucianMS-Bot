package com.lucianms.utils;

import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.object.DiscordObject;
import discord4j.core.object.entity.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Entity entity;
    private ConcurrentHashMap<String, ArrayList<CommandUtil>> permissions = new ConcurrentHashMap<>();

    public Permissions(Entity entity) {
        this.entity = entity;
    }

    public void save() {
        String dir = "permissions/" + entity.getClass().getSimpleName();
        if (new File(dir).mkdirs()) {
            LOGGER.info("Created directory {}", dir);
        }
        String file = dir + "/" + entity.getId().asLong() + ".json";

        JSONObject object = new JSONObject();
        JSONObject perms = new JSONObject();
        for (Map.Entry<String, ArrayList<CommandUtil>> entry : permissions.entrySet()) {
            JSONArray array = new JSONArray();
            for (CommandUtil s : entry.getValue()) {
                array.put(s.name());
            }
            perms.put(entry.getKey(), array);
        }
        object.put("permissions", perms);

        String output = object.toString(4);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(output.getBytes());
            fos.flush();
        } catch (IOException e) {
            LOGGER.info("Unable to save permissions for '{}'", entity.getId().asString());
            e.printStackTrace();
        }
    }

    public void load() {
        String dir = "permissions/" + entity.getClass().getSimpleName();
        if (new File(dir).mkdirs()) {
            LOGGER.info("Created directory {}", dir);
        }
        String file = dir + "/" + entity.getId().asString() + ".json";
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
                        give(key, CommandUtil.fromName((String) array.get(i)));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to load permissions for '{}'", entity.getId().asString());
            e.printStackTrace();
        }
    }

    public DiscordObject getEntity() {
        return entity;
    }

    private ArrayList<CommandUtil> getByKey(String key) {
        return permissions.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public boolean contains(String key, CommandUtil permission) {
        return getByKey(key).contains(permission);
    }

    public boolean give(String key, CommandUtil permission) {
        ArrayList<CommandUtil> perms = getByKey(key);
        if (!perms.contains(permission)) {
            return perms.add(permission);
        } else {
            LOGGER.info("{} {} already has permission {} with key {}", entity.getClass().getSimpleName(), entity.getId(), permission, key);
        }
        return false;
    }

    public boolean revoke(String key, CommandUtil permission) {
        return getByKey(key).remove(permission);
    }
}
