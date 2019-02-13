package com.lucianms.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author izarooni
 */
public final class Defaults {

    private static final Logger LOGGER = LoggerFactory.getLogger(Defaults.class);

    private Defaults() {
    }


    public static void createDefault(String path, String fileName) throws IOException {
        if (new File(path + fileName).exists()) {
            LOGGER.info("Default file {} in directory {} already exists", fileName, path);
            return;
        }

        InputStream res = Defaults.class.getResourceAsStream("/" + fileName);
        if (res == null) {
            throw new FileNotFoundException("/" + fileName);
        }
        FileUtils.copyToFile(res, new File(path + fileName));
        LOGGER.info("Created new default file {} in directory {}", fileName, path);
    }
}
