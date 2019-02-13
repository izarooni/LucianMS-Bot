package test;

import com.lucianms.io.Defaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author izarooni
 */
public class Tester {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tester.class);

    public static void main(String[] args) throws IOException {
        if (Defaults.tryCreateDefault("", "config.ini")) {
            LOGGER.info("Created config file");
        } else {
            LOGGER.info("Config file already exists");
        }
    }
}
