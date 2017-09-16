package com.lucianms.io;

import org.json.JSONObject;

import java.util.Properties;

/**
 * @author izarooni
 */
public class Config {

    private final Properties properties;

    public Config(JSONObject object) {
        properties = new Properties();
        for (Object key : object.keySet()) {
            properties.put(key, object.get((String) key));
        }
    }

    public JSONObject getJsonObject(String key) {
        return (JSONObject) properties.get(key);
    }

    public String getString(String key) {
        return (String) properties.get(key);
    }

    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }
}
